/**
 * 
 */
package jenjinn.engine.evaluation.componentimpl;

/**
 * @author t
 *
 */
public class PawnTable
{
	private static final int DEFAULT_SIZE = 13;

	/** We use the rowMapper to map position hashes to row entries. */
	private final long rowMapper;

	/** The size of the table */
	private final int size;

	/** The table of node information. */
	private PawnTableEntry[] table;

	private PawnTable(final int powerSize)
	{
		this.size = 1 << powerSize;
		this.rowMapper = size - 1;
		this.table = new PawnTableEntry[size];
	}

	/**
	 * Create a new transposition table, note that twoPower must be less than 32.
	 *
	 * @param twoPower
	 * @return
	 */
	static PawnTable create(final int twoPower)
	{
		return new PawnTable(twoPower);
	}

	static PawnTable createDefault()
	{
		return new PawnTable(DEFAULT_SIZE);
	}

	PawnTableEntry get(final long positionHash)
	{
		return table[(int) (positionHash & rowMapper)];
	}

	void set(final PawnTableEntry entry)
	{
		table[(int) (entry.posHash & rowMapper)] = entry;
	}

	void clear()
	{
		table = new PawnTableEntry[size];
	}
}
