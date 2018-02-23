/**
 *
 */
package jenjinn.engine.openingdatabase;

import java.util.HashMap;

import jenjinn.engine.enums.Sq;
import jenjinn.engine.pieces.PieceType;

/**
 * Represents a single move command in algebraic notation
 * 
 * @author ThomasB
 */
public class AlgebraicCommand
{
	private static final HashMap<String, Integer> FILEMAP = new HashMap<>();
	static {
		FILEMAP.put("a", 0);
		FILEMAP.put("b", 1);
		FILEMAP.put("c", 2);
		FILEMAP.put("d", 3);
		FILEMAP.put("e", 4);
		FILEMAP.put("f", 5);
		FILEMAP.put("g", 6);
		FILEMAP.put("h", 7);
	}

	private static final String KINGSIDE_CASTLE_ID = "O-O";
	private static final String QUEENSIDE_CASTLE_ID = "O-O-O";

	private String asString;
	private String castleOrder;
	private PieceType pieceToMove;
	private Sq targetSq;
	private int startRow = -1; // Will usually be left as -1
	private int startFile = -1; // Will usually be left as -1
	private boolean isAttackOrder;
	private boolean isPromotionOrder;
	private PieceType toPromoteTo; // usually null

	public AlgebraicCommand(final String asStr)
	{
		// Trim the string (we don't need to know about checks etc).
		asString = asStr.trim().replace("+", "");
		if (asString.contains(".")) {
			asString = asString.substring(asString.indexOf(".") + 1, asString.length());
		}
		isAttackOrder = asString.contains("x") ? true : false;
		isPromotionOrder = asString.contains("=") ? true : false;

		if (asString.equals(KINGSIDE_CASTLE_ID)) {
			castleOrder = "_KINGSIDE";
		}
		else if (asString.equals(QUEENSIDE_CASTLE_ID)) {
			castleOrder = "_QUEENSIDE";
		}
		else if (isPromotionOrder) {
			assert asString.length() == 4 || isAttackOrder;

			int len = asString.length();
			toPromoteTo = PieceType.valueOf(asString.substring(len - 1, len));
			targetSq = Sq.valueOf(asString.substring(len - 4, len - 2));

		}
		else {
			final int length = asString.length();
			if (length == 2) {
				pieceToMove = PieceType.P;
				targetSq = Sq.valueOf(asString);
			}
			else if (length == 3) {
				final Object[] preparedValues = processLengthThreeCommand(asString);
				pieceToMove = (PieceType) preparedValues[0];
				targetSq = (Sq) preparedValues[1];
			}
			else if (length == 4) {
				final Object[] preparedValues = processLengthFourCommand(asString);
				pieceToMove = (PieceType) preparedValues[0];
				targetSq = (Sq) preparedValues[1];
				startRow = (Integer) preparedValues[2];
				startFile = (Integer) preparedValues[3];
				toPromoteTo = (PieceType) preparedValues[4];
			}
			else if (length == 5) {
				final Object[] preparedValues = processLengthFiveCommand(asString);
				pieceToMove = (PieceType) preparedValues[0];
				targetSq = (Sq) preparedValues[1];
				startRow = (Integer) preparedValues[2];
				startFile = (Integer) preparedValues[3];
			}
		}
	}

	private Object[] processLengthFiveCommand(final String trimmed)
	{
		if (isPromotionOrder) {
			System.out.println(trimmed);
		}
		final Object[] ans = new Object[] { PieceType.valueOf(trimmed.substring(0, 1)), Sq.valueOf(
				trimmed.substring(3)), new Integer(-1), new Integer(-1) };
		final Integer fileNumber = FILEMAP.get(trimmed.substring(1, 2));
		if (fileNumber != null) {
			ans[3] = fileNumber;
		}
		else {
			ans[2] = Character.getNumericValue(trimmed.charAt(1)) - 1;
		}
		return ans;
	}

	private Object[] processLengthFourCommand(final String trimmed)
	{
		final boolean isCaptureCommand = trimmed.contains("x");
		final boolean isPromotionCommand = trimmed.contains("=");
		final Object[] ans = new Object[] { null, null, new Integer(-1), new Integer(-1), null };
		if (isCaptureCommand) {
			final char firstLetter = trimmed.charAt(0);
			if (Character.isUpperCase(firstLetter)) {
				ans[0] = PieceType.valueOf(trimmed.substring(0, 1));
				ans[1] = Sq.valueOf(trimmed.substring(2));
			}
			else {
				ans[0] = PieceType.P;
				ans[1] = Sq.valueOf(trimmed.substring(2));
				ans[3] = FILEMAP.get(trimmed.substring(0, 1));
			}
		}
		else if (isPromotionCommand) {
			ans[0] = PieceType.P;
			ans[1] = Sq.valueOf(trimmed.substring(0, 2));
			ans[4] = PieceType.valueOf(trimmed.substring(3));
		}
		else {
			ans[0] = PieceType.valueOf(trimmed.substring(0, 1));
			ans[1] = Sq.valueOf(trimmed.substring(2));
			final Integer fileNumber = FILEMAP.get(trimmed.substring(1, 2));
			if (fileNumber != null) {
				ans[3] = fileNumber;
			}
			else {
				ans[2] = Character.getNumericValue(trimmed.charAt(1)) - 1;
			}
		}
		return ans;
	}

	private Object[] processLengthThreeCommand(final String trimmed)
	{
		return new Object[] { PieceType.valueOf(trimmed.substring(0, 1)), Sq.valueOf(trimmed.substring(1)) };
	}

	public PieceType getPieceToMove()
	{
		return pieceToMove;
	}

	public Sq getTargetSq()
	{
		return targetSq;
	}

	public int getStartRow()
	{
		return startRow;
	}

	public int getStartFile()
	{
		return startFile;
	}

	public String getCastleOrder()
	{
		return castleOrder;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder("In algebraic form: " + asString + "\n");
		if (castleOrder != null) {
			return (sb.deleteCharAt(sb.length() - 1)).toString();
		}
		sb.append("PieceType to move: " + pieceToMove.toString() + "\n");
		sb.append("Target square is: " + targetSq.toString() + "\n");
		if (startRow > -1) {
			sb.append("Start rank is: " + (startRow + 1) + "\n");
		}
		if (startFile > -1) {
			sb.append("Start file is: " + (startFile + 1) + "\n");
		}
		return sb.toString();
	}

	public boolean isAttackOrder()
	{
		return isAttackOrder;
	}

	public boolean isPromotionOrder()
	{
		return isPromotionOrder;
	}

	public PieceType getToPromoteTo()
	{
		return toPromoteTo;
	}

	public String getAsString()
	{
		return asString;
	}
}
