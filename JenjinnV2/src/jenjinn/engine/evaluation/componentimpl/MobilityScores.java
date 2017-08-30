/**
 *
 */
package jenjinn.engine.evaluation.componentimpl;

/**
 * @author t
 *
 */
public class MobilityScores
{
	private final short bishop, knight, queen, rookH, rookV;

	public MobilityScores(final int bishop, final int knight, final int queen, final int rookH, final int rookV)
	{
		this.bishop = (short) bishop;
		this.knight = (short) knight;
		this.queen = (short) queen;
		this.rookH = (short) rookH;
		this.rookV = (short) rookV;
	}

	public short getBishop()
	{
		return bishop;
	}

	public short getKnight()
	{
		return knight;
	}

	public short getQueen()
	{
		return queen;
	}

	public short getRookH()
	{
		return rookH;
	}

	public short getRookV()
	{
		return rookV;
	}
}
