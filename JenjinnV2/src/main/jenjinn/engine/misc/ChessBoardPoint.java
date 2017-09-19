/**
 * Written by Tom Ball 2017. 
 * 
 * This code is unlicensed but please don't plagiarize. 
 */

package jenjinn.engine.misc;

import jenjinn.engine.bitboarddatabase.BBDB;
import jenjinn.engine.enums.Sq;

/**
 * @author TB
 * @date 22 Jan 2017
 *
 *  A lightweight representation of a cartesian point
 *  representing a square on a chess board. NOTE that
 *  I will be using the maths convention in that the 
 *  coordinates (0,0) correspond to the square a1.
 */
public class ChessBoardPoint
{
     public final byte x;
     public final byte y;
     
     public ChessBoardPoint(final byte x, final byte y)
     {
          if (Sq.coordinatesAreValid(x, y))
          {
               this.x = x;
               this.y = y;
          }
          else
          {
               this.x = -1;
               this.y = -1;
          }
     }
     
     public Sq toSq()
     {
          return Sq.getSq(x, y);
     }
     
     public long toBitboard()
     {
          long ans = 0;
          if (x > -1)
          {
               ans = BBDB.FILE[x] & BBDB.RNK[y];
          }
          return ans;
     }
}
