package jenjinn.engine.moves;

import jenjinn.engine.enums.MoveType;
import jenjinn.engine.enums.Sq;

/**
 * @author ThomasB
 * @since 20 Jul 2017
 */
public abstract class AbstractChessMove implements ChessMove
{
	private final byte start, target;

	private final MoveType type;

	AbstractChessMove(final MoveType type, final int start, final int target)
	{
		this.type = type;
		this.start = (byte) start;
		this.target = (byte) target;
	}

	@Override
	public boolean matches(final Sq start, final Sq target)
	{
		return start.ordinal() == this.start && target.ordinal() == this.target;
	}

	@Override
	public boolean matchesStart(final Sq start)
	{
		return start.ordinal() == this.start;
	}

	@Override
	public byte getStart()
	{
		return start;
	}

	public long getStartBB()
	{
		return (1L << start);
	}

	@Override
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