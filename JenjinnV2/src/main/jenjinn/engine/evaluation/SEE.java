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
		targBB = 1L << targ;
		occ = state.getOccupiedSquares();
		generateAttackDefenseInfo(state);
		
		int d = 0, moveSide = state.getFriendlySideValue();
		int[] gain = new int[32];
		gain[d] = pieceValues[state.getPieceAt(targ).index()];
		ChessPiece attPiece = state.getPieceFromBB(fromset);
		
		do
		{
			d++;
			gain[d] = pieceValues[attPiece.index()] - gain[d - 1];
			attadef ^= fromset;
			occ ^= fromset;
			
		}
		while (false);
		
		return 0;
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

	private long getLeastValuablePiece(long[] pieceLocs, long attadef, Side fromSide)
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
