/**
 * Copyright � 2017 Lhasa Limited
 * File created: 12 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.gametree;

/**
 * TODO - Make into interface
 *
 * @author ThomasB
 * @since 12 Jul 2017
 */
public class TranspositionTable
{
	/** We use the rowMapper to map position hashes to row entries. */
	private final long rowMapper;

	/** The size of the table */
	private final int size;

	/** The table of node information. */
	private TableEntry[] table;

	private TranspositionTable(final int powerSize)
	{
		this.size = 1 << powerSize;
		this.rowMapper = size - 1;
		this.table = new TableEntry[size];
	}

	/**
	 * Create a new transposition table, note that twoPower must be less than 32.
	 *
	 * @param twoPower
	 * @return
	 */
	static TranspositionTable create(final int twoPower)
	{
		return new TranspositionTable(twoPower);
	}

	TableEntry get(final long positionHash)
	{
		return table[(int) (positionHash & rowMapper)];
	}

	void set(final TableEntry entry)
	{
		table[(int) (entry.getPositionHash() & rowMapper)] = entry;
	}

	void clear()
	{
		table = new TableEntry[size];
	}
}

/* ---------------------------------------------------------------------*
 * This software is the confidential and proprietary
 * information of Lhasa Limited
 * Granary Wharf House, 2 Canal Wharf, Leeds, LS11 5PS
 * ---
 * No part of this confidential information shall be disclosed
 * and it shall be used only in accordance with the terms of a
 * written license agreement entered into by holder of the information
 * with LHASA Ltd.
 * --------------------------------------------------------------------- */