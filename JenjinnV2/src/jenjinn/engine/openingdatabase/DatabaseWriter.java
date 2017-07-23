/**
 *
 */
package jenjinn.engine.openingdatabase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jenjinn.engine.pgnutils.PgnReader;

/**
 * @author ThomasB
 *
 *         Provides utilities for writing the db
 */
public class DatabaseWriter
{
	private static final boolean dontIncludeLosingPos = false;
	private static final int LENGTH_CAP = 10;

	private static final String DB_FOLDER = "databases";

	/**
	 *
	 */
	private DatabaseWriter()
	{
	}

	public static void writePgnFileToDbFormatTxtFile(final String inputFileName, final String outputFileName) throws IOException
	{
		final Path filePath = Paths.get(DB_FOLDER, outputFileName);
		final OpeningOrder[] ordersToWrite = PgnReader.processFileForOpeningOrders(inputFileName, LENGTH_CAP);
		final List<String> ordersAsStrings = Arrays.stream(ordersToWrite).map(x -> x.toDatabaseString()).collect(Collectors.toList());
		Files.write(filePath, ordersAsStrings, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	}

	public static void main(final String[] args)
	{
		try
		{
			writePgnFileToDbFormatTxtFile("RuyLopezMarshall.pgn", "testdb.txt");
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
