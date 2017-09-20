/**
 *
 */
package jenjinn.testingengine.enums;

import jenjinn.engine.boardstate.CastlingRights;
import jenjinn.engine.enums.Side;

/**
 * @author t
 *
 */
public enum CastleArea
{
	W_K(CastlingRights.W_KINGSIDE, 0), W_Q(CastlingRights.W_QUEENSIDE, 1), B_K(CastlingRights.B_KINGSIDE, 2), B_Q(CastlingRights.B_QUEENSIDE, 3);

	public final byte byteRep;
	public final int hashingIndex;

	private CastleArea(final int byteRep, final int hashingIndex)
	{
		this.byteRep = (byte) byteRep;
		this.hashingIndex = hashingIndex;
	}

	public static CastleArea getKingside(final Side s)
	{
		return s.isWhite() ? W_K : B_K;
	}

	public static CastleArea getQueenside(final Side s)
	{
		return s.isWhite() ? W_Q : B_Q;
	}
}
