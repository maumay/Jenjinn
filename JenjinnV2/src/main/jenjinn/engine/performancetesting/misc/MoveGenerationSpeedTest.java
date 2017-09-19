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
import jenjinn.engine.exceptions.AmbiguousPgnException;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.openingdatabase.AlgebraicCommand;
import jenjinn.engine.pgnutils.ChessGameReader;

/**
 * @author t
 *
 */
public class MoveGenerationSpeedTest
{

	private static final Path SRC_FILE_PATH = Paths.get("JenjinnV2", "positionproviders", "talprovider500.txt");// Paths.get("positionproviders",
																												// "talprovider500.txt");// ,

	/**
	 * @throws IOException
	 *
	 */
	public static void main(final String[] args) throws IOException
	{
		final BufferedReader reader = Files.newBufferedReader(SRC_FILE_PATH, StandardCharsets.ISO_8859_1);

		final TLongList times = new TLongArrayList();
		String game;// = "1.c4 Nf6 2.Nc3 d6 3.d4 g6 4.e4 Bg7 5.Be2 e5 6.dxe5 dxe5 7.Qxd8+ Kxd8 8.f4 Be6 9.Nf3 Nbd7 10.O-O c6 11.Ng5 h6 12.f5 hxg5 13.fxe6 fxe6
					// 14.Bxg5 Ke7 15.Rad1 Nf8 16.Rd3 Nh7 17.Be3 Rhd8";
		while ((game = reader.readLine()) != null)
		{
			final AlgebraicCommand[] commands = ChessGameReader.processSequenceOfCommands(game);

			BoardState state = BoardStateImplV2.getStartBoard();

			for (final AlgebraicCommand command : commands)
			{
				try
				{
					final String comStr = command.getAsString();
					final ChessMove mv = state.generateMove(command);
					state = mv.evolve(state);
					final long start = System.nanoTime();
					state.getMoves();
					times.add(System.nanoTime() - start);
					// System.out.println(state.getEnPassantSq());
					// state.print();
				}
				catch (final AmbiguousPgnException err)
				{
					// System.out.println("-----------------------------------");
					// System.out.println("APE with game: " + game);
					// System.out.println("-----------------------------------");
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
