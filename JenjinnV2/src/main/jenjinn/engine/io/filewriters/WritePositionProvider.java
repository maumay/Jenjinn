/**
 * Copyright � 2017 Lhasa Limited
 * File created: 14 Jul 2017 by thomasb
 * Creator : thomasb
 * Version : $Id$
 */
package jenjinn.engine.io.filewriters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import jenjinn.engine.io.pgnutils.PgnReader;

/**
 * @author thomasb
 * @since 14 Jul 2017
 */
public class WritePositionProvider
{
	private static final Path SRC_FILE_PATH = Paths.get("positionproviders", "Tal.pgn");

	private static final String OUTPUT_FOLDER = "positionproviders";

	private static final String OUTPUT_FILE_NAME = "talprovider500.txt";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException
	{
		final Path outputFilePath = Paths.get(OUTPUT_FOLDER, OUTPUT_FILE_NAME);
		final List<String> gameStrings = PgnReader.getGameStrings(SRC_FILE_PATH, 18, 500);
		Files.write(outputFilePath, gameStrings, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
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