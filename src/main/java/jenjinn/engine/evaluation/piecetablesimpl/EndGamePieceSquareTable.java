package jenjinn.engine.evaluation.piecetablesimpl;

/**
 * @author ThomasB
 * @since 28 Jul 2017
 */
public class EndGamePieceSquareTable extends AbstractPieceSquareTable
{
	/* Don't modify elsewhere */
	public static final short[] PIECE_VALUES = { 120*2, 335*2, 305*2, 510*2, 910*2, 20000 };

	@Override
	protected short[] getPawnTable()
	{
		return new short[64];
	}

	@Override
	protected short[] getBishopTable()
	{
		return new short[] {
				-20, -10, -10, -10, -10, -10, -10, -20,
				-10, 0, 0, 0, 0, 0, 0, -10,
				-10, 0, 5, 10, 10, 5, 0, -10,
				-10, 5, 5, 10, 10, 5, 5, -10,
				-10, 0, 10, 10, 10, 10, 0, -10,
				-10, 10, 10, 10, 10, 10, 10, -10,
				-10, 5, 0, 0, 0, 0, 5, -10,
				-20, -10, -10, -10, -10, -10, -10, -20 };
	}

	@Override
	protected short[] getKnightTable()
	{
		return new short[] {
				-50, -40, -30, -30, -30, -30, -40, -50,
				-40, -20, 0, 0, 0, 0, -20, -40,
				-30, 0, 10, 15, 15, 10, 0, -30,
				-30, 5, 15, 20, 20, 15, 5, -30,
				-30, 0, 15, 20, 20, 15, 0, -30,
				-30, 5, 10, 15, 15, 10, 5, -30,
				-40, -20, 0, 5, 5, 0, -20, -40,
				-50, -40, -30, -30, -30, -30, -40, -50,
		};
	}

	@Override
	protected short[] getRookTable()
	{
		return new short[] {
				0, 0, 0, 0, 0, 0, 0, 0,
				5, 10, 10, 10, 10, 10, 10, 5,
				-5, 0, 0, 0, 0, 0, 0, -5,
				-5, 0, 0, 0, 0, 0, 0, -5,
				-5, 0, 0, 0, 0, 0, 0, -5,
				-5, 0, 0, 0, 0, 0, 0, -5,
				-5, 0, 0, 0, 0, 0, 0, -5,
				0, 0, 0, 5, 5, 0, 0, 0
		};
	}

	@Override
	protected short[] getQueenTable()
	{
		return new short[] {
				-20, -10, -10, -5, -5, -10, -10, -20,
				-10, 0, 0, 0, 0, 0, 0, -10,
				-10, 0, 5, 5, 5, 5, 0, -10,
				-5, 0, 5, 5, 5, 5, 0, -5,
				0, 0, 5, 5, 5, 5, 0, -5,
				-10, 5, 5, 5, 5, 5, 0, -10,
				-10, 0, 5, 0, 0, 0, 0, -10,
				-20, -10, -10, -5, -5, -10, -10, -20
		};
	}

	@Override
	protected short[] getKingTable()
	{
		return new short[] {
				-50, -40, -30, -20, -20, -30, -40, -50,
				-30, -20, -10, 0, 0, -10, -20, -30,
				-30, -10, 20, 30, 30, 20, -10, -30,
				-30, -10, 30, 40, 40, 30, -10, -30,
				-30, -10, 30, 40, 40, 30, -10, -30,
				-30, -10, 20, 30, 30, 20, -10, -30,
				-30, -30, 0, 0, 0, 0, -30, -30,
				-50, -30, -30, -30, -30, -30, -30, -50
		};
	}

	@Override
	protected short getPawnValue()
	{
		return PIECE_VALUES[0];
	}

	@Override
	protected short getBishopValue()
	{
		return PIECE_VALUES[1];
	}

	@Override
	protected short getKnightValue()
	{
		return PIECE_VALUES[2];
	}

	@Override
	protected short getRookValue()
	{
		return PIECE_VALUES[3];
	}

	@Override
	protected short getQueenValue()
	{
		return PIECE_VALUES[4];
	}

	@Override
	protected short getKingValue()
	{
		return PIECE_VALUES[5];
	}
}
