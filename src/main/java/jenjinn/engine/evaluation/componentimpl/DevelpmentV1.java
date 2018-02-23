/**
 * 
 */
package jenjinn.engine.evaluation.componentimpl;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImpl;
import jenjinn.engine.evaluation.EvaluatingComponent;
import jenjinn.engine.misc.EngineUtils;

/**
 * @author t
 *
 */
public class DevelpmentV1 implements EvaluatingComponent
{
	private static final short CASTLE_BONUS = 150;
	private static final short[] DEV_BONUSES = { 80, 75, 90, 90, 75, 80 };

	private static final long WRANKS = BBDB.RNK[0] | BBDB.RNK[1], BRANKS = BBDB.RNK[6] | BBDB.RNK[7];

	/*
	 * (non-Javadoc)
	 * 
	 * @see jenjinn.engine.evaluation.EvaluatingComponent#evaluate(jenjinn.engine.
	 * boardstate.BoardState)
	 */
	@Override
	public short evaluate(BoardState state)
	{
		// System.out.println("------------------------------------");
		byte castleStatus = state.getCastleStatus();
		// System.out.println(Integer.toBinaryString(castleStatus));
		boolean wc = (castleStatus & 0b11) != 0, bc = (castleStatus & 0b1100) != 0;
		// System.out.println(wc + ", " + bc);

		int eval = ((wc ? 1 : 0) - (bc ? 1 : 0)) * CASTLE_BONUS;

		long devStatus = state.getDevelopmentStatus();
		long wdev = devStatus & WRANKS, bdev = devStatus & BRANKS;
		// System.out.println();
		// EngineUtils.printNbitBoards(devStatus, wdev, bdev);

		for (int i = 1; i < 7; i++) {
			long fle = BBDB.FILE[i];
			eval += (((wdev & fle) == 0 ? 1 : 0) - ((bdev & fle) == 0 ? 1 : 0)) * DEV_BONUSES[i - 1];
		}
		// System.out.println(eval);
		// System.out.println("------------------------------------");
		return (short) eval;
	}

	public static void main(String[] args)
	{
		BoardState bs = BoardStateImpl.getStartBoard();
		EngineUtils.printNbitBoards(bs.getDevelopmentStatus());
		System.out.println();
		System.out.println(Integer.toBinaryString(bs.getCastleRights()));
		System.out.println(Integer.toBinaryString(bs.getCastleStatus()));
	}
}
