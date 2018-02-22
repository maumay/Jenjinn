/**
 *
 */
package jenjinn.engine.gametree;

import static io.xyz.chains.utilities.CollectionUtil.len;
import static io.xyz.chains.utilities.PrimitiveUtil.max;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImplV2;
import jenjinn.engine.enums.Infinity;
import jenjinn.engine.enums.TerminationType;
import jenjinn.engine.evaluation.BoardEvaluator;
import jenjinn.engine.evaluation.componentimpl.KingSafetyV1;
import jenjinn.engine.evaluation.componentimpl.MobilityV1;
import jenjinn.engine.evaluation.componentimpl.PawnStructureV1;
import jenjinn.engine.exceptions.AmbiguousPgnException;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.openingdatabase.AlgebraicCommand;
import jenjinn.io.pgnutils.ChessGameReader;

/**
 * @author t
 *
 */
public class TTAlphaBetaV1_2 implements MoveCalculator
{
	private static final int QUIESCENCE_DEPTH_CAP = 30;

	private static final int DEFAULT_TABLE_SIZE = 17;

	private static final String DESCRIPTOR = "[NegaAlphaBeta - no pv override 1 bucket tt - pv extraction - tt impl v1_2]";

	/**
	 * The heuristic position evaluator. It performs a quiescence search to make
	 * sure only quiet positions are evaluated to avoid overlooking any nasty
	 * tactics.
	 */
	private Quiescence quiescence;

	/**
	 * The transposition table this search algorithm will use.
	 */
	private TranspositionTable tt;

	/**
	 * Depth we will search at. Default to no max depth, i.e the search is just time
	 * limited.
	 */
	private int maxSearchDepth = 100;

	private int bestFirstMoveIndex = -1;

	public TTAlphaBetaV1_2(final BoardEvaluator eval)
	{
		this.quiescence = new Quiescence(eval);
		this.tt = TranspositionTable.create(DEFAULT_TABLE_SIZE);
	}

	public TTAlphaBetaV1_2()
	{
		this.tt = TranspositionTable.create(DEFAULT_TABLE_SIZE);
	}

	public TTAlphaBetaV1_2(final int tableSize)
	{
		this.tt = TranspositionTable.create(tableSize);
	}

	@Override
	public ChessMove getBestMoveFrom(final BoardState root)
	{
		bestFirstMoveIndex = -1;
		ChessMove bestMove;
		try {
			bestMove = getBestMoveFrom(root, 1, false);
		} catch (final InterruptedException e1) {
			// We should not be getting here
			e1.printStackTrace();
			throw new AssertionError();
		}
		for (int depth = 2; depth <= maxSearchDepth; depth++) {
			try {
				final ChessMove newBestMove = getBestMoveFrom(root, depth, true);
				bestMove = newBestMove;
			} catch (final InterruptedException e) {
				// Restore interrupted status
				Thread.interrupted();
				break;
			}
		}
		System.out.println("Pawn table used: " + PawnStructureV1.usedTable);
		System.out.println("Pawn table not used: " + PawnStructureV1.notUsedTable);
		return bestMove;
	}

	@Override
	public void setSearchDepth(final int depth)
	{
		maxSearchDepth = depth;
	}

	@Override
	public void setEvaluator(final BoardEvaluator evaluator)
	{
		throw new RuntimeException("NYI");
		// eval = evaluator;
	}

	@Override
	public String getDescriptor()
	{
		return DESCRIPTOR;
	}

	private int[] getPrincipalVariation(final BoardState root, final int depth)
	{
		final int[] pv = new int[max(0, depth - 1)];
		int count = 0;
		if (bestFirstMoveIndex > -1) {
			pv[count++] = bestFirstMoveIndex;
			List<ChessMove> mvs = root.getMoves();
			BoardState state = mvs.get(bestFirstMoveIndex).evolve(root);
			TableEntry entry;
			while ((entry = tt.get(state.getHashing())) != null && entry.getType() == TreeNodeType.PV
					&& entry.getPositionHash() == state.getHashing() && len(pv) < depth - 1) {
				pv[count++] = entry.getMoveIndex();
				mvs = state.getMoves();
				state = mvs.get(entry.getMoveIndex()).evolve(state);
			}
		}
		return pv;
	}

	private ChessMove getBestMoveFrom(final BoardState root, final int depth, final boolean interruptionAllowed)
			throws InterruptedException
	{
		System.out.println("Starting search of DEPTH: " + depth);
		// Initialise variables
		int bestMoveIndex = -1;
		int alpha = Infinity.IC_ALPHA; // Here alpha is the calculated value of our best move.
		final int[] pv = getPrincipalVariation(root, depth);
		final List<ChessMove> possibleMoves = root.getMoves();

		if (len(pv) != depth - 1) {
			System.out.println("Expected pv of length: " + (depth - 1) + " but got " + len(pv));
		}

		final int[] indices = IntStream.range(0, possibleMoves.size()).toArray();

		if (depth > 1) {
			assert len(pv) > 0;
			changeFirstIndex(indices, pv[0]);
		}

		for (final int idx : indices) {
			final ChessMove mv = possibleMoves.get(idx);
			final int bestBlackReply = -negamax(mv.evolve(root), -Infinity.IC_BETA, -alpha, depth - 1,
					interruptionAllowed);

			// We want to maximise the value of best opponent reply
			if (bestBlackReply > alpha) {
				alpha = bestBlackReply;
				bestMoveIndex = idx;
			}
		}
		bestFirstMoveIndex = bestMoveIndex;
		return possibleMoves.get(bestMoveIndex);
	}

	/**
	 * WIKI impl
	 *
	 * @param root
	 * @param alpha
	 * @param beta
	 * @param depth
	 * @return
	 */
	public int negamax(final BoardState root, int alpha, int beta, final int depth, final boolean interruptionAllowed)
			throws InterruptedException
	{
		if (interruptionAllowed && Thread.currentThread().isInterrupted()) {
			throw new InterruptedException();
		}

		final int alphaOrig = alpha;
		final long rootHash = root.getHashing();

		final TableEntry ttEntry = tt.get(rootHash);
		int recommendedMoveIndex = -1;

		if (entryIsValid(rootHash, ttEntry)) {
			if (ttEntry.getDepthSearched() >= depth) {
				switch (ttEntry.getType()) {
				case PV:
					return ttEntry.getScore();
				case CUT:
					alpha = Math.max(alpha, ttEntry.getScore());
					break;
				case ALL:
					beta = Math.min(beta, ttEntry.getScore());
					break;
				default:
					throw new AssertionError();
				}
				// If this isn't true then we need more information to get accurate calculation
				if (alpha >= beta) {
					return ttEntry.getScore();
				}
			}
			recommendedMoveIndex = ttEntry.getMoveIndex();
		}

		/* Not sure abou tthis termination bit */
		final TerminationType tState = root.getTerminationState();
		if (tState != TerminationType.NOT_TERMINAL) {
			return root.getFriendlySide().orientation() * tState.value;
		}

		if (depth == 0) {
			Quiescence.currentDepth = 0;
			// We quiesce with new window constraints, think this is more stable as I was
			// getting weird buggy cutoffs
			return quiescence.search(root, Infinity.IC_ALPHA, Infinity.IC_BETA, QUIESCENCE_DEPTH_CAP,
					interruptionAllowed);
		}

		int bestValue = -Infinity.INT_INFINITY;
		int bestMoveIndex = recommendedMoveIndex, refutationMoveIndex = -1;

		final List<ChessMove> possibleMoves = root.getMoves();
		final int[] indices = IntStream.range(0, possibleMoves.size()).toArray();

		changeFirstIndex(indices, recommendedMoveIndex);

		for (final int i : indices) {
			final ChessMove mv = possibleMoves.get(i);
			final int bestReply = -negamax(mv.evolve(root), -beta, -alpha, depth - 1, interruptionAllowed);

			final int oldBestValue = bestValue;
			bestValue = Math.max(bestValue, bestReply);
			bestMoveIndex = oldBestValue != bestValue ? i : bestMoveIndex;

			alpha = Math.max(alpha, bestValue);

			if (alpha >= beta) {
				refutationMoveIndex = i;
				break;
			}
		}

		TableEntry potentialNewEntry;
		if (bestValue <= alphaOrig) // ALL node
		{
			potentialNewEntry = TableEntry.generateALL(rootHash, bestValue, depth);
		} else if (bestValue >= beta) // CUT node
		{
			assert refutationMoveIndex != -1;
			potentialNewEntry = TableEntry.generateCUT(rootHash, bestValue, refutationMoveIndex, depth);
		} else // PV node
		{
			assert bestMoveIndex != -1;
			potentialNewEntry = TableEntry.generatePV(rootHash, bestValue, bestMoveIndex, depth);
		}
		processTableReplacement(potentialNewEntry, ttEntry);

		return bestValue;
	}

	private void changeFirstIndex(final int[] indices, final int recommendedMoveIndex)
	{
		if (recommendedMoveIndex > -1) {
			final int tmp = indices[0];
			indices[0] = indices[recommendedMoveIndex];
			indices[recommendedMoveIndex] = tmp;
		}
	}

	private void processTableReplacement(final TableEntry newEntry, final TableEntry oldEntry)
	{
		if (oldEntry == null) {
			tt.set(newEntry);
		} else if (newEntry.getType() == TreeNodeType.PV && oldEntry.getType() != TreeNodeType.PV) {
			// System.out.println("SET PV NODE!");
			tt.set(newEntry);
		} else if (newEntry.getType() != TreeNodeType.PV && oldEntry.getType() == TreeNodeType.PV) {
			return;
		} else {
			tt.set(newEntry);
		}
	}

	private boolean entryIsValid(final long nodeHash, final TableEntry entry)
	{
		return entry != null && entry.getPositionHash() == nodeHash;
	}

	static volatile ChessMove m;

	public static void main(final String[] args) throws IOException, AmbiguousPgnException
	{
		// final BoardState state = BoardStateImplV2.getStartBoard();
		final BoardEvaluator eval = new BoardEvaluator(
				Arrays.asList(new KingSafetyV1(), new MobilityV1(), new PawnStructureV1()));
		final TTAlphaBetaV1_2 c = new TTAlphaBetaV1_2(eval);
		// final NegaAlphaBeta d = new NegaAlphaBeta(eval);

		final List<BigInteger> times = new ArrayList<>();
		// System.out.println(d.getBestMoveFrom(state, 6));
		final BufferedReader br = Files.newBufferedReader(Paths.get("positionproviders", "carlsenprovider.txt"));
		br.readLine();
		for (int i = 0; i < 20; i++) {
			if (i == 0) {
				br.readLine();
			}
			System.out.println("Next");
			c.tt.clear();
			final AlgebraicCommand[] gComs = ChessGameReader.processSequenceOfCommands(br.readLine().trim());
			BoardState state = BoardStateImplV2.getStartBoard();
			for (int j = 0; j < Math.min(20, gComs.length); j++) {
				state = state.generateMove(gComs[j]).evolve(state);
			}
			assert state.getTerminationState() == TerminationType.NOT_TERMINAL;
			final long t = System.nanoTime();
			m = c.getBestMoveFrom(state);
			times.add(BigInteger.valueOf(System.nanoTime() - t));
		}

		System.out.println(times.stream().mapToLong(x -> x.longValueExact()).sum() / 20);
	}

}
