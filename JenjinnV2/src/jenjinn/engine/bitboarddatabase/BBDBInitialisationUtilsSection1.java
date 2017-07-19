/**
 * Written by Tom Ball 2017. 
 * 
 * This code is unlicensed but please don't plagiarize. 
 */

package jenjinn.engine.bitboarddatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jenjinn.engine.enums.Direction;
import jenjinn.engine.enums.Sq;
import jenjinn.engine.misc.EngineUtils;
import jenjinn.engine.misc.PieceMovementDirectionArrays;



/**
 * @author TB
 * @date 21 Jan 2017
 *
 *  First of three utility classes containing only
 *  static methods to initialise the constants in 
 *  the BBDB class. We generate the basic building
 *  blocks and then the move and attack sets of all
 *  piece types on an empty board.
 */
public class BBDBInitialisationUtilsSection1
{

     public static long[] generateSingleOccupancyBitboards()
     {
          final long[] ans = new long[64];
          for (int i = 0; i < 64; i++)
          {
               ans[i] = (1L << i);
          }
          return ans;
     }
     
     public static long[] generateRankBitboards()
     {
          final long[] ans = new long[8];
          final Direction west = Direction.W;
          for (byte i = 0; i < 8; i++)
          {
               final Sq start = Sq.getSq((byte) (8 * i));
               final Sq[] allConstituents = start.getAllSqInDirection(west, true);
               ans[i] = EngineUtils.multipleOr(allConstituents);
          }
          return ans;
     }
     
     public static long[] generateFileBitboards()
     {
          final long[] ans = new long[8];
          final Direction north = Direction.N;
          for (byte i = 0; i < 8; i++)
          {
               final Sq start = Sq.getSq((byte) (7 - i));
               final Sq[] allConstituents = start.getAllSqInDirection(north, true);
               ans[i] = EngineUtils.multipleOr(allConstituents);
          }
          return ans;
     }
     
     public static long[] generateDiagonalBitboards()
     {
          long[] ans = new long[15];
          final Direction nEast = Direction.NE;
          for (byte i = 0; i < 15; i++)
          {
               final Sq start = (i < 8) ? Sq.getSq(i) : Sq.getSq((byte) 0, (byte) (i - 7));
               final Sq[] allConstituents = start.getAllSqInDirection(nEast, true);
               ans[i] = EngineUtils.multipleOr(allConstituents);
          }
          return ans;
     }
     
     public static long[] generateAntidiagonalBitboards()
     {
          long[] ans = new long[15];
          final Direction nWest = Direction.NW;
          for (byte i = 0; i < 15; i++)
          {
               final Sq start = (i < 8) ? Sq.getSq((byte) (7 - i)) : Sq.getSq((byte) 7, (byte) (i - 7));
               final Sq[] allConstituents = start.getAllSqInDirection(nWest, true);
               ans[i] = EngineUtils.multipleOr(allConstituents);
          }
          return ans;
     }
     
     public static long[][] generateAllEmptyBoardPieceMovementBitboards()
     {
          long[][] ans = new long[7][];
          for (int i = 0; i < 7; i++)
          {
               ans[i] = generateMoves(i, false);
          }
          return ans;
     }
     
     public static long[][] generateAllEmptyBoardPieceAttackBitboards()
     {
          long[][] ans = new long[7][];
          for (int i = 0; i < 7; i++)
          {
               ans[i] = generateMoves(i, true);
          }
          return ans;
     }
     
     private static long[] generateMoves(final int i, final boolean isAttackset)
     {
          long[] ans = new long[64];
          
          if (i == 0)
          {
               ans = generateEmptyBoardPawnBitboards(true, isAttackset);
          }
          if (i == 1)
          {
               ans = generateEmptyBoardPawnBitboards(false, isAttackset);
          }
          if (i == 2)
          {
               ans = generateEmptyBoardMinorPieceBitboards(true);
          }
          if (i == 3)
          {
               ans = generateEmptyBoardMinorPieceBitboards(false);
          }
          if (i == 4)
          {
               ans = generateEmptyBoardMajorPieceBitboards(true);
          }
          if (i == 5)
          {
               ans = generateEmptyBoardMajorPieceBitboards(false);
          }
          if (i == 6)
          {
               ans = generateEmptyBoardKingBitboards();
          }
          return ans;
     }
     
     private static long[] generateEmptyBoardKingBitboards()
     {
          final long[] ans = new long[64];
          final Direction[] movementDirections = PieceMovementDirectionArrays.KD;
          for (Sq startSq : Sq.values())
          {
               final List<Sq> possMoveSqs = new ArrayList<>();
               for (Direction dir : movementDirections)
               {
                    Sq[] nextSqs = startSq.getAllSqInDirection(dir, false, (byte) 1);
                    possMoveSqs.addAll(Arrays.asList(nextSqs));
               }
               ans[startSq.ordinal()] = EngineUtils.multipleOr(possMoveSqs.toArray(new Sq[0]));
          }
          return ans;
     }
     
     private static long[] generateEmptyBoardMajorPieceBitboards(final boolean isRook)
     {
          final long[] ans = new long[64];
          final Direction[] movementDirections = isRook ? PieceMovementDirectionArrays.RD : PieceMovementDirectionArrays.QD;
          for (Sq startSq : Sq.values())
          {
               final List<Sq> possMoveSqs = new ArrayList<>();
               for (Direction dir : movementDirections)
               {
                    final Sq[] nextSqs = startSq.getAllSqInDirection(dir, false);
                    possMoveSqs.addAll(Arrays.asList(nextSqs));
               }
               ans[startSq.ordinal()] = EngineUtils.multipleOr(possMoveSqs.toArray(new Sq[0]));
          }
          return ans;
     }
     
     private static long[] generateEmptyBoardMinorPieceBitboards(final boolean isBishop)
     {
          final long[] ans = new long[64];
          final Direction[] movementDirections = isBishop ? PieceMovementDirectionArrays.BD : PieceMovementDirectionArrays.ND;
          for (Sq startSq : Sq.values())
          {
               final List<Sq> possMoveSqs = new ArrayList<>();
               for (Direction dir : movementDirections)
               {
                    if (isBishop)
                    {
                         final Sq[] nextSqs = startSq.getAllSqInDirection(dir, false);
                         possMoveSqs.addAll(Arrays.asList(nextSqs));
                    }
                    else
                    {
                         Sq[] nextSqs = startSq.getAllSqInDirection(dir, false, (byte) 1);
                         possMoveSqs.addAll(Arrays.asList(nextSqs));
                    }
               }
               ans[startSq.ordinal()] = EngineUtils.multipleOr(possMoveSqs.toArray(new Sq[0]));
          }
          return ans;
     }
     
     private static long[] generateEmptyBoardPawnBitboards(final boolean isWhite, final boolean isAttackset)
     {
          final long[] ans = new long[64];
          final long startRank = isWhite ? BBDB.RNK[1] : BBDB.RNK[6];
          
          Direction[] movementDirections = null;
          if (isWhite)
          {
               movementDirections = isAttackset ? PieceMovementDirectionArrays.WPA : PieceMovementDirectionArrays.WPM;
          }
          else
          {
               movementDirections = isAttackset ? PieceMovementDirectionArrays.BPA : PieceMovementDirectionArrays.BPM;
          }
          
          for (Sq startSq : Sq.values())
          {
               final List<Sq> possMoveSqs = new ArrayList<>();
               for (Direction dir : movementDirections)
               {
                    // This is the case of the pawns first move
                    if (!isAttackset && (startSq.getAsBB() & startRank) != 0)
                    {
                         Sq[] nextSqs = startSq.getAllSqInDirection(dir, false, (byte) 2);
                         possMoveSqs.addAll(Arrays.asList(nextSqs));
                    }
                    else
                    {
                         Sq[] nextSqs = startSq.getAllSqInDirection(dir, false, (byte) 1);
                         possMoveSqs.addAll(Arrays.asList(nextSqs));
                    }
               }
               ans[startSq.ordinal()] = EngineUtils.multipleOr(possMoveSqs.toArray(new Sq[0]));
          }
          return ans;
     }
     
}
