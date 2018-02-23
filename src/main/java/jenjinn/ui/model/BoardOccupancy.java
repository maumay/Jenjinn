/**
 *
 */
package jenjinn.ui.model;

import jenjinn.engine.pieces.ChessPiece;

/**
 * A convenience wrapper class representing the location of a particular piece
 * on a chessboard. Do NOT use these to represent empty squares, i.e. with
 * occupyingPiece set as null otherwise there will be problems with comparison
 * for sorting. Could we not cache these?
 * 
 * @author TB
 * @date 29 Jan 2017
 */
public class BoardOccupancy
{
	public final ChessPiece occupyingPiece;
	public final byte occupiedSquare;

	public BoardOccupancy(final ChessPiece occupyingPiece, final byte occupiedSquare)
	{
		this.occupyingPiece = occupyingPiece;
		this.occupiedSquare = occupiedSquare;
	}

	// public long getAttackset(final long friendlyPieces, final long enemyPieces)
	// {
	// return occupyingPiece.getAttackset(occupiedSquare, friendlyPieces,
	// enemyPieces);
	// }

	// public long getAttackSet(final OccupiedSquares pieceLocs)
	// {
	// final Side friendlySide = occupyingPiece.getSide();
	// return getAttackset(pieceLocs.getPieceLocs(friendlySide),
	// pieceLocs.getPieceLocs(friendlySide.otherSide()));
	// }
	//
	// public StandardMove[] getMoveset(final long friendlyPieces, final long
	// enemyPieces)
	// {
	// return occupyingPiece.getMoveset(occupiedSquare, friendlyPieces,
	// enemyPieces);
	// }

	// public StandardMove[] getMoveset(final OccupiedSquares pieceLocs)
	// {
	// return getMoveset(pieceLocs.getFriendlyPieceLocs(occupyingPiece),
	// pieceLocs.getEnemyPieceLocs(occupyingPiece));
	// }

}
