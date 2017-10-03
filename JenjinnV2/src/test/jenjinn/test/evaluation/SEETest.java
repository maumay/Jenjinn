/**
 * 
 */
package jenjinn.test.evaluation;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import static jenjinn.engine.enums.Sq.*;
import static jenjinn.engine.misc.EngineUtils.*;

import org.junit.Test;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImplV2;
import jenjinn.engine.evaluation.SEE;

/**
 * @author t
 *
 */
public class SEETest 
{
	private static final short[] PIECE_VALUES = { 100, 325, 325, 500, 1000 };
	
	private static final int EXPECTED_TEST1 = 100, EXPECTED_TEST2 = -225;

	/**
	 * Test method for {@link jenjinn.engine.evaluation.SEE#eval(byte, byte, jenjinn.engine.boardstate.BoardState, short[])}.
	 */
	@Test
	public void testEval() 
	{
		BoardState firstTest = getPosition1(), secondTest = getPosition2();
		
		SEE see = new SEE();
		
		assertEquals(EXPECTED_TEST1, see.eval((byte)e5.ordinal(), (byte)e1.ordinal(), firstTest, PIECE_VALUES));
		assertEquals(EXPECTED_TEST2, see.eval((byte)e5.ordinal(), (byte)d3.ordinal(), secondTest, PIECE_VALUES));
	}
	
	private BoardState getPosition1()
	{
		long wPawn = getBB(a3, b2, c2, g3, h2), bPawn = getBB(a6, b7, c7, e5, h7);
		long wRook = getBB(e1), bRook = getBB(d8);
		long wKing = getBB(c1), bKing = getBB(b8);
		
		long[] pieceLocs = new long[12];
		pieceLocs[0] = wPawn;
		pieceLocs[6] = bPawn;
		pieceLocs[3] = wRook;
		pieceLocs[9] = bRook;
		pieceLocs[5] = wKing;
		pieceLocs[11] = bKing;
		
		return new BoardStateImplV2(null, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, pieceLocs);
	}
	
	private BoardState getPosition2()
	{
		long wPawn = getBB(a3, b2, c2, g3, h2), bPawn = getBB(a6, b7, c7, e5, h7);
		long wBish = getBB(g2), bBish = getBB(f6);
		long wKnight = getBB(d3), bKnight = getBB(d7);
		long wRook = getBB(e2), bRook = getBB(d8);
		long wQueen = getBB(e1), bQueen = getBB(h8);
		long wKing = getBB(c1), bKing = getBB(b8);
		
		long[] pieceLocs = {wPawn, wBish, wKnight, wRook, wQueen, wKing, bPawn, bBish, bKnight, bRook, bQueen, bKing};
		
		return new BoardStateImplV2(null, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, pieceLocs);
	}
}
