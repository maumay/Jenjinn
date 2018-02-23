package jenjinn.test.boardrepresentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImpl;
import jenjinn.engine.enums.Side;
import jenjinn.engine.exceptions.AmbiguousPgnException;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.openingdatabase.AlgebraicCommand;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.io.pgnutils.ChessGameReader;
import jenjinn.testingengine.boardstate.TBoardState;

/**
 * @author ThomasB
 * @since 20 Sep 2017
 */
public class BoardStateComparisonTest
{
	private static final String[] POSITIONPROVIDERS = { "carlsenprovider.txt", "fischerprovider.txt", "grischukprovider.txt", "karpovprovider.txt", "kasparovprovider.txt", "petrosianprovider.txt", "talprovider.txt", "topalovprovider.txt" };

	@Test
	public void test()
	{
		for (final String posProvider : POSITIONPROVIDERS) {
			try {
				final InputStream is = getClass().getResourceAsStream("/" + posProvider);
				final BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, StandardCharsets.ISO_8859_1));

				String game;
				while ((game = reader.readLine()) != null) {
					try {
						testGame(game);
					}
					catch (final AmbiguousPgnException e) {
						System.err.println(
								"AmbiguousPgnException detected:\nAt command: " + e.getMessage() + "\nIn game: " + game);
					}
				}
			}
			catch (final IOException e) {
				e.printStackTrace();
				fail("ioexception");
			}
		}
	}

	private void testGame(final String game) throws AmbiguousPgnException
	{
		final AlgebraicCommand[] commands = ChessGameReader.processSequenceOfCommands(game.trim());
		BoardState constraint = TBoardState.getStartBoard(), toTest = BoardStateImpl.getStartBoard();
		testProperties(constraint, toTest, "");

		for (final AlgebraicCommand com : commands) {
			final String errorOutput = com.getAsString() + " in: " + game;
			final ChessMove conMv = constraint.generateMove(com), testMv = toTest.generateMove(com);
			assertEquals(conMv.toString(), testMv.toString());// , "Different moves generated");

			toTest = testMv.evolve(toTest);
			constraint = conMv.evolve(constraint);

			testProperties(constraint, toTest, errorOutput);
		}
	}

	private void testProperties(final BoardState cons, final BoardState test, final String errorOutput)
	{
		assertEquals(errorOutput, cons.getFriendlySideValue(), test.getFriendlySideValue());
		assertEquals(errorOutput, cons.getFriendlySide(), test.getFriendlySide());
		assertEquals(errorOutput, cons.getEnemySide(), test.getEnemySide());
		assertEquals(errorOutput, cons.getCastleStatus(), test.getCastleStatus());
		assertEquals(errorOutput, cons.getCastleRights(), test.getCastleRights());
		assertEquals(errorOutput, cons.getClockValue(), test.getClockValue());

		for (byte i = 0; i < 64; i++) {
			ChessPiece conP = cons.getPieceAt(i), testP = test.getPieceAt(i);
			assertTrue(errorOutput,
					(conP == null && testP == null) || ((conP != null && testP != null) && conP.index() == testP.index()));

			conP = cons.getPieceAt(i, Side.W);
			testP = test.getPieceAt(i, Side.W);
			assertTrue(errorOutput,
					(conP == null && testP == null) || ((conP != null && testP != null) && conP.index() == testP.index()));

			conP = cons.getPieceAt(i, Side.B);
			testP = test.getPieceAt(i, Side.B);
			assertTrue("" + i + errorOutput,
					(conP == null && testP == null) || ((conP != null && testP != null) && conP.index() == testP.index()));
		}

		Assert.assertArrayEquals(errorOutput, cons.getPieceLocationsCopy(), test.getPieceLocationsCopy());

		assertEquals(errorOutput, cons.getSideLocations(Side.W), test.getSideLocations(Side.W));
		assertEquals(errorOutput, cons.getSideLocations(Side.B), test.getSideLocations(Side.B));
		assertEquals(errorOutput, cons.getOccupiedSquares(), test.getOccupiedSquares());
		assertEquals(errorOutput, cons.getPiecePhase(), test.getPiecePhase());
		assertEquals(errorOutput, cons.getDevelopmentStatus(), test.getDevelopmentStatus());
		assertEquals(errorOutput, cons.getEnPassantSq(), test.getEnPassantSq());
		assertEquals(errorOutput, cons.getMidgamePositionalEval(), test.getMidgamePositionalEval());
		assertEquals(errorOutput, cons.getEndgamePositionalEval(), test.getEndgamePositionalEval());

		assertEquals(errorOutput, cons.getSquaresAttackedBy(Side.W), test.getSquaresAttackedBy(Side.W));
		assertEquals(errorOutput, cons.getSquaresAttackedBy(Side.B), test.getSquaresAttackedBy(Side.B));

		Set<String> cMoves = cons.getMoves().stream().map(x -> x.toString()).collect(Collectors.toSet());
		Set<String> tMoves = test.getMoves().stream().map(x -> x.toString()).collect(Collectors.toSet());
		boolean sameMoves = cMoves.containsAll(tMoves) && tMoves.containsAll(cMoves);

		assertTrue(
				errorOutput + "\n\n" + "Constraint has " + cMoves.size() + " moves:" + cMoves.toString() + "\n\nTotest has " + tMoves.size() + " moves:" + tMoves.toString(),
				sameMoves);

		cMoves = cons.getAttackMoves().stream().map(x -> x.toString()).collect(Collectors.toSet());
		tMoves = test.getAttackMoves().stream().map(x -> x.toString()).collect(Collectors.toSet());
		sameMoves = cMoves.containsAll(tMoves) && tMoves.containsAll(cMoves);

		assertTrue(
				errorOutput + "\n\n" + "Constraint has " + cMoves.size() + " moves:" + cMoves.toString() + "\n\nTotest has " + tMoves.size() + " moves:" + tMoves.toString(),
				sameMoves);

		assertEquals(errorOutput, cons.getTerminationState(), test.getTerminationState());
		assertEquals(errorOutput, cons.getHashing(), test.getHashing());
		Assert.assertArrayEquals(errorOutput, cons.getHashes(), test.getHashes());
	}
}