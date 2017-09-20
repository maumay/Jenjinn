/**
 * Copyright © 2017 Lhasa Limited
 * File created: 20 Sep 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.test.boardrepresentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.boardstate.BoardStateImplV2;
import jenjinn.engine.enums.Side;
import jenjinn.engine.exceptions.AmbiguousPgnException;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.openingdatabase.AlgebraicCommand;
import jenjinn.engine.pgnutils.ChessGameReader;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.testingengine.boardstate.TBoardState;

/**
 * @author ThomasB
 * @since 20 Sep 2017
 */
public class BoardStateComparisonTest
{
	private static final String[] POSITIONPROVIDERS = { "talprovider500.txt" };
	private static final String PROVIDER_FOLDER = "positionproviders";

	@Test
	public void test()
	{
		for (final String posProvider : POSITIONPROVIDERS)
		{
			try
			{
				final BufferedReader reader = Files.newBufferedReader(
						Paths.get(PROVIDER_FOLDER, posProvider), StandardCharsets.ISO_8859_1);

				String game;
				while ((game = reader.readLine()) != null)
				{

					try
					{
						testGame(game);
					}
					catch (final AmbiguousPgnException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			catch (final IOException e)
			{
				e.printStackTrace();
				fail("ioexception");
			}
		}
	}

	private void testGame(final String game) throws AmbiguousPgnException
	{
		final AlgebraicCommand[] commands = ChessGameReader.processSequenceOfCommands(game.trim());
		BoardState constraint = TBoardState.getStartBoard(), toTest = BoardStateImplV2.getStartBoard();

		for (final AlgebraicCommand com : commands)
		{
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

		for (byte i = 0; i < 64; i++)
		{
			ChessPiece conP = cons.getPieceAt(i), testP = test.getPieceAt(i);
			assertTrue(errorOutput, (conP == null && testP == null) || ((conP != null && testP != null) && conP.getIndex() == testP.getIndex()));

			conP = cons.getPieceAt(i, Side.W);
			testP = test.getPieceAt(i, Side.W);
			assertTrue(errorOutput, (conP == null && testP == null) || ((conP != null && testP != null) && conP.getIndex() == testP.getIndex()));

			conP = cons.getPieceAt(i, Side.B);
			testP = test.getPieceAt(i, Side.B);
			assertTrue(errorOutput, (conP == null && testP == null) || ((conP != null && testP != null) && conP.getIndex() == testP.getIndex()));
		}

		Assert.assertArrayEquals(errorOutput, cons.getPieceLocationsCopy(), test.getPieceLocationsCopy());

		assertEquals(errorOutput, cons.getSideLocations(Side.W), test.getSideLocations(Side.W));
		assertEquals(errorOutput, cons.getSideLocations(Side.B), test.getSideLocations(Side.B));
		assertEquals(errorOutput, cons.getOccupiedSquares(), test.getOccupiedSquares());
		assertEquals(errorOutput, cons.getPiecePhase(), test.getPiecePhase());
		assertEquals(errorOutput, cons.getDevelopmentStatus(), test.getDevelopmentStatus());
		assertEquals(errorOutput, cons.getHashing(), test.getHashing());
		assertEquals(errorOutput, cons.getEnPassantSq(), test.getEnPassantSq());
		Assert.assertArrayEquals(errorOutput, cons.getHashes(), test.getHashes());
		assertEquals(errorOutput, cons.getMidgamePositionalEval(), test.getMidgamePositionalEval());
		assertEquals(errorOutput, cons.getEndgamePositionalEval(), test.getEndgamePositionalEval());

		assertEquals(errorOutput, cons.getSquaresAttackedBy(Side.W), test.getSquaresAttackedBy(Side.W));
		assertEquals(errorOutput, cons.getSquaresAttackedBy(Side.B), test.getSquaresAttackedBy(Side.B));

		Set<String> cMoves = cons.getMoves().stream().map(x -> x.toString()).collect(Collectors.toSet());
		Set<String> tMoves = test.getMoves().stream().map(x -> x.toString()).collect(Collectors.toSet());
		// System.out.println(cMoves.size() + ", " + tMoves.size());
		// for (final String s : cMoves)
		// {
		// System.out.println(s);
		// }
		// System.out.println();
		// for (final String s : tMoves)
		// {
		// System.out.println(s);
		// }
		assertTrue(errorOutput, cMoves.containsAll(tMoves) && tMoves.containsAll(cMoves));

		cMoves = cons.getAttackMoves().stream().map(x -> x.toString()).collect(Collectors.toSet());
		tMoves = test.getAttackMoves().stream().map(x -> x.toString()).collect(Collectors.toSet());
		assertTrue(errorOutput, cMoves.containsAll(tMoves) && tMoves.containsAll(cMoves));

		assertEquals(errorOutput, cons.getTerminationState(), test.getTerminationState());
	}
}

/* ---------------------------------------------------------------------*
 * This software is the confidential and proprietary
 * information of Lhasa Limited
 * Granary Wharf House, 2 Canal Wharf, Leeds, LS11 5PS
 * ---
 * No part of this confidential information shall be disclosed
 * and it shall be used only in accordance with the terms of a
 * written license agreement entered into by holder of the information
 * with LHASA Ltd.
 * --------------------------------------------------------------------- */