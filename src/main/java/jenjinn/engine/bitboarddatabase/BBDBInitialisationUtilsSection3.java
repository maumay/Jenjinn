package jenjinn.engine.bitboarddatabase;

import java.util.ArrayList;
import java.util.List;

import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.misc.PieceMovementDirectionArrays;

/**
 * @author TB
 * @date 24 Jan 2017
 */
public class BBDBInitialisationUtilsSection3
{
	public static long[][] generateRookMagicMoveDatabase()
	{
		return generateMagicMoveDatabase(true);
	}

	public static long[][] generateBishopMagicMoveDatabase()
	{
		return generateMagicMoveDatabase(false);
	}

	private static long[][] generateMagicMoveDatabase(final boolean isRook)
	{
		final long[][] mmDatabase = new long[64][];
		final long[][] allSquaresOccupancyVariations = isRook ? BBDB.ROV : BBDB.BOV;

		for (byte i = 0; i < 64; i++) {
			final long[] singleSquaresOccupancyVariations = allSquaresOccupancyVariations[i];
			final long magicNumber = isRook ? BBDB.RMN[i] : BBDB.BMN[i];
			final byte bitShift = isRook ? BBDB.RMB[i] : BBDB.BMB[i];
			final long[] singleSquareMmDatabase = new long[singleSquaresOccupancyVariations.length];

			for (final long occVar : singleSquaresOccupancyVariations) {
				final int magicIndex = (int) ((occVar * magicNumber) >>> bitShift);
				singleSquareMmDatabase[magicIndex] = findAttackSetFromOccupancyVariation(Sq.get(i), occVar, isRook);
			}
			mmDatabase[i] = singleSquareMmDatabase;
		}

		return mmDatabase;
	}

	private static long findAttackSetFromOccupancyVariation(final Sq startSq, final long occVar, final boolean isRook)
	{
		final List<Sq> attackSquares = new ArrayList<>();
		final Direction[] movementDirections = isRook ? PieceMovementDirectionArrays.RD : PieceMovementDirectionArrays.BD;

		for (final Direction dir : movementDirections) {
			Sq nextSq = startSq;

			while (nextSq != null) {
				nextSq = nextSq.getNextSqInDirection(dir);
				final long nextSqAsBB = nextSq == null ? 0L : nextSq.getAsBB();
				final boolean blocked = (nextSqAsBB & occVar) != 0;

				if (nextSq != null) {
					attackSquares.add(nextSq);
				}
				if (blocked) {
					break;
				}
			}
		}
		return EngineUtils.multipleOr(attackSquares.toArray(new Sq[0]));
	}
}
