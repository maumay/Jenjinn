/**
 *
 */
package jenjinn.test.evaluation;

import static jenjinn.engine.enums.Sq.a3;
import static jenjinn.engine.enums.Sq.a6;
import static jenjinn.engine.enums.Sq.b2;
import static jenjinn.engine.enums.Sq.b7;
import static jenjinn.engine.enums.Sq.b8;
import static jenjinn.engine.enums.Sq.c1;
import static jenjinn.engine.enums.Sq.c2;
import static jenjinn.engine.enums.Sq.c7;
import static jenjinn.engine.enums.Sq.d3;
import static jenjinn.engine.enums.Sq.d7;
import static jenjinn.engine.enums.Sq.d8;
import static jenjinn.engine.enums.Sq.e1;
import static jenjinn.engine.enums.Sq.e2;
import static jenjinn.engine.enums.Sq.e5;
import static jenjinn.engine.enums.Sq.f6;
import static jenjinn.engine.enums.Sq.g2;
import static jenjinn.engine.enums.Sq.g3;
import static jenjinn.engine.enums.Sq.h2;
import static jenjinn.engine.enums.Sq.h7;
import static jenjinn.engine.enums.Sq.h8;
import static jenjinn.engine.misc.EngineUtils.getBB;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImpl;
import jenjinn.engine.evaluation.StaticExchangeEvaluator;

/**
 * @author t
 *
 */
public class SEETest
{
	private static final short[] PIECE_VALUES = { 100, 325, 325, 500, 1000 };

	private static final int EXPECTED_TEST1 = 100, EXPECTED_TEST2 = -225;

	/**
	 * Test method for
	 * {@link jenjinn.engine.evaluation.StaticExchangeEvaluator#eval(byte, byte, jenjinn.engine.boardstate.BoardState, short[])}.
	 */
	@Test
	public void testEval()
	{
		final BoardState firstTest = getPosition1(), secondTest = getPosition2();

		final StaticExchangeEvaluator see = new StaticExchangeEvaluator();

		assertEquals(EXPECTED_TEST1, see.eval((byte) e5.ordinal(), (byte) e1.ordinal(), firstTest, PIECE_VALUES));
		assertEquals(EXPECTED_TEST2, see.eval((byte) e5.ordinal(), (byte) d3.ordinal(), secondTest, PIECE_VALUES));
	}

	private BoardState getPosition1()
	{
		final long wPawn = getBB(a3, b2, c2, g3, h2), bPawn = getBB(a6, b7, c7, e5, h7);
		final long wRook = getBB(e1), bRook = getBB(d8);
		final long wKing = getBB(c1), bKing = getBB(b8);

		final long[] pieceLocs = new long[12];
		pieceLocs[0] = wPawn;
		pieceLocs[6] = bPawn;
		pieceLocs[3] = wRook;
		pieceLocs[9] = bRook;
		pieceLocs[5] = wKing;
		pieceLocs[11] = bKing;

		return new BoardStateImpl(null, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, pieceLocs);
	}

	private BoardState getPosition2()
	{
		final long wPawn = getBB(a3, b2, c2, g3, h2), bPawn = getBB(a6, b7, c7, e5, h7);
		final long wBish = getBB(g2), bBish = getBB(f6);
		final long wKnight = getBB(d3), bKnight = getBB(d7);
		final long wRook = getBB(e2), bRook = getBB(d8);
		final long wQueen = getBB(e1), bQueen = getBB(h8);
		final long wKing = getBB(c1), bKing = getBB(b8);

		final long[] pieceLocs = { wPawn, wBish, wKnight, wRook, wQueen, wKing, bPawn, bBish, bKnight, bRook, bQueen, bKing };

		return new BoardStateImpl(null, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, pieceLocs);
	}
}
