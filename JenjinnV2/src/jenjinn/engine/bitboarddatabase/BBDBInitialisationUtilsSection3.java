/**
 * Written by Tom Ball 2017. 
 * 
 * This code is unlicensed but please don't plagiarize. 
 */

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
 *
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
          
          for (byte i = 0; i < 64; i++)
          {
               final long[] singleSquaresOccupancyVariations = allSquaresOccupancyVariations[i];
               final long magicNumber = isRook ? BBDB.RMN[i] : BBDB.BMN[i];
               final byte bitShift = isRook ? BBDB.RMB[i] : BBDB.BMB[i];
               final long[] singleSquareMmDatabase = new long[singleSquaresOccupancyVariations.length];
               
               for (long occVar : singleSquaresOccupancyVariations)
               {
                    final int magicIndex = (int) ((occVar * magicNumber) >>> bitShift);
                    singleSquareMmDatabase[magicIndex] = findAttackSetFromOccupancyVariation(Sq.getSq(i), occVar, isRook);
               }
               mmDatabase[i] = singleSquareMmDatabase;
          }
          
          return mmDatabase;
     }

     private static long findAttackSetFromOccupancyVariation(final Sq startSq, final long occVar, final boolean isRook)
     {
          final List<Sq> attackSquares = new ArrayList<>();
          final Direction[] movementDirections = isRook ? PieceMovementDirectionArrays.RD : PieceMovementDirectionArrays.BD;
          
          for (Direction dir : movementDirections)
          {
               Sq nextSq = startSq;
               
               while (nextSq != null)
               {
                    nextSq = nextSq.getNextSqInDirection(dir);
                    long nextSqAsBB = nextSq == null ? 0L : nextSq.getAsBB();
                    boolean blocked = (nextSqAsBB & occVar) != 0;
                    
                    if (nextSq != null)
                    {
                         attackSquares.add(nextSq);
                    }
                    if (blocked)
                    {
                         break;
                    }
               }
          }
          return EngineUtils.multipleOr(attackSquares.toArray(new Sq[0]));
     }
     
     public static long[][] generateWhitePawnMagicMoveDatabase()
     {
          return generatePawnMagicMoveDatabase(true);
     }
     
     public static long[][] generateBlackPawnMagicMoveDatabase()
     {
          return generatePawnMagicMoveDatabase(false);
     }
     
     private static long[][] generatePawnMagicMoveDatabase(final boolean isWhite)
     {
          final long[][] mmDatabase = new long[8][];
          final long[][] allSquaresOccupancyVariations = isWhite ? BBDB.WPFMOV : BBDB.BPFMOV;
          final byte shiftFactor = (byte) (isWhite ? 8 : 48);
          
          for (byte i = 0; i < 8; i++)
          {
               final Sq startSq = Sq.getSq((byte) (i + shiftFactor));
               
               final long[] singleSquaresOccupancyVariations = allSquaresOccupancyVariations[i];
               final long magicNumber = isWhite ? BBDB.WPFMMN[i] : BBDB.BPFMMN[i];
               final byte bitShift = BBDB.PFMB;
               final long[] singleSquareMmDatabase = new long[singleSquaresOccupancyVariations.length];
               
               for (long occVar : singleSquaresOccupancyVariations)
               {
                    final int magicIndex = (int) ((occVar * magicNumber) >>> bitShift);
                    singleSquareMmDatabase[magicIndex] = findMovesetFromPawnFirstMoveOccupancyVariation(startSq, occVar, isWhite);
               }
               mmDatabase[i] = singleSquareMmDatabase;
          }
          
          return mmDatabase;
     }
     
     private static long findMovesetFromPawnFirstMoveOccupancyVariation(final Sq startSq, final long occVar, final boolean isWhite)
     {
          final List<Sq> allowedMoveSquares = new ArrayList<>();
          final Direction movementDirection = isWhite ? Direction.N : Direction.S;
          
          final Sq[] potentialMovesSquares = startSq.getAllSqInDirection(movementDirection, false, (byte) 2);
          
          if ((potentialMovesSquares[0].getAsBB() & occVar) == 0)
          {
               allowedMoveSquares.add(potentialMovesSquares[0]);
               
               if ((potentialMovesSquares[1].getAsBB() & occVar) == 0)
               {
                    allowedMoveSquares.add(potentialMovesSquares[1]);
               }
          }
          
          return EngineUtils.multipleOr(allowedMoveSquares.toArray(new Sq[0]));
     }
}
