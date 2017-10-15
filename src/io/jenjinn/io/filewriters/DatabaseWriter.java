/**
 *
 */
package jenjinn.io.filewriters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import jenjinn.engine.enums.Side;
import jenjinn.io.pgnutils.PgnReader;

/**
 * @author ThomasB
 *
 *         Provides utilities for writing the db
 */
public class DatabaseWriter
{
	private static final String ZIP_EXT = ".zip";
	private static final int LENGTH_CAP = 16;

	private static final Side TO_WRITE_FOR = Side.W;


	private static final String DB_FOLDER = "dbresources";

	/**
	 *
	 */
	private DatabaseWriter()
	{
	}

	public static void writePgnFileToDbFormatTxtFile(final Path pgnFolder, final String outputFileName, final Side requiredSide) throws IOException
	{
		assert Files.isDirectory(pgnFolder) : "Folder not passed!";


		final List<Path> filePaths = Files.list(pgnFolder).collect(Collectors.toList());
		filePaths.sort((a, b) -> a.getFileName().compareTo(b.getFileName()));

		filePaths.forEach(x ->
		{
			assert x.getFileName().toString().endsWith(ZIP_EXT) : "Non zip file passed!";
		});

		final Path outFilePath = Paths.get(DB_FOLDER, outputFileName);
		//		Files.
		PgnReader.writeDBFile(filePaths, outFilePath, LENGTH_CAP, requiredSide);
	}

	public static void main(final String[] args) throws IOException
	{
		//		try
		//		{
		//			writePgnFileToDbFormatTxtFile("Karpov.pgn", "karpovdb.txt");
		//			writePgnFileToDbFormatTxtFile("Kasparov.pgn", "kasparovdb.txt");
		//			writePgnFileToDbFormatTxtFile("Petrosian.pgn", "petrosiandb.txt");
		//			writePgnFileToDbFormatTxtFile("Tal.pgn", "taldb.txt");
		//			writePgnFileToDbFormatTxtFile("Topalov.pgn", "topalovdb.txt");
		//		}
		//		catch (final IOException e)
		//		{
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		Files.list(Paths.get(DB_FOLDER)).forEach(x -> System.out.println(x.toString()));

		final String sideName = TO_WRITE_FOR.name().toLowerCase();

		final String[] names = {
				"modernkings",
				"classickings",
				"modernqueens",
				"classicqueens",
				"flank"
		};

		for (final String name : names)
		{
			System.out.println("Next");
			final Path folder = Paths.get("F:", "chessopenings", name);
			writePgnFileToDbFormatTxtFile(folder, sideName + name, TO_WRITE_FOR);
		}
	}
}
