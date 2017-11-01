/**
 * Copyright © 2017 Lhasa Limited
 * File created: 7 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.zobristhashing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import jenjinn.engine.enums.Sq;

/**
 * Writes a file containing all feature keys we need to create a Zobrist hashing of
 * a board state. We generate them and store them in a file so that performance can
 * be tested. Some seeds will create better hashing keys than others.
 *
 * @author ThomasB
 * @since 7 Jul 2017
 */
public class RandomZobristHashingWriter
{
	/** The random seed generator, it is vital that for reproducibility we record that start seed. */
	private static final Random SEED_GENERATOR = new Random();

	private static final String EXT = ".csv", SEPARATOR = ",";

	/** The number of unique keys we need to generate. */
	private static final int EXPECTED_UNIQUE_FEATURE_COUNT = 781;

	/**
	 * Write the .csv file containing our hashing keys.
	 *
	 * @throws IOException
	 */
	private static void writeHashingFile() throws IOException
	{
		final String fileName = LocalDateTime.now().toString().replace(":", "-").replace(".", "_") + EXT;

		final Path filePath = Paths.get(new File("generatedhashings").getAbsolutePath(), fileName);

		final long hashingSeed = SEED_GENERATOR.nextLong();

		final Random hashGenerator = new Random(hashingSeed);

		final TLongSet generatedValues = new TLongHashSet();

		final List<String> fileLines = new ArrayList<>(Arrays.asList(Long.toHexString(hashingSeed)));

		for (@SuppressWarnings("unused")
		final Sq chessSquare : Sq.values())
		{
			fileLines.add(generateSquarePieceHashes(hashGenerator, generatedValues));
		}

		addAuxillaryFeatures(hashGenerator, fileLines, generatedValues);

		if (generatedValues.size() != EXPECTED_UNIQUE_FEATURE_COUNT)
		{
			throw new RuntimeException();
		}

		Files.write(filePath, fileLines, StandardOpenOption.CREATE);
	}

	/**
	 * Generates and add the features for:
	 * - side to move is black
	 * - castling rights
	 * - enpassant file
	 *
	 * @param hashGenerator
	 * @param fileLines
	 * @param generatedValues
	 */
	private static void addAuxillaryFeatures(final Random hashGenerator, final List<String> fileLines, final TLongSet generatedValues)
	{
		// Black to move feature
		fileLines.add(generateHash(hashGenerator, generatedValues));

		// Castling rights.
		final StringBuilder castlingFeatures = new StringBuilder();
		for (int i = 0; i < 4; i++)
		{
			castlingFeatures.append(generateHash(hashGenerator, generatedValues));

			if (i < 3)
			{
				castlingFeatures.append(SEPARATOR);
			}
		}
		fileLines.add(castlingFeatures.toString());

		// Enpassant possible file
		final StringBuilder enPassantFeatures = new StringBuilder();
		for (int i = 0; i < 8; i++)
		{
			enPassantFeatures.append(generateHash(hashGenerator, generatedValues));

			if (i < 7)
			{
				enPassantFeatures.append(SEPARATOR);
			}
		}
		fileLines.add(enPassantFeatures.toString());
	}

	/**
	 * Generates a random long using the parameter {@link Random}, stores it in the parameter set and
	 * returns the long as a hex string.
	 */
	private static String generateHash(final Random hashGenerator, final TLongSet valueStore)
	{
		final long n = hashGenerator.nextLong();
		valueStore.add(n);
		return Long.toHexString(n);
	}

	private static String generateSquarePieceHashes(final Random hashGenerator, final TLongSet generatedValues)
	{
		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 12; i++)
		{
			sb.append(generateHash(hashGenerator, generatedValues));

			if (i < 11)
			{
				sb.append(SEPARATOR);
			}
		}
		return sb.toString();
	}

	/**
	 * Generate 100 random files for testing
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args)
	{
		for (int i = 0; i < 100; i++)
		{
			try
			{
				writeHashingFile();
			}
			catch (final IOException e)
			{
				e.printStackTrace();
				System.err.println("IO Error, terminating process.");
				break;
			}
			catch (final RuntimeException re)
			{
				// We didn't generate unique keys so try again.
				i--;
				continue;
			}
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