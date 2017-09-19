/**
 * Written by Tom Ball 2017. 
 * 
 * This code is unlicensed but please don't plagiarize. 
 */

package jenjinn.engine.enums;

/**
 * @author TB
 * @date 1 Dec 2016
 *
 *  Enum representing all the different directions
 *  the various chesspieces can move on the board.
 */
public enum Direction
{
     N((byte) 0, (byte) 1), 
     E((byte) 1, (byte) 0), 
     S((byte) 0, (byte) -1), 
     W((byte) -1, (byte) 0),
     NE((byte) 1, (byte) 1), 
     SE((byte) 1, (byte) -1), 
     SW((byte) -1, (byte) -1), 
     NW((byte) -1, (byte) 1),
     NNE((byte) 1, (byte) 2),
     NEE((byte) 2, (byte) 1), 
     SEE((byte) 2, (byte) -1), 
     SSE((byte) 1, (byte) -2),
     SSW((byte) -1, (byte) -2),
     SWW((byte) -2, (byte) -1), 
     NWW((byte) -2, (byte) 1), 
     NNW((byte) -1, (byte) 2);
     
     public byte dx;
     public byte dy;
     
     private Direction(final byte i, final byte j)
     {
          dx = i;
          dy = j;
     }
}
