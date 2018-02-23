package jenjinn.io.pgnutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import jenjinn.engine.enums.Side;
import jenjinn.engine.exceptions.AmbiguousPgnException;
import jenjinn.engine.openingdatabase.OpeningOrder;

/**
 * Needed to make this class a bit more rigorous.
 *
 * @author ThomasB
 *
 */
public class PgnReader
{
	private static final String ZIP_EXT = ".zip";

	private static final String WHITE_WINS = "1-0";
	private static final String DRAW = "1/2-1/2";
	private static final String BLACK_WINS = "0-1";

	public static List<String> getGameStrings(final Path filePath, final int moveCap, final int gameCap) throws IOException
	{
		return getGameStrings(getReader(filePath), moveCap, gameCap);
	}

	private static BufferedReader getReader(final Path filePath) throws IOException
	{
		return Files.newBufferedReader(filePath, StandardCharsets.ISO_8859_1);
	}

	private static List<String> getGameStrings(final BufferedReader fileParser, final int moveCap, final int gameCap)
	{
		// First read whole file and remove unecessary lines:
		final List<String> wholeFile = fileParser.lines().map(x -> x.trim()).filter(
				x -> !(x.isEmpty() || x.contains("["))).collect(Collectors.toList());

		// Now concatenate lines to form full game strings.
		final List<StringBuilder> gameBuilder = new ArrayList<>();

		final Consumer<String> fileLineAction = x -> {
			if (x.substring(0, 2).equals("1.")) {
				gameBuilder.add(new StringBuilder(x + " "));
			}
			else {
				// try
				// {
				gameBuilder.get(gameBuilder.size() - 1).append(x + " ");
				// }
				// catch (ArrayIndexOutOfBoundsException e)
				// {
				// System.out.println(x);
				// throw new AssertionError();
				// }
			}
		};

		wholeFile.stream().forEach(fileLineAction);

		List<String> games = gameBuilder.stream().map(x -> removeGameResult(x.toString().trim())).filter(
				x -> !x.isEmpty()).collect(Collectors.toList());

		if (gameCap > -1) {
			games = games.subList(0, Math.min(gameCap, games.size()));
		}
		if (moveCap > 1) {
			final String sMoveCap = "" + moveCap;

			for (int i = 0; i < games.size(); i++) {
				final String game = games.get(i);

				if (game.contains(sMoveCap)) {
					games.set(i, game.substring(0, game.indexOf(sMoveCap)).trim());
				}
			}
		}
		return games;
	}

	private static String removeGameResult(final String game)
	{
		if (game.contains(WHITE_WINS)) {
			return game.substring(0, game.indexOf(WHITE_WINS)).trim();
		}
		else if (game.contains(DRAW)) {
			return game.substring(0, game.indexOf(DRAW)).trim();
		}
		else if (game.contains(BLACK_WINS)) {
			return game.substring(0, game.indexOf(BLACK_WINS)).trim();
		}
		else {
			System.err.println("--------------------------");
			System.err.println("Invalid game result for game:");
			System.err.println(game);
			System.err.println("game skipped.");
			System.err.println("--------------------------");
			return "";
		}
	}

	public static void writeDBFile(final List<Path> fileName, final Path outPath, final int lengthCap, final Side toInclude) throws IOException
	{
		final Set<Long> positionsUsed = new HashSet<>();

		// final String outFileName = outPath.getFileName().toString();
		final Path outParentFolder = outPath.getParent();
		final String passedName = outPath.getFileName().toString();

		final Path zipPath = Paths.get(outParentFolder.toString(), passedName + ZIP_EXT);
		final File f = new File(zipPath.toAbsolutePath().toString());

		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f))) {
			for (final Path p : fileName) {
				final File zipinput = new File(p.toAbsolutePath().toString());

				try (ZipFile zipsrc = new ZipFile(zipinput)) {
					final Enumeration<? extends ZipEntry> entries = zipsrc.entries();
					while (entries.hasMoreElements()) {
						final ZipEntry src = entries.nextElement();
						final Path tempfile = Paths.get(outParentFolder.toString(), "temp");

						final InputStream is = zipsrc.getInputStream(src);
						final BufferedReader fileParser = new BufferedReader(new InputStreamReader(is));

						List<String> gameStrings = null;
						gameStrings = getGameStrings(fileParser, -1, -1);

						/*
						 * This consumer checks if this OpeningOrder is unique and if so adds it to the
						 * List of orders read.
						 */
						final Consumer<OpeningOrder> orderAction = x -> {
							if (!positionsUsed.contains(x.getBoardHash())) {
								positionsUsed.add(x.getBoardHash());
								try {
									Files.write(tempfile, Arrays.asList(x.toDatabaseString()),
											StandardOpenOption.CREATE, StandardOpenOption.WRITE,
											StandardOpenOption.APPEND);
								}
								catch (final IOException e) {
									e.printStackTrace();
									throw new AssertionError();
								}
							}
						};

						// Convert to OpeningOrders and add as appropriate
						for (final String gs : gameStrings) {
							OpeningOrder[] game = null;
							try {
								game = ChessGameReader.convertAlgebraicString(gs.split("  ")[0], lengthCap);

								final List<OpeningOrder> requiredSide = new ArrayList<>();
								for (int i = toInclude.ordinal(); i < game.length; i += 2) {
									requiredSide.add(game[i]);
								}
								requiredSide.stream().forEach(orderAction);
							}
							catch (final AmbiguousPgnException e) {
								System.err.println("Skipped ambiguous game.");
								continue;
							}
						}

						if (Files.exists(tempfile, LinkOption.NOFOLLOW_LINKS)) {
							out.putNextEntry(new ZipEntry(src.getName()));
							final byte[] toWrite = Files.readAllBytes(tempfile);
							out.write(toWrite, 0, toWrite.length);
							out.closeEntry();
							Files.delete(tempfile);
						}
					}
				}
			}

		}
	}

	public static void main(final String[] args)
	{
		// String[] test = new String[]{
		// "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Be7 6.Re1 b5 7.Bb3 O-O 8.c3 d5",
		// "9.exd5 Nxd5 10.d4 exd4 11.cxd4 Bb4 12.Bd2 Bg4 13.Nc3 Nf6 14.Be3 Bxf3 15.gxf3
		// Qd7 1-0"
		// };
		// System.out.println(processCollectedStrings(Arrays.asList(test), false,
		// 12).length);
		// BufferedReader br = new BufferedReader(new
		// InputStreamReader(PgnReader.class.getResourceAsStream("testing.txt")));
		// OpeningOrder[] arr;
		// try
		// {
		// arr = processFile("testing.txt", false, 20);
		// System.out.println(arr.length);
		// } catch (IOException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// try
		// {
		// processFileForOpeningOrders("RuyLopezMarshall.pgn", 30);
		// }
		// catch (final IOException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

}
