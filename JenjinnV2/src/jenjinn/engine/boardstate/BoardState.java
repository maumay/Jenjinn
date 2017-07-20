/**
 *
 */
package jenjinn.engine.boardstate;

import java.util.List;

import jenjinn.engine.enums.Side;
import jenjinn.engine.enums.TerminationType;
import jenjinn.engine.moves.ChessMove;
import jenjinn.engine.pieces.ChessPiece;
import jenjinn.engine.zobristhashing.ZobristHasher;

/**
 * Completely immutable representation of a state of a chess board. IMPORTANT
 * CONVENTION: We denote the side to move by friendly for brevity.
 * The side which isn't to move is then denoted as enemy.
 *
 * // We need to make this class more memory efficient.
 *
 * @author TB
 * @date 28 Jan 2017
 */
public interface BoardState
{
	public static ZobristHasher HASHER = ZobristHasher.getDefault();

	byte getFriendlySideValue();

	Side getFriendlySide();

	Side getEnemySide();

	TerminationType getTerminationState();

	List<ChessMove> getMoves();

	// ChessMove generateMove(final AlgebraicCommand com);

	/**
	 * In general this hashing function is not what would be used during the tree search.
	 *
	 * @return
	 */
	long zobristHash();

	ChessPiece getPieceAt(final byte loc);

	ChessPiece getPieceAt(final byte loc, Side s);

	long getPieceLocations(int pieceIndex);

	long[] getPieceLocationsCopy();

	long getSideLocations(Side s);

	long getOccupiedSquares();

	long getAttackedSquares(final Side side);

	byte getCastleStatus();

	byte getCastleRights();

	long getDevelopmentStatus();

	long getHashing();

	byte getEnPassantSq();

	long[] getNewRecentHashings(long newHash);

}
