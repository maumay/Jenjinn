package jenjinn.engine.pgnutils;

import java.util.ArrayList;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImplV2;
import jenjinn.engine.exceptions.AmbiguousPgnException;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.openingdatabase.AlgebraicCommand;
import jenjinn.engine.openingdatabase.OpeningOrder;

/**
 * Takes a string of algebraic notation and puts the opening into the database
 */
public class ChessGameReader
{
	public ChessGameReader()
	{
	};

	// public OpeningOrder[] convertFullGame(final String s)
	// {
	// }

	public static OpeningOrder[] convertAlgebraicString(final String s, final int lengthCap) throws AmbiguousPgnException
	{
		final ArrayList<OpeningOrder> ans = new ArrayList<>();
		final String trimmed = s.trim();
		final AlgebraicCommand[] commands = processSequenceOfCommands(trimmed);
		BoardState state = BoardStateImplV2.getStartBoard();

		int counter = 0;
		for (final AlgebraicCommand a : commands)
		{
			if (counter > lengthCap)
			{
				break;
			}

			final ChessMove temp = state.generateMove(a);
			ans.add(new OpeningOrder(state.getHashing(), temp));
			state = temp.evolve(state);
			counter++;
		}
		return ans.toArray(new OpeningOrder[ans.size()]);
	}

	public static AlgebraicCommand[] processSequenceOfCommands(final String trimmed)
	{
		final String[] initialSplit = trimmed.split(" ");
		final AlgebraicCommand[] finalSplit = new AlgebraicCommand[initialSplit.length];
		int idx = 0;
		for (final String s : initialSplit)
		{
			finalSplit[idx] = new AlgebraicCommand(s);
			idx++;
		}
		return finalSplit;
	}

}
