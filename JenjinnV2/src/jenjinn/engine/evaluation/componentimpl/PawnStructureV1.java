/**
 * Copyright � 2017 Lhasa Limited
 * File created: 11 Aug 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.evaluation.componentimpl;

import static jenjinn.engine.bitboarddatabase.BBDB.FILE;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.Side;
import jenjinn.engine.evaluation.EvaluatingComponent;
import jenjinn.engine.misc.EngineUtils;

/**
 * @author ThomasB
 * @since 11 Aug 2017
 */
public class PawnStructureV1 implements EvaluatingComponent
{
	// Multipliers 
	static final double SEMIOPEN_FILE = 1.1;
	
	static final double OUTSIDE_FILE = 1.1;
	
	static final double ENEMY_CENTRAL_TERRITORY = 1.1;
	
	// PENALTIES
	static final short DOUBLED_PENALTY = 20;

	static final short ISOLATED_PENALTY = 30;

	static final short BACKWARD_PENALTY = 15;

	// BONUSES
	static final short PASSED_BONUS = 40;

	static final short DOUBLE_PHALANX_BONUS = 20;

	static final short TRIPLE_PHALANX_BONUS = 25;

	static final short CENTRAL_BONUS = 20;

	static final short CHAIN_BONUS = 4;
	
	static final short[] PHALANX_BONUSES = {0, 20, 25, 0 ,0, 0, 0};
	
	// Central area
	static final long CENTRAL_AREA = 0b0011110000111100L << (3 * 8);

	@Override
	public short evaluate(final BoardState state)
	{
		short overallEval = getIsolatedPawnScore(state.getPieceLocations(0), state.getPieceLocations(6));

		overallEval += evaluateIndividualPawnProperties(state);
		overallEval += evaluateGlobalPawnProperties(state);
		
		return overallEval;
	}
	
	private short evaluateGlobalPawnProperties(BoardState state)
	{
		short score = 0;
		
		long wPawns = state.getPieceLocations(0), bPawns = state.getPieceLocations(6);
		
		score += getChainBonus(wPawns, Side.W);
		score -= getChainBonus(bPawns, Side.B);
		
		score += getPhalanxBonus(wPawns);
		score -= getPhalanxBonus(bPawns);
		
		return score;
	}

	private short evaluateIndividualPawnProperties(BoardState state)
	{
		short score = 0;
		
		final long wPawns = state.getPieceLocations(0), bPawns = state.getPieceLocations(6);
		final long whiteAttacks = state.getSquaresAttackedBy(Side.W), blackAttacks = state.getSquaresAttackedBy(Side.B);
		
		for (int i = 0; i < 8; i++)
		{
			long wFilePawns = wPawns & BBDB.FILE[i], bFilePawns = bPawns & BBDB.FILE[i];
			
			if (Long.bitCount(wFilePawns) > 0)
			{
				byte[] pLocs = EngineUtils.getSetBits(wFilePawns);
				
				long adjFriendlies = getAdjacentFilePawns(wPawns, i);
				long adjEnemies = getAdjacentFilePawns(bPawns, i);
				
				score -= getDoubledPenaltySingle(pLocs, i);
				score -= getIsolatedPenaltySingle(pLocs, adjFriendlies, bFilePawns, i);
				score -= getBackwardPenaltySingle(pLocs, adjFriendlies, bFilePawns, blackAttacks, Side.W);
				
				score += getPassedBonus(pLocs, adjEnemies, bFilePawns, Side.W);
				score += getCentralBonus(pLocs, Side.W);
				
			}
			
			if (Long.bitCount(bFilePawns) > 0)
			{
				byte[] pLocs = EngineUtils.getSetBits(bFilePawns);
				
				long adjFriendlies = getAdjacentFilePawns(bPawns, i);
				long adjEnemies = getAdjacentFilePawns(wPawns, i);
				
				score += getDoubledPenaltySingle(pLocs, i);
				score += getIsolatedPenaltySingle(pLocs, adjFriendlies, wFilePawns, i);
				score += getBackwardPenaltySingle(pLocs, adjFriendlies, wFilePawns, whiteAttacks, Side.B);
				
				score -= getPassedBonus(pLocs, adjEnemies, wFilePawns, Side.B);
				score -= getCentralBonus(pLocs, Side.B);
			}
		}
		return score;
	}
	
	private short getChainBonus(final long pawns, final Side side)
	{
		long pawnAttacks = EngineUtils.multipleOr(getPawnAttacksFromLocs(pawns, side));
		return (short) (Long.bitCount(pawnAttacks & pawns) * CHAIN_BONUS); 
	}
	
	private static short getPhalanxBonus(final long pawns)
	{
		short bonus = 0;
		long pawnsLeft = pawns;
		
		for (int i = 0; i < 8; i++)
		{
			long filePawns = pawnsLeft & BBDB.FILE[7 - i];
			
			if (filePawns > 0)
			{
				byte[] positions = EngineUtils.getSetBits(filePawns);
				
				for (byte position : positions)
				{
					int counter = 0;
					long pos = 1L << position;
					
					while (((pos <<= 1) & pawnsLeft) != 0)
					{
						pawnsLeft &= ~pos;
						counter++;
					}
					bonus += PHALANX_BONUSES[counter];
				}
			}
		}
		
		return bonus;
	}
	
	private short getCentralBonus(byte[] pLocs, Side friendlySide) 
	{
		short bonus = 0;
		
		boolean isWhite = friendlySide.isWhite();
		
		for (byte pLoc : pLocs) 
		{
			long location = 1L << pLoc;
			
			if ((location & CENTRAL_AREA) != 0)
			{
				long enemyCentral = 0b111100L << (isWhite ? (4 * 8) : (3 * 8));
				bonus += (location & enemyCentral) != 0 ? ENEMY_CENTRAL_TERRITORY * CENTRAL_BONUS : CENTRAL_BONUS;
			}
		}
		return bonus;
	}

	private short getPassedBonus(byte[] pLocs, long adjEnemies, long enemyFilePawns, Side friendlySide)
	{
		short bonus = 0;
		
		boolean isWhite = friendlySide.isWhite();
		
		long opposingEnemies = adjEnemies | enemyFilePawns;
		
		long comparisonBit = isWhite ? Long.highestOneBit(opposingEnemies) : Long.lowestOneBit(opposingEnemies);
		
		for (byte pLoc : pLocs)
		{
			long loc = 1L << pLoc;
			
			if ((isWhite && (loc << 1) >= comparisonBit) || (!isWhite && (loc >>> 1) <= comparisonBit))
			{
				bonus += PASSED_BONUS;
			}
		}
		
		return bonus;
	}
	
	private short getBackwardPenaltySingle(byte[] pLocs, long adjFriendlies, long enemyFilePawns, long enemyAttacks, Side friendlySide) 
	{
		short score = 0;
		
		boolean isWhite = friendlySide.isWhite();
		
		long compareBit = isWhite ? Long.lowestOneBit(adjFriendlies) : Long.highestOneBit(adjFriendlies);
		
		for (byte pawnLoc : pLocs)
		{
			long pLoc = 1L << pawnLoc;
			
			if ((isWhite && pLoc < compareBit && (pLoc << 1) != compareBit && ((pLoc << 8) & enemyAttacks) != 0) ||
					(!isWhite && compareBit > 0 && pLoc > compareBit && (pLoc >>> 1) != compareBit && ((pLoc >>> 8) & enemyAttacks) != 0))
			{
				score += enemyFilePawns == 0 ? SEMIOPEN_FILE * BACKWARD_PENALTY : BACKWARD_PENALTY;
			}
		}
		return score;
	}

	private short getIsolatedPenaltySingle(byte[] pLocs, long adjFriendlies, long enemyFilePawns, int fileIdx) 
	{
		short score = 0;
		if (adjFriendlies == 0)
		{
			int penalty = pLocs.length * ISOLATED_PENALTY;
			score += enemyFilePawns == 0 ? SEMIOPEN_FILE * penalty : penalty; 
		}
		return score;
	}

	private short getDoubledPenaltySingle(byte[] dPawns, int fileIdx) 
	{
		short penalty = 0;
		
		for (int j = 0; j < dPawns.length - 1; j++)
		{
			if (Math.abs(dPawns[j] - dPawns[j + 1]) / 8 == 1)
			{
				penalty += (fileIdx == 0) || (fileIdx == 7) ? OUTSIDE_FILE * DOUBLED_PENALTY : DOUBLED_PENALTY;
			}
		}
		return penalty;
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
	
	private static long[] getPawnAttacksFromLocs(final long locs, final Side pawnSide)
	{
		boolean areWhitePawns = pawnSide.isWhite();
		long rightSideAttacks, leftSideAttacks;
		final long piecesToRemoveForLHS = areWhitePawns ? FILE[0] : FILE[7];
		final long piecesToRemoveForRHS = areWhitePawns ? FILE[7] : FILE[0];

		long relevantPiecesForLHS = locs & ~piecesToRemoveForLHS;
		long relevantPiecesForRHS = locs & ~piecesToRemoveForRHS;

		leftSideAttacks = areWhitePawns ? (relevantPiecesForLHS << 9) : (relevantPiecesForLHS >>> 7);
		rightSideAttacks = areWhitePawns ? (relevantPiecesForRHS << 7) : (relevantPiecesForRHS >>> 9);

		return new long[]{leftSideAttacks , rightSideAttacks};
	}

	public static void main(final String[] args)
	{
		long testPawns = 0b1100111000111010L;
		System.out.println(getPhalanxBonus(testPawns));
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