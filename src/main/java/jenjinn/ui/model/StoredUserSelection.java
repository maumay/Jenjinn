/**
 * 
 */
package jenjinn.ui.model;

import jenjinn.engine.enums.Sq;

/**
 * @author ThomasB
 */
public class StoredUserSelection
{
	private Sq storedSquare;

	StoredUserSelection()
	{
	}

	Sq getStoredSquare()
	{
		return storedSquare;
	}

	void setStoredSquare(final Sq firstClickedSquare)
	{
		this.storedSquare = firstClickedSquare;
	}

	void reset()
	{
		storedSquare = null;
	}

	boolean selectionIsSet()
	{
		return storedSquare != null;
	}
}
