package jenjinn.engine.gametree;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImpl;
import jenjinn.engine.enums.Infinity;
import jenjinn.engine.evaluation.BoardEvaluator;
import jenjinn.engine.moves.ChessMove;

/**
 * @author ThomasB
 * @since 13 Jul 2017
 */
public class NegaAlphaBeta implements MoveCalculator
{
	/**
	 * Only use a nega evaluator, i.e one that is signed depending on whether white
	 * or black is to move.
	 */
	private Quiescence quiescence;
	private int depth = 1;

	public NegaAlphaBeta(final BoardEvaluator eval)
	{
		this.quiescence = new Quiescence(eval);
	}

	public ChessMove getBestMoveFrom(final BoardState root, final int depth)
	{
		// Initialise variables
		ChessMove bestMove = null;
		int alpha = -Infinity.INT_INFINITY; // Here alpha is the calculated value of our best move.

		for (final ChessMove mv : root.getMoves()) {
			System.out.println("---------------------");
			System.out.println(mv.toString());

			if (mv.toString().equals("S[b8, c6]")) {
				System.out.println("Wait");
			}
			int bestReply = 0;
			try {
				bestReply = -nAlphaBeta(mv.evolve(root), -Infinity.INT_INFINITY, -alpha, depth - 1);
			}
			catch (final InterruptedException e) {
				e.printStackTrace();
				throw new AssertionError();
			}

			System.out.println("Current alpha: " + alpha + "\nBest reply: " + bestReply);
			System.out.println("---------------------");

			if (bestReply > alpha) // We want to maximise the value of best reply
			{
				alpha = bestReply;
				bestMove = mv;
			}
		}
		return bestMove;
	}

	/**
	 * So now for both sides alpha is the minimum score we are guaranteed to be able
	 * to get and beta is the best score
	 *
	 * @param root
	 * @param alpha
	 * @param beta
	 * @param depth
	 * @return
	 * @throws InterruptedException
	 */
	public int nAlphaBeta(final BoardState root, int alpha, final int beta, final int depth) throws InterruptedException
	{
		if (depth == 0 || root.isTerminal()) {
			System.out.println((depth == 0) + ", " + root.isTerminal());
			System.out.println(
					"Quiescence: " + quiescence.search(root, Infinity.IC_ALPHA, Infinity.IC_BETA, 30, false));
			return quiescence.search(root, Infinity.IC_ALPHA, Infinity.IC_BETA, 30, false);// getEvaluator().evaluate(root);//
		}

		for (final ChessMove mv : root.getMoves()) {
			/*
			 * Let root.sideToMove = S. Then bestReply is the best score !S can achieve from
			 * the perspective of S, so the higher the score the better it is for S.
			 */
			final int bestReply = -nAlphaBeta(mv.evolve(root), -beta, -alpha, depth - 1);

			if (bestReply >= beta) {
				return beta;
			}
			if (bestReply > alpha) {
				alpha = bestReply;
			}
		}
		return alpha;
	}

	@Override
	public ChessMove getBestMoveFrom(final BoardState root)
	{
		return getBestMoveFrom(root, depth);
	}

	@Override
	public void setSearchDepth(final int depth)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setEvaluator(final BoardEvaluator evaluator)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescriptor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(final String[] args)
	{
		final NegaAlphaBeta nag = new NegaAlphaBeta(BoardEvaluator.getDefault());
		final MoveCalculator mc = new TTAlphaBetaV1_2(BoardEvaluator.getDefault());

		BoardState state = BoardStateImpl.getStartBoard();
		state = ChessMove.fromCompactString2("0_e2_e4").evolve(state);
		state = ChessMove.fromCompactString2("0_e7_e5").evolve(state);
		state = ChessMove.fromCompactString2("0_d2_d4").evolve(state);
		state = ChessMove.fromCompactString2("0_b8_c6").evolve(state);
		state = ChessMove.fromCompactString2("0_d4_e5").evolve(state);
		state = ChessMove.fromCompactString2("0_c6_e5").evolve(state);
		state = ChessMove.fromCompactString2("0_f1_b5").evolve(state);
		state = ChessMove.fromCompactString2("0_c7_c6").evolve(state);
		System.out.println(nag.getBestMoveFrom(state) + " ---- " + mc.getBestMoveFrom(state));
		System.out.println("-------------------");
		// state = ChessMove.fromCompactString2("0_d1_d5").evolve(state);
		// state = ChessMove.fromCompactString2("0_c6_d5").evolve(state);
		// state = ChessMove.fromCompactString2("0_e4_d5").evolve(state);
	}

	public Quiescence getQuiescence()
	{
		return quiescence;
	}
}
