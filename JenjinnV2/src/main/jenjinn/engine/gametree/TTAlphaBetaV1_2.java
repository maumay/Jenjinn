/**
 *
 */
package jenjinn.engine.gametree;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImplV2;
import jenjinn.engine.enums.Infinity;
import jenjinn.engine.enums.TerminationType;
import jenjinn.engine.evaluation.BoardEvaluator;
import jenjinn.engine.evaluation.componentimpl.KingSafetyV1;
import jenjinn.engine.evaluation.componentimpl.MobilityV1;
import jenjinn.engine.evaluation.componentimpl.PawnStructureV1;
import jenjinn.engine.moves.ChessMove;

/**
 * @author t
 *
 */
public class TTAlphaBetaV1_2 implements MoveCalculator
{
	private static final int DEFAULT_TABLE_SIZE = 20;

	private static final String DESCRIPTOR = "[NegaAlphaBeta - no pv override 1 bucket tt - pv extraction - tt impl v1_2]";

	/**
	 * Only use a nega evaluator, i.e one that is signed depending
	 * on whether white or black is to move.
	 */
	// private BoardEvaluator eval;
	private Quiescence quiescence;

	/**
	 * The transposition table this search algorithm will use.
	 */
	private TranspositionTable tt;

	/**
	 * Depth we will search at.
	 */
	private int searchDepth = 5;

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
	public ChessMove getBestMove(final BoardState root)
	{
		tt.clear();
		bestFirstMoveIndex = -1;
		ChessMove bestMove = null;

		for (int depth = 1; depth <= searchDepth; depth++)
		{
			bestMove = getBestMoveFrom(root, depth);
		}

		return bestMove;
	}

	@Override
	public void setSearchDepth(final int depth)
	{
		searchDepth = depth;
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

	private TIntList getPrincipalVariation(final BoardState root)
	{
		final TIntList pv = new TIntArrayList();

		if (bestFirstMoveIndex > -1)
		{
			pv.add(bestFirstMoveIndex);
			List<ChessMove> mvs = root.getMoves();
			BoardState state = mvs.get(bestFirstMoveIndex).evolve(root);
			TableEntry entry;
			while ((entry = tt.get(state.getHashing())) != null && entry.getPositionHash() == state.getHashing())
			{
				assert entry.getType() == TreeNodeType.PV;
				pv.add(entry.getMoveIndex());
				mvs = state.getMoves();
				state = mvs.get(entry.getMoveIndex()).evolve(state);
			}
		}
		return pv;
	}

	private ChessMove getBestMoveFrom(final BoardState root, final int depth)
	{
		System.out.println("HELLO FROM DEPTH: " + depth);
		// Initialise variables
		int bestMoveIndex = -1;
		int alpha = -Infinity.SHORT_INFINITY; // Here alpha is the calculated value of our best move.
		final TIntList pv = getPrincipalVariation(root);
		final List<ChessMove> possibleMoves = root.getMoves();

		if (pv.size() != depth - 1)
		{
			System.out.println("Expected pv of length: " + (depth - 1) + " but got " + pv.size());
		}

		final int[] indices = IntStream.range(0, possibleMoves.size()).toArray();

		if (depth > 1)
		{
			assert pv.size() > 0;
			changeFirstIndex(indices, pv.get(0));
		}

		for (final int idx : indices)
		{
			final ChessMove mv = possibleMoves.get(idx);
			final int bestBlackReply = -negamax(mv.evolve(root), -Infinity.SHORT_INFINITY, -alpha, depth - 1);

			if (bestBlackReply > alpha) // We want to maximise the value of best opponent reply
			{
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
	public int negamax(final BoardState root, int alpha, int beta, final int depth)
	{
		final int alphaOrig = alpha;
		final long rootHash = root.getHashing();

		final TableEntry ttEntry = tt.get(rootHash);
		int recommendedMoveIndex = -1;

		if (entryIsValid(rootHash, ttEntry))
		{
			if (ttEntry.getDepthSearched() >= depth)
			{
				switch (ttEntry.getType())
				{
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
				if (alpha >= beta) // If this isn't true then we need more information to get accurate calculation
				{
					return ttEntry.getScore();
				}
			}
			recommendedMoveIndex = ttEntry.getMoveIndex();
		}

		final TerminationType tState = root.getTerminationState();
		if (tState != TerminationType.NOT_TERMINAL)
		{
			return root.getFriendlySide().orientation() * tState.value;
		}

		if (depth == 0)
		{
			return quiescence.getEvaluator().evaluate(root);// .search(root, alpha, beta);
		}

		int bestValue = -Infinity.SHORT_INFINITY;
		int bestMoveIndex = -1, refutationMoveIndex = -1;

		final List<ChessMove> possibleMoves = root.getMoves();
		final int[] indices = IntStream.range(0, possibleMoves.size()).toArray();

		changeFirstIndex(indices, recommendedMoveIndex);

		for (final int i : indices)
		{
			final ChessMove mv = possibleMoves.get(i);
			final int bestReply = -negamax(mv.evolve(root), -beta, -alpha, depth - 1);

			final int oldBestValue = bestValue;
			bestValue = Math.max(bestValue, bestReply);
			bestMoveIndex = oldBestValue != bestValue ? i : bestMoveIndex;

			alpha = Math.max(alpha, bestReply);

			if (alpha >= beta)
			{
				refutationMoveIndex = i;
				break;
			}
		}

		TableEntry potentialNewEntry;
		if (bestValue <= alphaOrig) // ALL node
		{
			potentialNewEntry = TableEntry.generateALL(rootHash, bestValue, depth);
		}
		else if (bestValue >= beta) // CUT node
		{
			potentialNewEntry = TableEntry.generateCUT(rootHash, bestValue, refutationMoveIndex, depth);
		}
		else // PV node
		{
			potentialNewEntry = TableEntry.generatePV(rootHash, bestValue, bestMoveIndex, depth);
		}
		processTableReplacement(potentialNewEntry, ttEntry);

		return bestValue;
	}

	private void changeFirstIndex(final int[] indices, final int recommendedMoveIndex)
	{
		if (recommendedMoveIndex > -1)
		{
			final int tmp = indices[0];
			indices[0] = indices[recommendedMoveIndex];
			indices[recommendedMoveIndex] = tmp;
		}
	}

	private void processTableReplacement(final TableEntry newEntry, final TableEntry oldEntry)
	{
		if (oldEntry == null)
		{
			tt.set(newEntry);
		}
		else if (newEntry.getType() == TreeNodeType.PV && oldEntry.getType() != TreeNodeType.PV)
		{
			System.out.println("SET PV NODE!");
			tt.set(newEntry);
		}
		else if (newEntry.getType() != TreeNodeType.PV && oldEntry.getType() == TreeNodeType.PV)
		{
			return;
		}
		else
		{
			tt.set(newEntry.getDepthSearched() >= oldEntry.getDepthSearched() ? newEntry : oldEntry);
		}
	}

	/**
	 * Assumes the {@link TableEntry} is valid.
	 *
	 * @param moveIndexList
	 * @param ttEntry
	 */
	private void orderMoves(final TIntList moveIndexList, final TableEntry ttEntry)
	{
		final int moveIndex = ttEntry.getMoveIndex();
		throw new RuntimeException("not yet impl");
		// if (moveIndex > -1)
		// {
		// changeFirstIndex(moveIndexList, moveIndex);
		// }
	}

	private boolean entryIsValid(final long nodeHash, final TableEntry entry)
	{
		return entry != null && entry.getPositionHash() == nodeHash;
	}

	// /**
	// * So now for both sides alpha is the minimum score we are guaranteed to be able
	// * to get and beta is the best score
	// *
	// * @param root
	// * @param alpha
	// * @param beta
	// * @param depth
	// * @return
	// */
	// public int nAlphaBeta(final BoardState root, int alpha, final int beta, final int depth)
	// {
	// if (depth == 0)
	// {
	// return eval.evaluate(root);
	// }
	//
	// for (final ChessMove mv : root.getPossibleMovesCopy())
	// {
	// /* Let root.sideToMove = S. Then bestReply is the best score !S can achieve from
	// * the perspective of S, so the higher the score the better it is for S. */
	// final int bestReply = -nAlphaBeta(mv.evolve(root), -beta, -alpha, depth - 1);
	//
	// if (bestReply >= beta)
	// {
	// return beta;
	// }
	// if (bestReply > alpha)
	// {
	// alpha = bestReply;
	// }
	// }
	// return alpha;
	// }

	private TIntList generateIndexList(final int length)
	{
		final TIntList indexList = new TIntArrayList();
		for (int i = 0; i < length; i++)
		{
			indexList.add(i);
		}
		return indexList;
	}

	/**
	 * This method ONLY works once on a List
	 *
	 * @param indexList
	 * @param toGoFirst
	 */
	private <T> void changeFirstIndex(final List<T> x, final int toGoFirst)
	{
		if (toGoFirst > -1)
		{
			x.add(0, x.remove(toGoFirst));
		}
	}

	public static void main(final String[] args)
	{
		final BoardState state = BoardStateImplV2.getStartBoard();
		final BoardEvaluator eval = new BoardEvaluator(Arrays.asList(new KingSafetyV1(), new MobilityV1(), new PawnStructureV1()));
		final MoveCalculator c = new TTAlphaBetaV1_2(eval);
		final NegaAlphaBeta d = new NegaAlphaBeta(eval);

		System.out.println(c.getBestMove(state));
		System.out.println(d.getBestMoveFrom(state, 6));
	}

}
