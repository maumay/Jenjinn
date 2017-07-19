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
	WHITE_WIN(Infinity.INT_INFINITY - 1), BLACK_WIN(1 - Infinity.INT_INFINITY), DRAW(0), NOT_TERMINAL(0);

	public int value;

	private TerminationType(final int value)
	{
		this.value = value;
	}
}
