package jenjinn.engine.evaluation.piecetablesimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import jenjinn.engine.enums.Side;
import jenjinn.engine.evaluation.PieceSquareTable;

/**
 * @author ThomasB
 * @since 28 Jul 2017
 */
public abstract class AbstractPieceSquareTable implements PieceSquareTable
{
	protected final List<short[]> tables = new ArrayList<>();

	/**
	 *
	 */
	public AbstractPieceSquareTable()
	{
		for (final Side s : Side.values()) {
			tables.add(getTableFromReverseWhitePositionTable(getPawnTable(), s, getPawnValue()));
			tables.add(getTableFromReverseWhitePositionTable(getBishopTable(), s, getBishopValue()));
			tables.add(getTableFromReverseWhitePositionTable(getKnightTable(), s, getKnightValue()));
			tables.add(getTableFromReverseWhitePositionTable(getRookTable(), s, getRookValue()));
			tables.add(getTableFromReverseWhitePositionTable(getQueenTable(), s, getQueenValue()));
			tables.add(getTableFromReverseWhitePositionTable(getKingTable(), s, getKingValue()));
		}
	}

	@Override
	public short getPieceSquareValue(final byte pieceIndex, final byte squareIndex)
	{
		return tables.get(pieceIndex)[squareIndex];
	}

	protected abstract short[] getPawnTable();

	protected abstract short[] getBishopTable();

	protected abstract short[] getKnightTable();

	protected abstract short[] getRookTable();

	protected abstract short[] getQueenTable();

	protected abstract short[] getKingTable();

	protected abstract short getPawnValue();

	protected abstract short getBishopValue();

	protected abstract short getKnightValue();

	protected abstract short getRookValue();

	protected abstract short getQueenValue();

	protected abstract short getKingValue();

	private short[] getTableFromReverseWhitePositionTable(final short[] wPositionTable, final Side s, final short wPieceValue)
	{
		assert wPositionTable.length == 64;
		final short[] wTable = vectorisedAddition(reverseIndices(wPositionTable), wPieceValue);
		return s.isWhite() ? wTable : mirrorTable(wTable);
	}

	private short[] reverseIndices(final short[] arr)
	{
		final short[] newTable = new short[arr.length];
		IntStream.range(0, arr.length).forEach(i -> newTable[i] = arr[arr.length - (i + 1)]);
		return newTable;
	}

	private short[] mirrorTable(final short[] arr)
	{
		final short[] newTable = new short[arr.length];
		IntStream.range(0, arr.length).forEach(i -> newTable[getMirrorIndex(i)] = (short) -arr[i]);
		return newTable;
	}

	private short[] vectorisedAddition(final short[] arr, final short val)
	{
		final short[] newArr = new short[arr.length];
		IntStream.range(0, arr.length).forEach(i -> newArr[i] = (short) (arr[i] + val));
		return newArr;
	}

	private int getMirrorIndex(final int idx)
	{
		assert -1 < idx && idx < 64;
		final int newRank = 7 - (idx / 8);
		return (newRank * 8) + (idx % 8);
	}

}