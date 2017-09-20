/**
 *
 */
package jenjinn.testingengine.moves;

import jenjinn.engine.enums.MoveType;
import jenjinn.engine.moves.ChessMove;

/**
 * @author t
 *
 */
public abstract class TAbstractChessMove implements ChessMove
{
	private final byte start, target;

	private final MoveType type;

	TAbstractChessMove(final MoveType type, final int start, final int target)
	{
		this.type = type;
		this.start = (byte) start;
		this.target = (byte) target;
	}

	public byte getStart()
	{
		return start;
	}

	public long getStartBB()
	{
		return (1L << start);
	}

	public byte getTarget()
	{
		return target;
	}

	@Override
	public long getTargetBB()
	{
		return (1L << target);
	}

	public MoveType getType()
	{
		return type;
	}
}
