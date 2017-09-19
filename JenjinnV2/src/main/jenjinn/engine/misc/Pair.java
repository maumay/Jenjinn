/**
 * Written by Tom Ball 2017. 
 * 
 * This code is unlicensed but please don't plagiarize. 
 */

package jenjinn.engine.misc;

/**
 * @author TB
 * @date 4 Jan 2017
 *
 */
public class Pair<X, Y>
{
     public final X x;
     public final Y y;
     
     public Pair(final X x, final Y y)
     {
          this.x = x;
          this.y = y;
     }
     
     public static <T,U> Pair<T,U> newPair(final T t, final U u)
     {
          return new Pair<T, U>(t, u);
     }
     
     public X first()
     {
          return x;
     }
     
     public Y second()
     {
          return y;
     }
}
