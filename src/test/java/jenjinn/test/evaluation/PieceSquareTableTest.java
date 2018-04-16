package jenjinn.test.evaluation;

import static jenjinn.engine.boardstate.BoardStateConstants.getEndGamePST;
import static jenjinn.engine.boardstate.BoardStateConstants.getMiddleGamePST;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImpl;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 * @since 13 Oct 2017
 */
public class PieceSquareTableTest
{
	@Test
	public void testState1()
	{
		final BoardState state = getTestState1();

		int midEval = 0, endEval = 0, ctr = 0;

		for (final long pieceLocs : state.getPieceLocationsCopy()) {
			final ChessPiece p = ChessPiece.get(ctr++);

			for (final byte loc : EngineUtils.getSetBits(pieceLocs)) {
				midEval += getMiddleGamePST().getPieceSquareValue(p.index(), loc);
				endEval += getEndGamePST().getPieceSquareValue(p.index(), loc);
			}
		}

		assertEquals(midEval, state.getMidgamePositionalEval());
		assertEquals(endEval, state.getEndgamePositionalEval());

		System.out.println(midEval);
	}

	private static BoardState getTestState1()
	{
		BoardState state = BoardStateImpl.getStartBoard();
		state = ChessMove.fromCompactString2("0_e2_e4").evolve(state);
		state = ChessMove.fromCompactString2("0_e7_e6").evolve(state);
		state = ChessMove.fromCompactString2("0_a2_a4").evolve(state);
		state = ChessMove.fromCompactString2("0_b8_c6").evolve(state);
		return state;
	}

}
