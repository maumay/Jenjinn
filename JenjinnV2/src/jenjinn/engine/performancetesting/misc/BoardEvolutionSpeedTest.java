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
public class BoardEvolutionSpeedTest
{
	private static final Path SRC_FILE_PATH = Paths.get("JenjinnV2","positionproviders", "talprovider500.txt");//Paths.get("positionproviders", "talprovider500.txt");// ,
																									// 

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException
	{
		final BufferedReader reader = Files.newBufferedReader(SRC_FILE_PATH, StandardCharsets.ISO_8859_1);

		final TLongList times = new TLongArrayList();
		String game;// = "1.d4 Nf6 2.c4 e6 3.Nf3 d5 4.Nc3 c6 5.e3 Nbd7 6.Bd3 Bb4 7.a3 Ba5 8.O-O O-O 9.Ne5 Nxe5 10.dxe5 dxc4 11.Bxc4 Nd7 12.f4 Qe7 13.b4 Bb6 14.Qb3 f6 15.Bxe6+ Kh8 16.Ne4 fxe5 17.Kh1 exf4";

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
					final long start = System.nanoTime();
					state = mv.evolve(state);
					times.add(System.nanoTime() - start);
//					 System.out.println(state.getEnPassantSq());
//					 state.print();
				}
				catch (final AmbiguousPgnException err)
				{
//					System.out.println("-----------------------------------");
//					System.out.println("APE with game: " + game);
//					System.out.println("-----------------------------------");
					break;
				}
				catch (final NullPointerException npe)
				{
					System.out.println("NPE with game: " + game);
					System.out.println("at command: " + command.getAsString());
					break;
				}
				catch (AssertionError err)
				{
					System.out.println(game);
					throw err;
				}
			}
		}

		System.out.println(EngineUtils.average(Arrays.asList(times)).get(0));
	}

}
