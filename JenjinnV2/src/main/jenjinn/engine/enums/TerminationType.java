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
	// Make just a little smaller than infinity so that we don't get null moves during the tree search.
	WHITE_WIN(Infinity.SHORT_INFINITY - 1), BLACK_WIN(1 - Infinity.SHORT_INFINITY), DRAW(0), NOT_TERMINAL(0);

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
