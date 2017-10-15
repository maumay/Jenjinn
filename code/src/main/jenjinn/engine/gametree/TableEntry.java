/**
 * Copyright � 2017 Lhasa Limited
 * File created: 12 Jul 2017 by ThomasB
 * Creator : ThomasB
 * Version : $Id$
 */
package jenjinn.engine.gametree;

/**
 * TODO - Replace moveIndex field with actual move
 *
 * @author ThomasB
 * @since 12 Jul 2017
 */
public class TableEntry
{
	/** The Zobrist hash of the position this entry corresponds to. */
	private long positionHash;

	/** The type of node this represents in the game tree. */
	private TreeNodeType type;

	/** Score we calculated for this node. */
	private int score;

	/** The index of the best / refutation move if applicable */
	private short moveIndex;

	/** How far we searched from this node to get the result. */
	private byte depthSearched;

	private TableEntry(
			final long positionHash,
			final TreeNodeType type,
			final int score,
			final int moveIndex,
			final int depthSearched)
	{
		this.positionHash = positionHash;
		this.type = type;
		this.score = score;
		this.moveIndex = (short) moveIndex;
		this.depthSearched = (byte) depthSearched;
	}

	/**
	 * Generate a Principal value node entry.
	 *
	 * @param positionHash
	 * @param score
	 * @param bestMoveIndex
	 * @param depthSearched
	 * @return
	 */
	public static TableEntry generatePV(final long positionHash, final int score, final int bestMoveIndex, final int depthSearched)
	{
		return new TableEntry(positionHash, TreeNodeType.PV, score, bestMoveIndex, depthSearched);
	}

	/**
	 * Generate a cut node entry.
	 *
	 * @param positionHash
	 * @param score
	 * @param bestMoveIndex
	 * @param depthSearched
	 * @return
	 */
	public static TableEntry generateCUT(final long positionHash, final int lowerBound, final int refutationMoveIndex, final int depthSearched)
	{
		return new TableEntry(positionHash, TreeNodeType.CUT, lowerBound, refutationMoveIndex, depthSearched);
	}

	/**
	 * Generate an all node entry.
	 *
	 * @param positionHash
	 * @param score
	 * @param bestMoveIndex
	 * @param depthSearched
	 * @return
	 */
	public static TableEntry generateALL(final long positionHash, final int upperBound, final int depthSearched)
	{
		return new TableEntry(positionHash, TreeNodeType.ALL, upperBound, -1, depthSearched);
	}

	public long getPositionHash()
	{
		return positionHash;
	}

	public TreeNodeType getType()
	{
		return type;
	}

	public int getScore()
	{
		return score;
	}

	public short getMoveIndex()
	{
		return moveIndex;
	}

	public byte getDepthSearched()
	{
		return depthSearched;
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