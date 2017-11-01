/**
 * Copyright � 2017 Lhasa Limited
 * File created: 13 Oct 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.evaluation.componentimpl;

import static jenjinn.engine.evaluation.componentimpl.PawnStructureV1.getPawnAttacksFromLocs;

import java.util.ArrayList;
import java.util.List;

import gnu.trove.list.TByteList;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TByteArrayList;
import gnu.trove.list.array.TLongArrayList;
import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.Side;
import jenjinn.engine.evaluation.EvaluatingComponent;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.pieces.ChessPiece;

/**
 * We do these components together because both require all the attacks of all pieces.
 *
 * @author ThomasB
 * @since 13 Oct 2017
 */
public class MobilityAndKingSafetyV2 implements EvaluatingComponent
{
	// Pawn shield/storm static variables
	private static final short MID_PAWN_SHIELD_BONUS = 40, END_PAWN_SHIELD_BONUS = 7;
	private static final short MID_DIRECT_SHIELD_BONUS = 25, END_DIRECT_SHIELD_BONUS = 0;
	private static final short MID_OPEN_FILE_PENALTY = 100, END_OPEN_FILE_PENALTY = 20;

	// Mobility static variables
	/**
	 * The mobility scores define what value we give to the mobility of the
	 * various pieces.
	 */
	private static final MobilityScores MID_MOBILITY_SCORES = new MobilityScores(2, 3, 0, 0, 1);
	private static final MobilityScores END_MOBILITY_SCORES = new MobilityScores(1, 1, 1, 1, 2);

	/**
	 * Working variables.
	 */
	private BoardState state;
	private short midEval, endEval;

	// Pawn shield/storm working variables
	private boolean whiteCastled, blackCastled;

	// B, N, R, Q Piece attacks and R, Q locs
	private long wLocs, bLocs;
	private List<TLongList> atts;
	private List<TByteList> rQlocs;
	private long wPawnAtt, bPawnAtt;



	@Override
	public short evaluate(final BoardState state)
	{
		//--------------------------------
		/* THIS DATA MUST BE RESET HERE */
		this.state = state;
		midEval = 0;
		endEval = 0;
		initPieceAttacks();
		//--------------------------------

		evaluatePawnShield();
		evaluateBishopMobility();
		evaluateKnightMobility();
		evaluateRookMobility();
		evaluateQueenMobility();
		
		calcKingSafety(Side.W);
		calcKingSafety(Side.B);

		final short gamePhase = state.getGamePhase();
		return (short) (((midEval * (256 - gamePhase)) + (endEval * gamePhase)) / 256);
	}

	private void calcKingSafety(Side s)
	{
		int totalattckunits = 0;
		int totalattckingpieces = 0;
		// First for white
		int kindex = 5 + s.index();
		final byte kingLoc = EngineUtils.getSetBits(state.getPieceLocations(kindex))[0];
		long kloc = (1L << kingLoc);
		
		long friendly = s.isWhite()? wLocs : bLocs, enemy = s.isWhite()? bLocs : wLocs;
		final long mvset = ChessPiece.get(kindex).getMoveset(kingLoc, friendly, enemy);

		final int fileNum = 7 - (kingLoc % 8), rnk = kingLoc / 8;
		final boolean bckrnk = s.isWhite()? rnk == 7 : rnk == 0;
		
		int ebaIdxShift = (fileNum == 0)? -1 : ((fileNum == 7) ? 1 : 0);
		long eba = BBDB.EBA[6][kingLoc + ebaIdxShift];
		long kingzone = 
				(bckrnk? 0L : (eba & BBDB.RNK[rnk + (s.isWhite()? 1 : -1)])) 
				| (eba & mvset) 
				| kloc;
		
		int lower = s.isWhite()? 4 : 0, upper = lower + 4;
		for (int i = lower; i < upper; i++)
		{
			TLongList pAtts = atts.get(i);
			int attckunits = KingSafetyTable.indexAttackUnits(i%4);
			int checkbonus = KingSafetyTable.indexBonusTable(i%4);
			
			for (int j = 0; j < pAtts.size(); j++)
			{
				long attcks = pAtts.get(j);
				int card = Long.bitCount(kingzone & attcks);
				totalattckingpieces += card > 0 ? 1 : 0;
				totalattckunits += card*attckunits;
				totalattckunits += Long.bitCount(kloc & attcks)*checkbonus;
			}
		}
		
		if (totalattckingpieces > 1)
		{
			totalattckunits = totalattckingpieces == 2 ? totalattckunits/2 : totalattckunits;
			int sgn = -s.orientation();
			midEval += sgn*KingSafetyTable.indexSafetyTable(totalattckunits);
			endEval += sgn*KingSafetyTable.indexSafetyTable(totalattckunits);
		}
	}


	private void initPieceAttacks()
	{
		wLocs = state.getSideLocations(Side.W);
		bLocs = state.getSideLocations(Side.B);

		atts = new ArrayList<>(8);
		for (int i = 0; i < 8; i++)
		{
			atts.add(new TLongArrayList(2));
		}

		rQlocs = new ArrayList<>(4);
		for (int i = 0; i < 4; i++)
		{
			rQlocs.add(new TByteArrayList(2));
		}

		final long occ = wLocs | bLocs;

		// White B, N, R, Q attacks
		for (int i = 1; i < 5; i++)
		{
			final ChessPiece p = ChessPiece.get(i);

			for (final byte loc : EngineUtils.getSetBits(state.getPieceLocations(i)))
			{
				atts.get(i - 1).add(p.getAttackset(loc, occ));
				if (i > 2)
				{
					// e.g wr index 3 gets mapped to rQlocs index 0
					rQlocs.get(i - 3).add(loc);
				}
			}
		}

		// Black B, N, R, Q attacks
		for (int i = 7; i < 11; i++)
		{
			final ChessPiece p = ChessPiece.get(i);

			for (final byte loc : EngineUtils.getSetBits(state.getPieceLocations(i)))
			{
				atts.get(i - 3).add(p.getAttackset(loc, occ));
				if (i > 8)
				{
					// e.g br index 9 gets mapped to rQlocs index 2
					rQlocs.get(i - 7).add(loc);
				}
			}
		}

		wPawnAtt = EngineUtils.multipleOr(getPawnAttacksFromLocs(state.getPieceLocations(0), Side.W));
		bPawnAtt = EngineUtils.multipleOr(getPawnAttacksFromLocs(state.getPieceLocations(6), Side.B));
	}


	public void evaluatePawnShield()
	{
		final byte castleStatus = state.getCastleStatus();
		whiteCastled = (castleStatus & 0b11) != 0;
		blackCastled = (castleStatus & 0b1100) != 0;

		evaluateKingPawnProtection(Side.W);
		evaluateKingPawnProtection(Side.B);
	}

	private void evaluateKingPawnProtection(final Side side)
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

	private void evaluateQueenMobility()
	{
		final TLongList wQueenAtts = atts.get(3), bQueenAtts = atts.get(7);

		for (int j = 0; j < wQueenAtts.size(); j++)
		{
			final int moveNum = Long.bitCount(wQueenAtts.get(j) & ~bPawnAtt);
			midEval += moveNum * MID_MOBILITY_SCORES.getQueen();
			endEval += moveNum * END_MOBILITY_SCORES.getQueen();
		}

		for (int j = 0; j < bQueenAtts.size(); j++)
		{
			final int moveNum = Long.bitCount(bQueenAtts.get(j) & ~wPawnAtt);
			midEval -= moveNum * MID_MOBILITY_SCORES.getQueen();
			endEval -= moveNum * END_MOBILITY_SCORES.getQueen();
		}
	}

	private void evaluateRookMobility()
	{
		final TLongList wRookAtts = atts.get(2), bRookAtts = atts.get(6);

		// Just treat horizontal and vertical the same for now.
		for (int j = 0; j < wRookAtts.size(); j++)
		{
			final int moveNum = Long.bitCount(wRookAtts.get(j) & ~bPawnAtt);
			midEval += moveNum * MID_MOBILITY_SCORES.getRookH();
			endEval += moveNum * END_MOBILITY_SCORES.getRookH();
		}

		for (int j = 0; j < bRookAtts.size(); j++)
		{
			final int moveNum = Long.bitCount(bRookAtts.get(j) & ~wPawnAtt);
			midEval -= moveNum * MID_MOBILITY_SCORES.getRookH();
			endEval -= moveNum * END_MOBILITY_SCORES.getRookH();
		}
	}

	private void evaluateKnightMobility()
	{
		final TLongList wKnightAtts = atts.get(1), bKnightAtts = atts.get(5);

		for (int j = 0; j < wKnightAtts.size(); j++)
		{
			final int moveNum = Long.bitCount(wKnightAtts.get(j) & ~bPawnAtt);
			midEval += moveNum * MID_MOBILITY_SCORES.getKnight();
			endEval += moveNum * END_MOBILITY_SCORES.getKnight();
		}

		for (int j = 0; j < bKnightAtts.size(); j++)
		{
			final int moveNum = Long.bitCount(bKnightAtts.get(j) & ~wPawnAtt);
			midEval -= moveNum * MID_MOBILITY_SCORES.getKnight();
			endEval -= moveNum * END_MOBILITY_SCORES.getKnight();
		}
	}

	private void evaluateBishopMobility()
	{
		final TLongList wBishopAtts = atts.get(0), bBishopAtts = atts.get(4);

		for (int j = 0; j < wBishopAtts.size(); j++)
		{
			final int moveNum = Long.bitCount(wBishopAtts.get(j) & ~bPawnAtt);
			midEval += moveNum * MID_MOBILITY_SCORES.getBishop();
			endEval += moveNum * END_MOBILITY_SCORES.getBishop();
		}

		for (int j = 0; j < bBishopAtts.size(); j++)
		{
			final int moveNum = Long.bitCount(bBishopAtts.get(j) & ~wPawnAtt);
			midEval -= moveNum * MID_MOBILITY_SCORES.getBishop();
			endEval -= moveNum * END_MOBILITY_SCORES.getBishop();
		}
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