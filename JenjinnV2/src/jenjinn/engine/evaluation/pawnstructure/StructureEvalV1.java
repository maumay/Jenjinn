/**
 * Copyright © 2017 Lhasa Limited
 * File created: 11 Aug 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.evaluation.pawnstructure;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.Side;
import jenjinn.engine.evaluation.PawnStructureEvaluator;
import jenjinn.engine.misc.EngineUtils;

/**
 * @author ThomasB
 * @since 11 Aug 2017
 */
public class StructureEvalV1 implements PawnStructureEvaluator
{
	// PENALTIES
	static final short DOUBLED_PENALTY = -20;

	static final short ISOLATED_PENALTY = -30;

	static final short BACKWARD_PENALTY = -20;

	// BONUSES
	static final short PASSED_BONUS = 40;

	static final short DOUBLE_PHALANX_BONUS = 20;

	static final short TRIPLE_PHALANX_BONUS = 25;

	static final short CENTRAL_BONUS = 20;

	static final short CHAIN_BONUS = 5;

	static final short ADVANCED_BONUS = 5;

	@Override
	public short evaluate(final BoardState state)
	{
		short overallEval = 0;

		final long whiteAttacks = state.getSquaresAttackedBy(Side.W), blackAttacks = state.getSquaresAttackedBy(Side.B);

		overallEval += evaluateWhiteStructure(state, blackAttacks);

		return 0;
	}

	private short evaluateWhiteStructure(final BoardState state, final long blackAttacks)
	{
		final long wPawns = state.getPieceLocations(0);
		short eval = 0;

		eval -= getDoubledPenalty(wPawns);
		// TODO Auto-generated method stub
		return 0;
	}

	private short getIsolatedPawnScore(final long whitePawns, final long blackPawns)
	{
		int score = 0;

		/* wIsolatedRight, for example, marks the files which have no white pawns
		 * on the file to the immediate right */
		int wIsolatedRight = 0b00000001, bIsolatedRight = 0b00000001;
		int wIsolatedLeft = 0b10000000, bIsolatedLeft = 0b00000001;

		for (int i = 0; i < 7; i++)
		{
			if ((BBDB.FILE[7 - i] & whitePawns) == 0)
			{
				wIsolatedRight |= (1 << (i + 1));
			}

			if ((BBDB.FILE[7 - i] & blackPawns) == 0)
			{
				bIsolatedRight |= (1 << (i + 1));
			}

			if ((BBDB.FILE[i] & whitePawns) == 0)
			{
				wIsolatedLeft |= (1 << (6 - i));
			}

			if ((BBDB.FILE[i] & blackPawns) == 0)
			{
				bIsolatedLeft |= (1 << (6 - i));
			}
		}

		final int wIsolated = wIsolatedLeft & wIsolatedRight;
		final int bIsolated = bIsolatedLeft & bIsolatedRight;

		for (int i = 0; i < 8; i++)
		{
			final long file = BBDB.FILE[7 - i];

			if ((wIsolated & (1 << i)) != 0)
			{
				final int wPawnPenalty = Long.bitCount(whitePawns & file) * ISOLATED_PENALTY;
				// Bigger penalty if semi open file
				score -= (file & blackPawns) != 0 ? 1.5 * wPawnPenalty : wPawnPenalty;
			}

			if ((bIsolated & (1 << i)) != 0)
			{
				final int bPawnPenalty = Long.bitCount(blackPawns & file) * ISOLATED_PENALTY;
				// Bigger penalty if semi open file
				score += (file & whitePawns) != 0 ? 1.5 * bPawnPenalty : bPawnPenalty;
			}
		}

		return (short) score;
	}

	private short getDoubledPenalty(final long pawns)
	{
		int penalty = 0;

		for (int i = 0; i < 8; i++)
		{
			final long fileAndPawns = BBDB.FILE[i] & pawns;

			if (Long.bitCount(fileAndPawns) > 1)
			{
				final byte[] dPawns = EngineUtils.getSetBits(fileAndPawns);

				for (int j = 0; j < dPawns.length - 1; j++)
				{
					if (Math.abs(dPawns[j] - dPawns[j + 1]) / 8 == 1)
					{
						penalty -= (i == 0) || (i == 7) ? 1.5 * DOUBLED_PENALTY : DOUBLED_PENALTY;
					}
				}
			}
		}
		return (short) penalty;
	}

	public static void main(final String[] args)
	{
		EngineUtils.printNbitBoards(BBDB.FILE[0]);
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