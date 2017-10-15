/**
 *
 */
package jenjinn.test.debugging;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImplV2;
import jenjinn.engine.evaluation.BoardEvaluator;
import jenjinn.engine.gametree.MoveCalculator;
import jenjinn.engine.gametree.NegaAlphaBeta;
import jenjinn.engine.gametree.TTAlphaBetaV1_2;
import jenjinn.engine.moves.ChessMove;

/**
 * @author t
 *
 */
public class TreeDebugging
{

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{

		final NegaAlphaBeta nag = new NegaAlphaBeta(BoardEvaluator.getDefault());
		final MoveCalculator mc = new TTAlphaBetaV1_2(BoardEvaluator.getDefault());

		/*
		 * Play this sequence as white, some bugs can be seen from this
		 * position. Try removing evaluating components again first before
		 * looking into tree. Maybe should just writr tests for the eval first?
		 */
		BoardState state = BoardStateImplV2.getStartBoard();
		//		state = ChessMove.fromCompactString2("0_e2_e4").evolve(state);
		//		state = ChessMove.fromCompactString2("0_c7_c5").evolve(state);
		//		state = ChessMove.fromCompactString2("0_g1_f3").evolve(state);
		//		state = ChessMove.fromCompactString2("0_b8_c6").evolve(state);
		//		state = ChessMove.fromCompactString2("0_c2_c3").evolve(state);
		//		state = ChessMove.fromCompactString2("0_g8_f6").evolve(state);
		//		state = ChessMove.fromCompactString2("0_f1_d3").evolve(state);
		//		state = ChessMove.fromCompactString2("0_e7_e5").evolve(state);
		//		state = ChessMove.fromCompactString2("0_d3_c2").evolve(state);
		//		System.out.println(nag.getBestMoveFrom(state) + " ---- " + mc.getBestMoveFrom(state));
		System.out.println("-------------------");
		state = ChessMove.fromCompactString2("0_e2_e4").evolve(state);
		state = ChessMove.fromCompactString2("0_e7_e6").evolve(state);
		state = ChessMove.fromCompactString2("0_a2_a4").evolve(state);
		//		state = ChessMove.fromCompactString2("0_b8_c6").evolve(state);
		//
		//		try
		//		{
		//			System.out.println(nag.getQuiescence().search(state, -1000, 1000, false));
		//		}
		//		catch (final InterruptedException e)
		//		{
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		System.out.println(nag.getBestMoveFrom(state));// + " ---- " + mc.getBestMoveFrom(state));
	}

}
