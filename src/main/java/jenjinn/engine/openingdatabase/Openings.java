/**
 *
 */
package jenjinn.engine.openingdatabase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author t
 *
 */
public final class Openings
{
//	private static final String[] ORDERING1 = {"petrosiandb.txt",
//			"karpovdb.txt",
//			"carlsendb.txt",
//			"fischerdb.txt",
//			"topalovdb.txt",
//			"taldb.txt",
//			"grischukdb.txt",
//	};
//
//	private static final String[] ORDERIN2 = {"carlsendb.txt",
//			"petrosiandb.txt",
//			"fischerdb.txt",
//			"taldb.txt",
//			"karpovdb.txt",
//			"topalovdb.txt",
//			"grischukdb.txt",
//	};

	private static final String[] B_ORDERING = {
			"bclassickings.zip",
			"bmodernkings.zip",
			"bclassicqueens.zip",
			"bmodernqueens.zip",
			"bflank.zip"
	};

	private static final String[] W_ORDERING = {
			"wclassickings.zip",
			"wmodernkings.zip",
			"wclassicqueens.zip",
			"wmodernqueens.zip",
			"wflank.zip"
	};



	/**
	 *
	 */
	private Openings()
	{
		// TODO Auto-generated constructor stub
	}

	public static List<String> getBlackOpenings()
	{
		return Arrays.stream(B_ORDERING).map(s -> "/" + s).collect(Collectors.toList());
	}

	public static List<String> getWhiteOpenings()
	{
		return Arrays.stream(W_ORDERING).map(s -> "/" + s).collect(Collectors.toList());
	}
}
