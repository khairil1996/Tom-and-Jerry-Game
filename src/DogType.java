// DogType.java 				Written by David Wagner
// This Class Describes a Dog

import java.util.Random;

public class DogType extends LocationType
{

	int myNumber;
	char dogChar;
	char [] [] house;
	LocationType player;
	Random randomNum;
	static int numDogs = 0;

	// Define an axis to be either vertical (NORTHSOUTH) 
	// or horizontal (EASTWEST)
	static final int NORTHSOUTH = 0;
	static final int EASTWEST = 1;
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is the constructor for the DogType class
	//
	/////////////////////////////////////////////////////////////////////////
	

	public DogType(int newDogNumber, char [] [] newHouse, 
			LocationType newPlayer, char newDogChar)
	{
		super();
		myNumber = newDogNumber;
		dogChar = newDogChar;
		house = newHouse;
		player = newPlayer;
		randomNum= new Random();
		numDogs++;
	}


	/////////////////////////////////////////////////////////////////////////
	//
	// This function returns a count of how many dogs were created
	//
	/////////////////////////////////////////////////////////////////////////
	


	public static int getNumDogs()
	{
		return numDogs;
	}

	/////////////////////////////////////////////////////////////////////////
	//
	// This function changes the location of the dog to x,y
	//
	/////////////////////////////////////////////////////////////////////////
	
	public void changeLocation(int x, int y)
	{
		house[this.getY()][this.getX()] = ' ';
		house[y][x] = dogChar;
		this.setXY(x,y);	
	}

	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// This function checks if the dog is at location x,y.  
	// If the dog is at this location it returns true
	//
	/////////////////////////////////////////////////////////////////////////
	

	public boolean atLocation(int x, int y)
	{
		if (x == this.getX() && y == this.getY())
		{
			return true;
		}
		return false;
	}

	/////////////////////////////////////////////////////////////////////////
	//
	// This function moves the dog either North or South
	// 
	// The dog always moves North or South towards the player
	// unless there is an obstacle in that location.  If there is an obstacle
	// blocking the way then the dog does not move
	//
	// This function returns true if the dog actually moved
	//
	/////////////////////////////////////////////////////////////////////////
	
		
	public boolean moveNS()
	{
		int newY = this.getY();
		int newX = this.getX();

		if (newY < player.getY())
		{
			newY ++;
		}
		else if (newY > player.getY())
		{
			newY--;
		}

		if (CatDogMouse.playerLocation(newX, newY) ||
		CatDogMouse.emptyLocationForAnimal(newX, newY))
		{
			this.changeLocation(newX, newY);
			return true;
		}
		return false;
	}

	/////////////////////////////////////////////////////////////////////////
	//
	// This function moves the dog either East or West
	// 
	// The dog always moves East or West towards the player
	// unless there is an obstacle in that location.  If there is an obstacle
	// blocking the way then the dog does not move
	//
	// This function returns true if the dog actually moved
	//
	/////////////////////////////////////////////////////////////////////////
	
	public boolean moveEW()
	{
		int newX = this.getX();
		int newY = this.getY();
	
		if (newX < player.getX())
		{
			newX++;
		}
		else if (newX > player.getX())
		{
			newX--;
		}
	
		if (CatDogMouse.playerLocation(newX, newY) ||
				CatDogMouse.emptyLocationForAnimal(newX, newY))
		{
			this.changeLocation(newX, newY);
			return true;
		}
	
		return false;
	}

	/////////////////////////////////////////////////////////////////////////
	//
	// This function moves the dog either along the specified axis.
	// The axis can be NORTHSOUTH or EASTWEST
	// 
	// The dog always moves along that axis towards the player
	// unless there is an obstacle in that location.  If there is an obstacle
	// blocking the way then the dog does not move
	//
	// This function returns true if the dog actually moved
	//
	/////////////////////////////////////////////////////////////////////////
	
	public boolean moveOnAxis(int axis)
	{
		boolean moved = false;
	
		if (axis == NORTHSOUTH)
		{
			moved=moveNS();
		}
		else if (axis == EASTWEST)
		{
			moved=moveEW();
		}
		
		return moved;
	
	}
	/////////////////////////////////////////////////////////////////////////
	//
	// This function moves the dog towards the player.  
	// The direction is chosen to be North-South or
	// East-West at random.  If the dog cannot move in that direction
	// then the other direction is attempted.  If the dog cannot move in
	// either direction, then it does not move.
	//
	/////////////////////////////////////////////////////////////////////////
	
	public void moveDog()
	{
		boolean moved;
		int axis;
	
		// Choose the vertical or horizontal axis at random
		axis = randomNum.nextInt(2);
	
		moved = moveOnAxis(axis);
	
		// If the dog was not successfully moved 
		// try again in the other direction
		if (!moved)
		{
			axis ++;
			axis %= 2;
			moveOnAxis(axis);
		}
	}
	

}
