package jenjinn.engine.openingdatabase;

import jenjinn.engine.moves.ChessMove;

/**
 * An opening order wraps a lightweight version of a BoardState and a
 * corresponding move to take from that position. Obviously we need a way to
 * check equality of these lw bordstates with actual ones.
 *
 * @author ThomasB
 *
 */
public class OpeningOrder
{
	private long boardStateHash;
	private ChessMove move;

	public OpeningOrder(final long boardStateHash, final ChessMove move)
	{
		this.boardStateHash = boardStateHash;
		this.move = move;
	}

	public ChessMove getMove()
	{
		return move;
	}

	public long getBoardHash()
	{
		return boardStateHash;
	}

	public String toDatabaseString()
	{
		return Long.toHexString(boardStateHash) + " " + move.toCompactString();
	}
}
