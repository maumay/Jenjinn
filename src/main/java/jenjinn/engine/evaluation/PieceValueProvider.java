package jenjinn.engine.evaluation;

import jenjinn.engine.evaluation.piecetablesimpl.EndGamePSTimplV1;
import jenjinn.engine.evaluation.piecetablesimpl.MiddleGamePSTimplV1;

public final class PieceValueProvider
{
	public static final short[] MGAME_VALUES = MiddleGamePSTimplV1.PIECE_VALUES;
	public static final short[] EGAME_VALUES = EndGamePSTimplV1.PIECE_VALUES;

	private PieceValueProvider()
	{
	}
}
