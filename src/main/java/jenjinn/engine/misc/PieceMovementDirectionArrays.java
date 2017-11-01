/**
 * Written by Tom Ball 2017. 
 * 
 * This code is unlicensed but please don't plagiarize. 
 */

package jenjinn.engine.misc;

import jenjinn.engine.enums.Direction;

/**
 * @author TB
 * @date 22 Jan 2017
 *
 *  Convenience class containing the directions each piece
 *  type can move / attack.
 */
public class PieceMovementDirectionArrays
{
     /** White pawn movement directions */
     public static final Direction[] WPM = { Direction.N };
     
     /** Black pawn movement directions */
     public static final Direction[] BPM = { Direction.S };
     
     /** White Pawn attack directions */
     public static final Direction[] WPA = { Direction.NE, Direction.NW };
     
     /** Black Pawn attack directions */
     public static final Direction[] BPA = { Direction.SE, Direction.SW };
     
     /** Bishop directions */
     public static final Direction[] BD = { Direction.NE, Direction.NW, Direction.SE, Direction.SW };
     
     /** Rook directions */
     public static final Direction[] RD = { Direction.N, Direction.W, Direction.S, Direction.E };
     
     /** Knight directions */
     public static final Direction[] ND = { Direction.NNE, Direction.NNW, Direction.NWW, Direction.NEE, Direction.SEE, Direction.SSE, Direction.SSW, Direction.SWW}; 
     
     /** Queen directions */
     public static final Direction[] QD = { Direction.NE, Direction.NW, Direction.N, Direction.E, Direction.SE, Direction.S, Direction.SW, Direction.W}; 
     
     /** King directions */ 
     public static final Direction[] KD = QD;
}
