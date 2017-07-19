/**
 * Copyright � 2017 Lhasa Limited
 * File created: 7 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.zobristhashing;

import java.util.Random;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author ThomasB
 * @since 7 Jul 2017
 */
public class ZobristHasher
{
	private static final long DEFAULT_SEED = 0x110894L;

	private long[][] squarePieceFeatures = new long[64][12];

	private long[] castleFeatures = new long[4];

	private long blackToMove;

	private long[] enPassantFileFeatures = new long[8];

	private ZobristHasher()
	{
	}

	public static final ZobristHasher getDefault()
	{
		return getFrom(DEFAULT_SEED);
	}

	public static final ZobristHasher getFrom(final long seed)
	{
		if (!seedIsValid(seed))
		{
			throw new AssertionError();
		}

		final ZobristHasher hasher = new ZobristHasher();

		final Random r = new Random(seed);

		addSquarePieceFeatures(r, hasher);
		addAuxilaryFeatures(r, hasher);

		return hasher;
	}

	private static void addAuxilaryFeatures(final Random r, final ZobristHasher hasher)
	{
		for (int i = 0; i < 4; i++)
		{
			hasher.castleFeatures[i] = r.nextLong();
		}

		hasher.blackToMove = r.nextLong();

		for (int i = 0; i < 8; i++)
		{
			hasher.enPassantFileFeatures[i] = r.nextLong();
		}
	}

	private static void addSquarePieceFeatures(final Random r, final ZobristHasher hasher)
	{
		int i = 0;
		for (@SuppressWarnings("unused")
		final Sq sq : Sq.values())
		{
			for (int j = 0; j < 12; j++)
			{
				hasher.squarePieceFeatures[i][j] = r.nextLong();
			}
			i++;
		}
	}

	private static boolean seedIsValid(final long seed)
	{
		final Random r = new Random(seed);
		final TLongSet values = new TLongHashSet();
		for (int i = 0; i < 800; i++)
		{
			values.add(r.nextLong());
		}
		return values.size() == 800;
	}

	public long getSquarePieceFeature(final byte loc, final ChessPiece piece)
	{
		return squarePieceFeatures[loc][piece.getId()];
	}

	public long getCastleFeature(final int i)
	{
		return castleFeatures[i];
	}

	public long getEnpassantFeature(final int fileNum)
	{
		return enPassantFileFeatures[fileNum];
	}

	public long getBlackToMove()
	{
		return blackToMove;
	}

	public static void main(final String[] args)
	{
		getDefault();
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