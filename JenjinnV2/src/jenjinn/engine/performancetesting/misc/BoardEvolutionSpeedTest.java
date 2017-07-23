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
	public static void main(String[] args) throws IOException 
	{
		BufferedReader reader = Files.newBufferedReader(SRC_FILE_PATH, StandardCharsets.ISO_8859_1);
		
		TLongList times = new TLongArrayList();
		String game = "1.d4 Nf6 2.c4 g6 3.Nc3 Bg7 4.e4 d6 5.f3 O-O 6.Be3 e5 7.d5 Ne8 8.Qd2 f5 9.O-O-O f4 10.Bf2 Nd7 11.Nge2 Nb6 12.Qd3 g5 13.Kb1 Bd7 14.Nc1 c5 15.dxc6 bxc6 16.c5 Nc8 17.cxd6 Nexd6";
		while((game = reader.readLine()) != null)
		{
			AlgebraicCommand[] commands = ChessGameReader.processSequenceOfCommands(game);
			
			BoardState state = BoardStateImplV2.getStartBoard();
			
			for (AlgebraicCommand command : commands)
			{
				try
				{
					ChessMove mv = state.generateMove(command);
					long start = System.nanoTime();
					state = mv.evolve(state);
					times.add(System.nanoTime() - start);
//					System.out.println(state.getEnPassantSq());
//					state.print();
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
