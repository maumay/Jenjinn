/**
 *
 */
package jenjinn.engine.entity;

import java.io.IOException;
import java.util.Arrays;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.Side;
import jenjinn.engine.evaluation.BoardEvaluator;
import jenjinn.engine.gametree.MoveCalculator;
import jenjinn.engine.gametree.NegaAlphaBeta;
import jenjinn.engine.gametree.TTAlphaBetaV1_2;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.openingdatabase.OpeningDBv4;
import jenjinn.engine.openingdatabase.Openings;

/**
 * @author TB
 * @date 8 Feb 2017
 *
 */
public final class Jenjinn
{
	public final Side side;
	private final MoveCalculator gts;
	private final OpeningDBv4 openings;

	private int outsideOpeningCounter = 0;

	public Jenjinn(final Side side, final int plyCount, final BoardEvaluator evaluator, final String openingdbFileName)
	{
		this.side = side;
		// gts = new TTAlphaBetaV1_2(evaluator);
		gts = new TTAlphaBetaV1_2(evaluator);
		openings = new OpeningDBv4(Openings.getQualifyedNames());
	}

	public ChessMove calculateBestMove(final BoardState root)
	{
		ChessMove bestMove = null;

		if (outsideOpeningCounter < 5)
		{
			try
			{
				bestMove = openings.getMoveForPosition(root);
			}
			catch (final IOException e)
			{
				e.printStackTrace();
				throw new RuntimeException();
			}
		}

		if (bestMove == null)
		{
			bestMove = gts.getBestMoveFrom(root);
			outsideOpeningCounter++;
		}
		else
		{
			outsideOpeningCounter = 0;
		}

		return bestMove;
	}

	public static void main(final String[] args)
	{
		// final ChessMove[] mvs = {
		// StandardMove.get(e2, e4),
		// StandardMove.get(e7, e5),
		// StandardMove.get(g1, f3),
		// StandardMove.get(d7, d5),
		// StandardMove.get(f1, e2),
		// StandardMove.get(d5, d4),
		// CastleMove.WHITE_KINGSIDE,
		// StandardMove.get(c8, e6),
		// StandardMove.get(c2, c4),
		// EnPassantMove.get(d4, c3),
		// StandardMove.get(b1, c3),
		// StandardMove.get(b8, c6),
		// StandardMove.get(d1, c2),
		// StandardMove.get(d8, d7),
		// StandardMove.get(a2, a4),
		// CastleMove.BLACK_QUEENSIDE
		// };
		//
		// final Jenjinn jenjinn = new Jenjinn(Side.W, 5, new V3_0(), null);
		// int numOfMovesMade = 0;
		// BoardState state = BoardState.generateStartBoard();
		// final long start = System.nanoTime();
		//
		// for (final ChessMove mv : mvs)
		// {
		// state = mv.evolveBoard(state);
		// if (state.sideToMove == jenjinn.side)
		// {
		// jenjinn.calculateBestMove(state);
		// numOfMovesMade++;
		// }
		// }
		// final long timeTakenInNs = System.nanoTime() - start;
		// final BigDecimal inNano = new BigDecimal(timeTakenInNs);
		// final BigDecimal ONE_BILLION = new BigDecimal((int) Math.pow(10, 9));
		// final String inSeconds = inNano.divide(ONE_BILLION).toPlainString() + " seconds";
		// System.out.println(numOfMovesMade + " moves made in " + inSeconds);
	}
}
