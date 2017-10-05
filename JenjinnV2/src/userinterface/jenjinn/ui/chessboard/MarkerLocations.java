/**
 * 
 */
package jenjinn.ui.chessboard;

import java.util.ArrayList;
import java.util.List;

import jenjinn.engine.enums.Sq;

/**
 * @author t
 *
 */
public final class MarkerLocations 
{
	private Sq locationMarker;
	
	private List<Sq> movementMarkers = new ArrayList<>();
	
	private List<Sq> attackMarkers = new ArrayList<>();

	/**
	 * @return the attackMarkers
	 */
	public List<Sq> getAttackMarkers() 
	{
		return attackMarkers;
	}
	
	/**
	 * @return the locationMarker
	 */
	public Sq getLocationMarker() 
	{
		return locationMarker;
	}

	/**
	 * @return the movementMarkers
	 */
	public List<Sq> getMovementMarkers() 
	{
		return movementMarkers;
	}

	public void clear() 
	{
		locationMarker = null;
		movementMarkers.clear();
		attackMarkers.clear();
	}

	public void set(Sq loc, List<Sq> movementMarkers, List<Sq> attackMarkers) 
	{
		locationMarker = loc;
		this.movementMarkers.addAll(movementMarkers);
		this.attackMarkers.addAll(attackMarkers);
	}
}
