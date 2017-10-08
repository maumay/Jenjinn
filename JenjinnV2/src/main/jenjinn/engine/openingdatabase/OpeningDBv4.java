/**
 * Copyright � 2017 Lhasa Limited
 * File created: 11 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.openingdatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.moves.ChessMove;

/**
 * @author ThomasB
 * @since 11 Jul 2017
 */
public class OpeningDBv4
{
	/** The source file path assumes the source file is in a source folder! */
	private final List<String> srcpaths = new ArrayList<>();

	public OpeningDBv4(final List<String> filenames)
	{
		srcpaths.addAll(filenames);
	}

	/**
	 * Read through the database file looking for a hashing match. If we find one we
	 * return the recorded move, if we don't a null value is returned.
	 *
	 * @param stateHashing
	 * @return
	 * @throws IOException
	 */
	public ChessMove getMoveForPosition(final long stateHashing) throws IOException
	{
		for (String s : srcpaths)
		{
			try (final InputStream dbStream = getClass().getResourceAsStream(s))
			{
				assert dbStream != null && new InputStreamReader(dbStream) != null;

				final BufferedReader reader = new BufferedReader(new InputStreamReader(dbStream));

				String line;
				while ((line = reader.readLine()) != null)
				{
					final String[] components = line.split(" ");

					if (new BigInteger(components[0], 16).longValue() == stateHashing)
					{
						return ChessMove.fromCompactString(components[1]);
					}
				}
			}
		}
		return null;
	}

	public ChessMove getMoveForPosition(final BoardState state) throws IOException
	{
		return getMoveForPosition(state.getHashing());
	}

//	public static void main(final String[] args)
//	{
//		final OpeningDBv4 db = new OpeningDBv4("testdb.txt");
//		BoardState state = BoardStateImplV2.getStartBoard();
//
//		state = StandardMove.get(Sq.e2, Sq.e4).evolve(state);
//		state = StandardMove.get(Sq.e7, Sq.e5).evolve(state);
//
//		try
//		{
//			System.out.println(db.getMoveForPosition(state.getHashing()).toString());
//		}
//		catch (final IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
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