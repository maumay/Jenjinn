package jenjinn.engine.evaluation;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.Side;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.pieces.ChessPiece;

public class SEE 
{
	
	private static final int[] ASCENDING_PVALUES = { 0, 2, 1, 3, 4, 5, 6, 8, 7, 9, 10, 11 };
	
	public SEE() {}
	
	
	private long targBB, fromset, occ, attadef, potenxray;
	
	public int eval(byte targ, byte from, BoardState state, short[] pieceValues)
	{
		// Make sure all instance variables set correctly first
		fromset = 1L << from;
		targBB = 1L << targ;
		occ = state.getOccupiedSquares();
		generateAttackDefenseInfo(state);
		
		long knightLocs = state.getPieceLocations(2)|state.getPieceLocations(8);
		Side fromSide = state.getFriendlySide();
		
		int d = 0;
		int[] gain = new int[32];
		gain[d] = pieceValues[state.getPieceAt(targ).index()];
		ChessPiece attPiece = state.getPieceFromBB(fromset);
		
		do
		{
			d++;
			fromSide = fromSide.otherSide();
			
			gain[d] = pieceValues[attPiece.index()] - gain[d - 1];
			if (Math.max(-gain[d - 1], gain[d]) < 0)
			{
				break;
			}
			
			attadef ^= fromset;
			occ ^= fromset;
			
			if ((fromset & knightLocs) == 0)
			{
				updateXrays(state);
			}
			fromset = getLeastValuablePiece(state.getPieceLocationsCopy(), fromSide);
			attPiece = state.getPieceFromBB(fromset);
		}
		while (fromset != 0);
		
		while (--d > 0)
		{
			gain[d - 1] = -Math.max(-gain[d - 1], gain[d]);
		}
		return gain[0];
	}
	
	private void updateXrays(BoardState state) 
	{
		for (byte loc : EngineUtils.getSetBits(potenxray))
		{
			ChessPiece p = state.getPieceAt(loc);
			if ((p.getAttackset(loc, occ) & targBB) != 0)
			{
				long locBB = 1L << loc;
				potenxray ^= locBB;
				attadef ^= locBB;
			}
		}
	}

	private void generateAttackDefenseInfo(BoardState state) 
	{
		attadef = 0L;
		potenxray = 0L;
		int ctr = 0;
		for (long locs : state.getPieceLocationsCopy())
		{
			ChessPiece p = ChessPiece.get(ctr);
			boolean canXray = p.canXray();

			for (byte loc : EngineUtils.getSetBits(locs))
			{
				long atts = p.getAttackset(loc, occ), xray = BBDB.EBA[p.index() + 1][loc];

				if ((atts & targBB) != 0)
				{
					attadef |= loc;
				}
				else if (canXray && (xray & targBB) != 0)
				{
					potenxray |= loc;
				}
			}
		}
	}

	private long getLeastValuablePiece(long[] pieceLocs, Side fromSide)
	{
		int startIdx = fromSide.index(), endIdx = startIdx + 6;
		for (int i = startIdx; i < endIdx; i++)
		{
			long subset = attadef & pieceLocs[ASCENDING_PVALUES[i]];
			if (subset != 0)
			{
				return (subset & ~subset);
			}
		}
		return 0L;
	}
}
