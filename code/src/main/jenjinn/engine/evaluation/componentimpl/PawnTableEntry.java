/**
 * 
 */
package jenjinn.engine.evaluation.componentimpl;

/**
 * @author t
 *
 */
public class PawnTableEntry 
{
	public final long posHash;
	public final short eval;
	
	public PawnTableEntry(long posHash, short eval)
	{
		this.posHash = posHash;
		this.eval = eval;
	}
}
