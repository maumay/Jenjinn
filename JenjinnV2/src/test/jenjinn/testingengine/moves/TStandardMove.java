/**
 * 
 */
package jenjinn.testingengine.moves;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.enums.MoveType;
import jenjinn.engine.enums.Sq;

/**
 * @author t
 *
 */
public class TStandardMove extends TAbstractChessMove {

	/**
	 * @param type
	 * @param start
	 * @param target
	 */
	public TStandardMove(int start, int target) 
	{
		super(MoveType.STANDARD, start, target);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see jenjinn.engine.moves.ChessMove#evolve(jenjinn.engine.boardstate.BoardState)
	 */
	@Override
	public BoardState evolve(BoardState state) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString()
	{
		return "S" + "[" + Sq.getSq(getStart()).name() + ", " + Sq.getSq(getTarget()).name() + "]";
	}

}
