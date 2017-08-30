/**
 *
 */
package jenjinn.engine.evaluation.componentimpl;

import static jenjinn.engine.evaluation.componentimpl.PawnStructureV1.getPawnAttacksFromLocs;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.Side;
import jenjinn.engine.evaluation.EvaluatingComponent;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.pieces.ChessPiece;

/**
 * @author t
 *
 */
public class MobilityV1 implements EvaluatingComponent
{
	/**
	 * The mobility scores define what value we give to the mobility of the
	 * various pieces.
	 */
	private final MobilityScores midScores = new MobilityScores(2, 3, 0, 0, 1);
	private final MobilityScores endScores = new MobilityScores(1, 1, 1, 1, 2);

	/**
	 * Working variables
	 */
	private BoardState state;

	private int midGamEval, endGameEval;

	private long wPawnAttacks, bPawnAttacks;

	private long allWhiteLoc, allBlackLoc;

	/* (non-Javadoc)
	 *
	 * @see jenjinn.engine.evaluation.EvaluatingComponent#evaluate(jenjinn.engine.boardstate.BoardState) */
	@Override
	public short evaluate(final BoardState state)
	{
		this.state = state;
		midGamEval = 0;
		endGameEval = 0;
		wPawnAttacks = EngineUtils.multipleOr(getPawnAttacksFromLocs(state.getPieceLocations(0), Side.W));
		bPawnAttacks = EngineUtils.multipleOr(getPawnAttacksFromLocs(state.getPieceLocations(6), Side.B));
		allWhiteLoc = state.getSideLocations(Side.W);
		allBlackLoc = state.getSideLocations(Side.B);

		evaluateBishopMobility();
		evaluateKnightMobility();
		evaluateRookMobility();
		evaluateQueenMobility();

		final short gamePhase = state.getGamePhase();
		return (short) (((midGamEval * (256 - gamePhase)) + (endGameEval * gamePhase)) / 256);
	}

	private void evaluateQueenMobility()
	{
		final byte[] whiteQueenLocs = EngineUtils.getSetBits(state.getPieceLocations(4));

		for (final byte loc : whiteQueenLocs)
		{
			final int moveNum = Long.bitCount(ChessPiece.get(4).getMoveset(loc, allWhiteLoc, allBlackLoc) & ~bPawnAttacks);
			midGamEval += moveNum * midScores.getQueen();
			endGameEval += moveNum * endScores.getQueen();
		}

		final byte[] blackQueenLocs = EngineUtils.getSetBits(state.getPieceLocations(8));

		for (final byte loc : blackQueenLocs)
		{
			final int moveNum = Long.bitCount(ChessPiece.get(10).getMoveset(loc, allBlackLoc, allWhiteLoc) & ~wPawnAttacks);
			midGamEval -= moveNum * midScores.getQueen();
			endGameEval -= moveNum * endScores.getQueen();
		}
	}

	private void evaluateRookMobility()
	{
		final byte[] whiteRookLocs = EngineUtils.getSetBits(state.getPieceLocations(3));

		for (final byte loc : whiteRookLocs)
		{
			final long file = BBDB.FILE[7 - (loc % 8)];
			final long allMoves = ChessPiece.get(3).getMoveset(loc, allWhiteLoc, allBlackLoc) & ~bPawnAttacks;

			final int vMovesNum = Long.bitCount(allMoves & file), hMovesNum = Long.bitCount(allMoves & ~file);
			midGamEval += vMovesNum * midScores.getRookV();
			midGamEval += hMovesNum * midScores.getRookH();

			endGameEval += vMovesNum * endScores.getRookV();
			endGameEval += hMovesNum * endScores.getRookH();
		}

		final byte[] blackRookLocs = EngineUtils.getSetBits(state.getPieceLocations(9));

		for (final byte loc : blackRookLocs)
		{
			final long file = BBDB.FILE[7 - (loc % 8)];
			final long allMoves = ChessPiece.get(9).getMoveset(loc, allBlackLoc, allWhiteLoc) & ~wPawnAttacks;

			final int vMovesNum = Long.bitCount(allMoves & file), hMovesNum = Long.bitCount(allMoves & ~file);
			midGamEval -= vMovesNum * midScores.getRookV();
			midGamEval -= hMovesNum * midScores.getRookH();

			endGameEval -= vMovesNum * endScores.getRookV();
			endGameEval -= hMovesNum * endScores.getRookH();
		}
	}

	private void evaluateKnightMobility()
	{
		final byte[] whiteKnightLocs = EngineUtils.getSetBits(state.getPieceLocations(2));

		for (final byte loc : whiteKnightLocs)
		{
			final int moveNum = Long.bitCount(ChessPiece.get(2).getMoveset(loc, allWhiteLoc, allBlackLoc) & ~bPawnAttacks);
			midGamEval += moveNum * midScores.getKnight();
			endGameEval += moveNum * endScores.getKnight();
		}

		final byte[] blackKnightLocs = EngineUtils.getSetBits(state.getPieceLocations(8));

		for (final byte loc : blackKnightLocs)
		{
			final int moveNum = Long.bitCount(ChessPiece.get(8).getMoveset(loc, allBlackLoc, allWhiteLoc) & ~wPawnAttacks);
			midGamEval -= moveNum * midScores.getKnight();
			endGameEval -= moveNum * endScores.getKnight();
		}
	}

	private void evaluateBishopMobility()
	{
		final byte[] whiteBishopLocs = EngineUtils.getSetBits(state.getPieceLocations(1));

		for (final byte loc : whiteBishopLocs)
		{
			final int moveNum = Long.bitCount(ChessPiece.get(1).getMoveset(loc, allWhiteLoc, allBlackLoc) & ~bPawnAttacks);
			midGamEval += moveNum * midScores.getBishop();
			endGameEval += moveNum * endScores.getBishop();
		}

		final byte[] blackBishopLocs = EngineUtils.getSetBits(state.getPieceLocations(1));

		for (final byte loc : blackBishopLocs)
		{
			final int moveNum = Long.bitCount(ChessPiece.get(7).getMoveset(loc, allBlackLoc, allWhiteLoc) & ~wPawnAttacks);
			midGamEval -= moveNum * midScores.getBishop();
			endGameEval -= moveNum * endScores.getBishop();
		}
	}
}
