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

	public MobilityScores(short bishop, short knight, short queen, short rookH, short rookV) 
	{
		this.bishop = bishop;
		this.knight = knight;
		this.queen = queen;
		this.rookH = rookH;
		this.rookV = rookV;
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
