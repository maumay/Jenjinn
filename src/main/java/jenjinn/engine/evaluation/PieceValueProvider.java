package jenjinn.engine.evaluation;

import jenjinn.engine.evaluation.piecetablesimpl.EndGamePieceSquareTable;
import jenjinn.engine.evaluation.piecetablesimpl.MiddleGamePieceSquareTable;

public final class PieceValueProvider
{
	public static final short[] MGAME_VALUES = MiddleGamePieceSquareTable.PIECE_VALUES;
	public static final short[] EGAME_VALUES = EndGamePieceSquareTable.PIECE_VALUES;

	private PieceValueProvider()
	{
	}
}
