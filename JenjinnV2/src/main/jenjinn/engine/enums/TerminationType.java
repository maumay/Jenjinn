/**
 *
 */
package jenjinn.engine.enums;

/**
 * @author TB
 * @date 1 Feb 2017
 *
 */
public enum TerminationType
{
	// Make just a little bigger than the initial alpha beta calls so we don't change the bounds for terminal states..
	WHITE_WIN(Infinity.SHORT_INFINITY), BLACK_WIN(Infinity.SHORT_INFINITY), DRAW(0), NOT_TERMINAL(0);

	public short value;

	private TerminationType(final int value)
	{
		this.value = (short) value;
	}

	public boolean isTerminal()
	{
		return this != NOT_TERMINAL;
	}
}
