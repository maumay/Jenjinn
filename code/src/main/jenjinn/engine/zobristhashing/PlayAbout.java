/**
 *
 */
package jenjinn.engine.zobristhashing;

import java.util.Random;

/**
 * @author ThomasB
 *
 */
public class PlayAbout
{
	private static final String LINE_SEPARATOR = "---------------";

	private static final long SEED = 4L;

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		final Random r = new Random(SEED);

		for (int i = 0; i < 5; i++)
		{
			printSeparator();
			final long generated = r.nextLong();
			System.out.println(generated);
			System.out.println(Long.toBinaryString(generated));
			printSeparator();
		}

		final long one = r.nextLong(), two = r.nextLong();

		System.out.println(Long.toBinaryString(one));
		System.out.println(Long.toBinaryString(two));
		System.out.println(Long.toBinaryString(one ^ two));
	}

	private static void printSeparator()
	{
		System.out.println(LINE_SEPARATOR);
	}

}
