// LocationType.java                          Written by David Wagner
// Define a location in the house as a combination of X and Y Coordinates

public class LocationType
{
	public int XCoordinate;
	public int YCoordinate;
	
	public LocationType()
	{
		this(0,0);
	}

	public LocationType(LocationType newLocation)
	{
		this (newLocation.getX(), newLocation.getY());
	}
	
	public LocationType(int newXCoordinate, int newYCoordinate)
	{
		setXY(newXCoordinate, newYCoordinate);
	}

	public int getX()
	{
		return XCoordinate;
	}

	public int getY()
	{
		return YCoordinate;
	}

	public void setXY(int newXCoordinate, int newYCoordinate)
	{
		XCoordinate = newXCoordinate;
		YCoordinate = newYCoordinate;
	}

	public void setLocation(LocationType myLocation)
	{
		setXY(myLocation.getX(), myLocation.getY());
	}

}
