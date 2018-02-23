package jenjinn.io.filewriters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import jenjinn.io.pgnutils.PgnReader;

/**
 * @author thomasb
 * @since 14 Jul 2017
 */
public class WritePositionProvider
{
	private static final String FILENAME = "Topalov.pgn";
	
	private static final String OUTPUT_FILE_NAME = "topalovprovider.txt";
	
	private static final Path SRC_FILE_PATH = Paths.get("JenjinnV2", "pgnfiles", FILENAME);

	private static final String OUTPUT_FOLDER = "positionproviders";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException
	{
		final Path outputFilePath = Paths.get("JenjinnV2", OUTPUT_FOLDER, OUTPUT_FILE_NAME);
		final List<String> gameStrings = PgnReader.getGameStrings(SRC_FILE_PATH, 100, 3000);
		Files.write(outputFilePath, gameStrings, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}
}
