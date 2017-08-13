/**
 * Copyright � 2017 Lhasa Limited
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
	// Multipliers 
	static final double SEMIOPEN_FILE = 1.5;
	
	static final double OUTSIDE_FILE = 1.5;
	
	// PENALTIES
	static final short DOUBLED_PENALTY = 20;

	static final short ISOLATED_PENALTY = 30;

	static final short BACKWARD_PENALTY = 15;

	// BONUSES
	static final short PASSED_BONUS = 40;

	static final short DOUBLE_PHALANX_BONUS = 20;

	static final short TRIPLE_PHALANX_BONUS = 25;

	static final short CENTRAL_BONUS = 20;

	static final short CHAIN_BONUS = 5;

	@Override
	public short evaluate(final BoardState state)
	{
		short overallEval = getIsolatedPawnScore(state.getPieceLocations(0), state.getPieceLocations(6));

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
	
	private short evaluateIndividualPawnProperties(BoardState state)
	{
		short score = 0;
		
		final long wPawns = state.getPieceLocations(0), bPawns = state.getPieceLocations(6);
		final long whiteAttacks = state.getSquaresAttackedBy(Side.W), blackAttacks = state.getSquaresAttackedBy(Side.B);
		
		for (int i = 0; i < 8; i++)
		{
			long wFilePawns = wPawns & BBDB.FILE[i], bFilePawns = bPawns & BBDB.FILE[i];
		}
		
		return score;
	}
	
	private short getPassedPawnScore(BoardState state)
	{
		short score = 0;
		
		return score;
	}
	
	private short getBackwardPawnScore(BoardState state)
	{
		short score = 0;
		
		final long wPawns = state.getPieceLocations(0), bPawns = state.getPieceLocations(6);
		final long whiteAttacks = state.getSquaresAttackedBy(Side.W), blackAttacks = state.getSquaresAttackedBy(Side.B);
		
		for (int i = 0; i < 8; i++)
		{
			byte[] wSetBits = EngineUtils.getSetBits(wPawns & BBDB.FILE[i]);
			byte[] bSetBits = EngineUtils.getSetBits(bPawns & BBDB.FILE[i]);
			
			if (wSetBits.length > 0)
			{
				long adjacentPawns = getAdjacentFilePawns(wPawns, i);
				
				long lowestBit = Long.lowestOneBit(adjacentPawns);
				
				for (byte pawnLoc : wSetBits)
				{
					long pLoc = 1L << pawnLoc;
					
					if (pLoc < lowestBit && (pLoc << 1) != lowestBit && ((pLoc << 8) & blackAttacks) != 0)
					{
						score -= (BBDB.FILE[i] & bPawns) != 0 ? SEMIOPEN_FILE * BACKWARD_PENALTY : BACKWARD_PENALTY;
					}
				}
			}
			
			if (bSetBits.length > 0)
			{
				long adjacentPawns = getAdjacentFilePawns(bPawns, i);
				
				long highestBit = Long.highestOneBit(adjacentPawns);
				
				for (byte pawnLoc : bSetBits)
				{
					long pLoc = 1L << pawnLoc;
					
					if (pLoc > highestBit && (pLoc >>> 1) != highestBit && ((pLoc >>> 8) & whiteAttacks) != 0)
					{
						score += (BBDB.FILE[i] & wPawns) != 0 ? SEMIOPEN_FILE * BACKWARD_PENALTY : BACKWARD_PENALTY;
					}
				}
			}
		}
		return score;
	}

	private long getAdjacentFilePawns(long pawns, int fileNumber)
	{
		if (fileNumber == 0)
		{
			return pawns & BBDB.FILE[1];
		}
		else if (fileNumber == 7)
		{
			return pawns & BBDB.FILE[6];
		}
		else
		{
			return pawns & (BBDB.FILE[fileNumber - 1] | BBDB.FILE[fileNumber + 1]);
		}
	}
	
	/**
	 * Improed version
	 * 
	 * @param whitePawns
	 * @param blackPawns
	 * @return
	 */
	private short getIsolatedPawnScore2(final long whitePawns, final long blackPawns)
	{
		int score = 0;
		
		for (int i = 0; i < 8; i++)
		{
			long wFilePawns = BBDB.FILE[i] & whitePawns, bFilePawns = BBDB.FILE[i] & blackPawns;
			
			if (wFilePawns > 0)
			{
				long wAdjPawns = getAdjacentFilePawns(whitePawns, i);
				
				if (wAdjPawns == 0)
				{
					int penalty = Long.bitCount(wFilePawns) * ISOLATED_PENALTY;
					score -= bFilePawns == 0 ? SEMIOPEN_FILE * penalty : penalty; 
				}
				
			}
			if (bFilePawns > 0)
			{
				long bAdjPawns = getAdjacentFilePawns(blackPawns, i);
				
				if (bAdjPawns == 0)
				{
					int penalty = Long.bitCount(bFilePawns) * ISOLATED_PENALTY;
					score += wFilePawns == 0 ? SEMIOPEN_FILE * penalty : penalty; 
				}
			}
		}
		
		return (short) score;
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

		final int wIsolated = wIsolatedLeft & wIsolatedRight, bIsolated = bIsolatedLeft & bIsolatedRight;

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

		assert score == (short) score; // Assert no numerical overflow.
		
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
						penalty -= (i == 0) || (i == 7) ? OUTSIDE_FILE * DOUBLED_PENALTY : DOUBLED_PENALTY;
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