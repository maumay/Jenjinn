/**
 *
 */
package jenjinn.engine.performancetesting.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImplV2;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.openingdatabase.AlgebraicCommand;
import jenjinn.engine.pgnutils.ChessGameReader;

/**
 * @author t
 *
 */
public class BoardEvolutionSpeedTest
{
	private static final Path SRC_FILE_PATH = Paths.get("positionproviders", "talprovider500.txt");

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException
	{
		final BufferedReader reader = Files.newBufferedReader(SRC_FILE_PATH, StandardCharsets.ISO_8859_1);

		final TLongList times = new TLongArrayList();
		String game;// = "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e5 6.Bb5+ Bd7 7.Bxd7+ Qxd7 8.Nf5 Nc6 9.O-O Nxe4 10.Nxe4 Qxf5 11.Nxd6+ Bxd6 12.Qxd6
					// Qd7 13.Qc5 Qe7 14.Be3 Qxc5 15.Bxc5 O-O-O 16.Rfd1 b6 17.Be3 Rd7";
		while ((game = reader.readLine()) != null)
		{
			final AlgebraicCommand[] commands = ChessGameReader.processSequenceOfCommands(game);

			BoardState state = BoardStateImplV2.getStartBoard();

			for (final AlgebraicCommand command : commands)
			{
				try
				{
					final ChessMove mv = state.generateMove(command);
					final long start = System.nanoTime();
					state = mv.evolve(state);
					times.add(System.nanoTime() - start);
					// System.out.println(state.getEnPassantSq());
					// state.print();
				}
				catch (final AssertionError err)
				{
					System.out.println("AE with game: " + game);
					break;
				}
				catch (final NullPointerException npe)
				{
					System.out.println("NPE with game: " + game);
					System.out.println("at command: " + command.getAsString());
					break;
				}
			}

		}

		System.out.println(EngineUtils.average(Arrays.asList(times)).get(0));
	}

}
