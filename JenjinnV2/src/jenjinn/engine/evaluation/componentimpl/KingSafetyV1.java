/**
 * Copyright © 2017 Lhasa Limited
 * File created: 30 Aug 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.evaluation.componentimpl;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.Side;
import jenjinn.engine.evaluation.EvaluatingComponent;
import jenjinn.engine.misc.EngineUtils;

/**
 * @author ThomasB
 * @since 30 Aug 2017
 */
public class KingSafetyV1 implements EvaluatingComponent
{
	private static final short MID_PAWN_SHIELD_BONUS = 10, END_PAWN_SHIELD_BONUS = 7;
	private static final short MID_DIRECT_SHIELD_BONUS = 12, END_DIRECT_SHIELD_BONUS = 0;

	private static final short MID_OPEN_FILE_PENALTY = 35, END_OPEN_FILE_PENALTY = 2;

	/**
	 * Working variables.
	 */
	private BoardState state;

	private short midEval, endEval;

	private boolean whiteCastled, blackCastled;

	@Override
	public short evaluate(final BoardState state)
	{
		this.state = state;

		midEval = 0;
		endEval = 0;

		final byte castleStatus = state.getCastleStatus();
		whiteCastled = (castleStatus & 0b11) != 0;
		blackCastled = (castleStatus & 0b1100) != 0;

		// TODO Auto-generated method stub
		evaluateKingSafety(Side.W);
		evaluateKingSafety(Side.B);

		return 0;
	}

	private void evaluateKingSafety(final Side side)
	{
		final boolean isWhite = side.isWhite();
		final int orientation = side.orientation();

		if (isWhite ? whiteCastled : blackCastled)
		{
			final byte kingLoc = EngineUtils.getSetBits(state.getPieceLocations(5 + side.index()))[0];
			// final int rankNum = kingLoc / 8, fileNum = 7 - (kingLoc % 8);

			final long immediateShieldArea = getImmediateShieldArea(kingLoc, side);
			final long outerShieldArea = isWhite ? immediateShieldArea << 8 : immediateShieldArea >>> 8;

			final long pawns = state.getPieceLocations(side.index());

			midEval += orientation * Long.bitCount(outerShieldArea & pawns) * MID_PAWN_SHIELD_BONUS;
			midEval += orientation * Long.bitCount(immediateShieldArea & pawns) * (MID_PAWN_SHIELD_BONUS + MID_DIRECT_SHIELD_BONUS);

			endEval += orientation * Long.bitCount(outerShieldArea & pawns) * END_PAWN_SHIELD_BONUS;
			endEval += orientation * Long.bitCount(immediateShieldArea & pawns) * (END_PAWN_SHIELD_BONUS + END_DIRECT_SHIELD_BONUS);
		}
		else
		{
			midEval += 3 * orientation * MID_OPEN_FILE_PENALTY;
			endEval += 3 * orientation * END_OPEN_FILE_PENALTY;
		}

	}

	private static long getImmediateShieldArea(final byte kingLoc, final Side side)
	{
		final long[] components = new long[3];

		final int orientation = side.orientation();
		final int rankNum = kingLoc / 8;

		final int shiftStart = kingLoc + orientation * 7;
		for (int i = 0; i < 3; i++)
		{
			final int shifted = Math.min(Math.max(shiftStart + orientation * i, 0), 64);
			if (shifted / 8 == rankNum + orientation)
			{
				components[i] = 1L << shifted;
			}
		}
		return EngineUtils.multipleOr(components);
	}

	public static void main(final String[] args)
	{
		System.out.println(0b110 >>> 8);

		EngineUtils.printNbitBoards(getImmediateShieldArea((byte) 48, Side.W));
	}

}

/* ---------------------------------------------------------------------*
 * This software is the confidential and proprietary
 * information of Lhasa Limited
 * Granary Wharf House, 2 Canal Wharf, Leeds, LS11 5PS
 * ---
 * No part of this confidential information shall be disclosed
 * and it shall be used only in accordance with the terms of a
 * written license agreement entered into by holder of the information
 * with LHASA Ltd.
 * --------------------------------------------------------------------- */