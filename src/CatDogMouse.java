// CatDogMouse.java                          Written by David Wagner
// Project for ObjectOrientedProgramming

// PLEASE RUN THE CatGUI.java!!
import java.util.Scanner;
import java.util.Formatter;
import java.util.Random;
import java.io.File;
// PLEASE RUN THE CatGUI.java!!
public class CatDogMouse
{
	// PLEASE RUN THE CatGUI.java!!

	//////////////////////
	// Constant Values  //
	//////////////////////
	
	// The house is a square and this is the length of each side 
	static final int HOUSESIZE = 30;
	
	// This is the smallest possible size of the house
	static final int MINHOUSESIZE=18;
	
	// This is the maximum number of dogs and mice that can appear in the house
	static final int MAXDOGS=20;
	static final int MAXMICE=20;

	// This is the starting floor of the house
	static final int MINFLOOR=1;
	
	// A location in the house can contain an obsatcle, player, dog or mouse
	static final char OBSTACLECHAR='.';
	static final char CATCHAR='C';
	static final char DOGCHAR='D';
	static final char MOUSECHAR='M';
	static final char UPCHAR='^';
	static final char DOWNCHAR='v';
	static final char EXITCHAR='H';
	
	
	// The name of the high score file
	static String HIGHSCOREFILE="CatDogMouseHighScore.txt";
	
	// If this is the first time you run the program, this is the high score
	static final int STARTHIGHSCORE=0;
	
	// Define the size of an obstacle free center region
	// This also defines the region where the cat must start
	// And where no dogs can start
	static final int TOOCLOSE=4;

	// Define the size of an obstructed external region on the top floor
	static final int TOOFAR=8;
	
	static final int NOMOUSEINDEX=-1;
	
	
	// Define an axis to be either vertical (NORTHSOUTH) or horizontal (EASTWEST)
	static final int NORTHSOUTH = 0;
	static final int EASTWEST = 1;
	
	//////////////////////
	// Global Variables //
	//////////////////////
	
	// This is the house variable
	// It is a 2 dimensional array of characters
	static char [][] house;
	
	// This is the count of how many dogs and mice are currently in the house
	static int numDogs;
	static int numMice;
	
	// This is the current size of the house
	static int houseSize;
	
	// These are the current min and max coordinates of the house
	static int houseMinCoord;
	static int houseMaxCoord;
	static float houseMidCoord;
	
	// This is how much smaller the house becomes after every level
	static int houseSizeIncrement;
	
	// This is the current number of the house
	static int houseNumber;
	
	// This is the current floor number where the player is located
	static int floorNum;
	
	// This is the highest floor reached by the player
	static int maxFloorNum;
	
	// This variable contains the location of the player
	static LocationType player;
	
	// This variable conatains the locations of the dogs
	static DogType [] dogs;
	
	// This variable contains the locations of the mice
	static MouseType [] mouse;
	
	// This variable contains the location of the up and down stairs
	static LocationType upStairs, downStairs;
	
	// This variable contains the location of the exit
	static LocationType houseExit;
	
	// This is how many mice you are carrying
	static int miceCarried;

	// This is how many mice you have removed
	static int miceRemoved;

	// This is your high score, which is the tallest house you have reached so far
	static int highScore;

	// Random number generator
	static Random randomNum;

	// Scanner to read from a file or from the command line
	static Scanner input;

	// Formatter to write to a file
	static Formatter output;

	/////////////////////////////////////////////////////////////////////////
	//
	// Print out the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void printHouse()
	{
		int i,j;
	
		System.out.printf("\f");  // Clear the screen
		System.out.printf("\n==============================\n");
		System.out.printf("House:%d Floor:%d High Score:%d\n",
				houseNumber, floorNum, highScore);
		System.out.printf("Mice Carried:%d Mice Removed:%d\n", 
				miceCarried, miceRemoved);
		System.out.printf("==============================\n");
	
		for (i=0; i<HOUSESIZE; i++)
		{
			for (j=0; j<HOUSESIZE; j++)
			{
				System.out.printf("%c", house[i][j]);
			}
			System.out.printf("\n");
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// Print out the key, explaining what the various letters are in the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void printKey()
	{
		System.out.printf("\n");
		//System.out.printf("mc = %d\n", MouseType.getNumMouse());
		System.out.printf("Cat (Player) = %c\n", CATCHAR);
		System.out.printf("Guard Dog = %c\n", DOGCHAR);
		System.out.printf("Mouse = %c\n", MOUSECHAR);
		System.out.printf("Up Stairs = %c\n", UPCHAR);
		System.out.printf("Down Stairs = %c\n", DOWNCHAR);
		System.out.printf("Helicopter = %c\n", EXITCHAR);
		System.out.printf("\n");
	
	}

	/////////////////////////////////////////////////////////////////////////
	//
	// Print out the house and the key
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void printHouseAndKey()
	{
		printHouse();
		printKey();
	}


	/////////////////////////////////////////////////////////////////////////
	//
	// Add walls to the four sides of the house
	//
	/////////////////////////////////////////////////////////////////////////

	public static void buildHouseWalls()
	{
		int i;
		for (i=houseMinCoord; i<houseMaxCoord; i++)
		{
			house[houseMinCoord][i] = '-';
			house[houseMaxCoord-1][i] = '-';
			house[i][houseMinCoord] = '|';
			house[i][houseMaxCoord-1] = '|';
	
		}
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// Return true if the given coordinates are inside the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static boolean insideHouse(int i, int j)
	{
		if (i > houseMinCoord && i < houseMaxCoord-1 && j > houseMinCoord && j < houseMaxCoord-1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// Return true if the given coordinates are outside the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static boolean outsideHouse(int i, int j)
	{
		if (i < houseMinCoord || i > houseMaxCoord-1 || j < houseMinCoord || j > houseMaxCoord-1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// Return the square of the distance from the coords to the house center 
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static float centerDistSquared(int i, int j)
	{
		float iDist = i - houseMidCoord;
		float jDist = j - houseMidCoord;
	
		return (iDist * iDist) + (jDist * jDist);
	}
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// This funcation returns true if the coords (i,j) are closer
	// to the house center than the given distance 
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static boolean closerToCenter(int i, int j, float distance)
	{
		if (centerDistSquared(i,j) < distance * distance)
		{
			return true;
		}
		return false;
	}
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// This funcation returns true if the player is on the bottom floor
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static boolean bottomFloor()
	{
		if (floorNum <= MINFLOOR)
		{
			return true;
		}
		return false;
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// This funcation returns true if the player is on the top floor of the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static boolean topFloor()
	{
		if (houseSize <= MINHOUSESIZE)
		{
			return true;
		}
		return false;
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// Add some obstacles and empty spaces to the house at random
	// The center of the house should have no obstacles
	// The region away from the center of the top floor should be all obstacles
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void buildHouseObstacles()
	{
		int i, j;
		for (i=0; i<HOUSESIZE; i++)
		{
			for (j=0; j<HOUSESIZE; j++)
			{
				if (outsideHouse(i,j)) 
				{
					house[i][j] = ' ';
				}
	
				else if (insideHouse(i,j))
				{
					if (closerToCenter(i,j, TOOCLOSE))
					{
						house[i][j] = ' ';
					} 
					else if (topFloor() &&
						!closerToCenter(i,j, TOOFAR))
					{
						house[i][j] = OBSTACLECHAR;
					} 
					else if (randomNum.nextInt(5) == 0) 
					{
						house[i][j] = OBSTACLECHAR;
					} 
					else
					{
						house[i][j] = ' ';
					}
				}
			}
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	// 
	// Create a new high score file
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void createHighScoreFile()
	{
	
		System.out.printf("Creating High Score file\n");
	
		try {
			output = new Formatter(HIGHSCOREFILE);
			output.format("%d\n", STARTHIGHSCORE);
		}
		catch (Exception error)
		{
			System.out.printf("Error creating new high score file\n");
			return;
		}
	
		output.close();
	
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	// 
	// Load the high score from a file
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void loadHighScore()
	{
	
		try {
			// read the high score from the file
			input = new Scanner(new File(HIGHSCOREFILE));
			highScore = input.nextInt();
		}
		catch(Exception error)
		{
			// the file does not exist yet so create it
			highScore = STARTHIGHSCORE;
			createHighScoreFile(); 
			return;
		} 
	
		input.close();
	}
	
	/////////////////////////////////////////////////////////////////////////
	// 
	// Save the high score to a file
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void saveHighScore()
	{
	
		try {
			output = new Formatter(HIGHSCOREFILE);
			output.format("%d\n", highScore);
		}
	
		catch (Exception error)
		{
			System.out.printf("Error saving the high score to a file\n");
			return;
		}

		output.close();

	}

	/////////////////////////////////////////////////////////////////////////
	// 
	// Check if the high score has been beaten
	// If yes, then save the new high score
	//
	/////////////////////////////////////////////////////////////////////////

	public static void checkAndUpdateHighScore()
	{
	
		if (miceRemoved > highScore)
		{
			highScore = miceRemoved;
			saveHighScore();
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	//
	// Pick a random location in the house, and assign that location to myLocation
	//
	// This function changes the value of myLocation in the parameter
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void chooseRandomHouseLocation(LocationType myLocation)
	{
		int randomX = randomNum.nextInt(houseMaxCoord-houseMinCoord);
		int randomY = randomNum.nextInt(houseMaxCoord-houseMinCoord);
		myLocation.setXY(houseMinCoord + randomX, houseMinCoord + randomY);
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// This function checks if the house is empty at location x,y
	// If the house is empty at this location it returns true
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static boolean emptyLocation(int x, int y)
	{
		// out of range check
		if(x < 0 || y < 0 || x >= houseSize || y >= houseSize) return false;
		return " ^vHMD".indexOf(house[y][x]) != -1;
	}
	public static boolean emptyLocationForAnimal(int x, int y)
	{
		// out of range check
		if(x < 0 || y < 0 || x >= houseSize || y >= houseSize) return false;
		return " ".indexOf(house[y][x]) != -1;
	}
	
	public static boolean emptyLocation(LocationType myLocation)
	{
		return emptyLocation(myLocation.getX(), myLocation.getY());
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// This function checks if the player is at location x,y
	// If the player is at this location it returns true
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static boolean playerLocation(int x, int y)
	{
		if (x == player.getX() && y == player.getY())
		{
			return true;
		}
		return false;
	}
	
	public static boolean playerLocation(LocationType myLocation)
	{
		return playerLocation(myLocation.getX(), myLocation.getY());
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// This function checks if the UP stairs is at location x,y
	// If the UP stairs is at this location it returns true
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static boolean upStairsLocation(int x, int y)
	{
		if (x == upStairs.getX() && y == upStairs.getY())
		{
			return true;
		}
		return false;
	}
	
	public static boolean upStairsLocation(LocationType myLocation)
	{
		return upStairsLocation(myLocation.getX(), myLocation.getY());
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// This function checks if the DOWN stairs is at location x,y
	// If the UP stairs is at this location it returns true
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static boolean downStairsLocation(int x, int y)
	{
		if (x == downStairs.getX() && y == downStairs.getY())
		{
			return true;
		}
		return false;
	}
	
	public static boolean downStairsLocation(LocationType myLocation)
	{
		return downStairsLocation(myLocation.getX(), myLocation.getY());
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// This function checks if the DOWN stairs is at location x,y
	// If the UP stairs is at this location it returns true
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static boolean exitLocation(int x, int y)
	{
		if (x == houseExit.getX() && y == houseExit.getY())
		{
			return true;
		}
		return false;
	}
	
	public static boolean exitLocation(LocationType myLocation)
	{
		return exitLocation(myLocation.getX(), myLocation.getY());
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// This function checks if a specific mouse, with the index mouseNum, 
	// is at location x,y.  If the mouse is at this location it returns true.
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static boolean mouseNumberAtLocation(int x, int y, int mouseNum)
	{
		if (x == mouse[mouseNum].getX() && y == mouse[mouseNum].getY())
		{
			return true;
		}
		return false;
	}
	
	public static boolean mouseNumberAtLocation(LocationType myLocation, int mouseNum)
	{
		return mouseNumberAtLocation(myLocation.getX(), myLocation.getY(), mouseNum);
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is an integer function.  It can return an integer
	//
	// This function checks if any mouse is at location x,y
	// If any mouse is at this location it returns the index of that mouse.
	// If there is no mouse then this returns -1.
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static int mouseIndexAtLocation(int x, int y)
	{
		int mouseNum;
	
		for (mouseNum = 0; mouseNum < numMice; mouseNum++)
		{
			if (mouseNumberAtLocation(x, y, mouseNum))
			{
				return mouseNum;
			}
		}
		return NOMOUSEINDEX;
	}
	
	public static int mouseIndexAtLocation(LocationType myLocation)
	{
		return mouseIndexAtLocation(myLocation.getX(), myLocation.getY());
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// This function checks if any mouse is at location x,y
	// If any mouse is at this location it returns true.
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static boolean mouseLocation(int x, int y)
	{
		if (mouseIndexAtLocation(x, y) == NOMOUSEINDEX)
		{
			return false;
		}
		return true;
	}
	
	public static boolean mouseLocation(LocationType myLocation)
	{
		return mouseLocation(myLocation.getX(), myLocation.getY());
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// This function checks if the specific dog, with the number dogNumber,
	// is at location x,y.  If that dog is at this location it returns true
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static boolean dogNumberAtLocation(int x, int y, int dogNumber)
	{
//		if (x == dogs[dogNumber].getX() && 
//				y == dogs[dogNumber].getY())
//		{
//			return true;
//		}
//		return false;
		return dogs[dogNumber].atLocation(x,y);
	}
	
	public static boolean dogNumberAtLocation(LocationType myLocation, int dogNumber)
	{
		return dogNumberAtLocation(myLocation.getX(), myLocation.getY(), dogNumber);
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// This function checks if any dog is at location x,y
	// If any dog is at this location it returns true
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static boolean dogLocation(int x, int y)
	{
		int dogNum;
	
		for (dogNum = 0; dogNum < numDogs; dogNum++)
		{
			if (dogNumberAtLocation(x, y, dogNum))
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean dogLocation(LocationType myLocation)
	{
		return dogLocation(myLocation.getX(), myLocation.getY());
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// Pick an empty location in the house at random, 
	// and assign that location to myLocation
	//
	// This function changes the value of myLocation in the parameter
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void chooseEmptyHouseLocation(LocationType myLocation)
	{
		chooseRandomHouseLocation(myLocation);
	
		while (!emptyLocation(myLocation))
		{
			chooseRandomHouseLocation(myLocation);
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This funcation starts by putting the stairs and the mouse outside the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void initializeStairsAndExit()
	{
		houseExit.setXY(-1,-1);
		upStairs.setXY(-1,-1);
		downStairs.setXY(-1,-1);
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This funcation adds the exit to an empty location in the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void addExit()
	{
		LocationType myLocation = new LocationType();
		
		chooseEmptyHouseLocation(myLocation);
		houseExit.setLocation(myLocation);
		house[myLocation.getY()][myLocation.getX()] = EXITCHAR;
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This funcation adds an up stairs to an empty location in the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void addUpStairs()
	{
		LocationType myLocation = new LocationType();
	
		chooseEmptyHouseLocation(myLocation);
		upStairs.setLocation(myLocation);
		house[myLocation.getY()][myLocation.getX()] = UPCHAR;
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This function adds a down stairs to an empty location in the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void addDownStairs()
	{
		LocationType myLocation = new LocationType();
	
		chooseEmptyHouseLocation(myLocation);
		downStairs.setLocation(myLocation);
		house[myLocation.getY()][myLocation.getX()] = DOWNCHAR;
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This function adds an exit and stairs to the house if necessary
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void addExitandStairs()
	{
		initializeStairsAndExit();
		if (topFloor()) addExit();
		if (!bottomFloor()) addDownStairs();
		if (!topFloor()) addUpStairs();
			
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This funcation adds the player to an empty location in the house
	// The player is always added close to the center of the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void addPlayer()
	{
		LocationType myLocation = new LocationType();
	
		chooseEmptyHouseLocation(myLocation);
		while(!closerToCenter(myLocation.getX(),myLocation.getY(),TOOCLOSE)) chooseEmptyHouseLocation(myLocation);
		player.setLocation(myLocation);
		house[myLocation.getY()][myLocation.getX()] = CATCHAR;
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This funcation adds a specific dog, with the number dogNumber,
	// to an empty location in the house
	// The dog is never added close to the center of the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void addOneDog(int dogNumber)
	{
		DogType myLocation = new DogType(dogNumber, house, player, DOGCHAR);
	
		chooseEmptyHouseLocation(myLocation);
		while(closerToCenter(myLocation.getX(),myLocation.getY(),TOOCLOSE)) chooseEmptyHouseLocation(myLocation);
		dogs[dogNumber].setLocation(myLocation);
		house[myLocation.getY()][myLocation.getX()] = DOGCHAR;
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This funcation adds all dogs to empty locations in the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void addManyDogs()
	{
		int dogNumber;
	
		for (dogNumber = 0; dogNumber < numDogs; dogNumber++)
		{
			addOneDog(dogNumber);
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This funcation adds one mouse to an empty location in the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void addOneMouse(int mouseNumber)
	{
		MouseType myLocation = new MouseType(mouseNumber, house, player, MOUSECHAR);
	
		chooseEmptyHouseLocation(myLocation);
		mouse[mouseNumber].setLocation(myLocation);
		house[myLocation.getY()][myLocation.getX()] = MOUSECHAR;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////
	//
	// This funcation copies one mouse from one position of the array to another
	//
	/////////////////////////////////////////////////////////////////////////////
	
	public static void copyMouse(int toIndex, int fromIndex)
	{
		int x, y;
	
		mouse[toIndex].setLocation(mouse[fromIndex]);
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This funcation adds all mouse to empty locations in the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void addManyMice()
	{
		int mouseNumber;
	
		for (mouseNumber = 0; mouseNumber < numMice; mouseNumber++)
		{
			addOneMouse(mouseNumber);
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	//  This function prints out the menu, asking the user to give a direction
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void printMenu()
	{
		System.out.printf("Which direction (N)orth, (S)outh, (E)ast, (W)est or (Q)uit: ");
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	//  This function reads one character from the user
	//
	//  The character read is converted to uppercase and is returned
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static char getCommand()
	{
		String commandStr;
		char commandChar = 'x';
	
		input = new Scanner(System.in);
	
		commandStr = input.nextLine();
		if (commandStr.length() > 0) commandChar = commandStr.charAt(0);
		commandChar = Character.toUpperCase(commandChar);
	
		return Character.toUpperCase(commandChar);
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This function waits for the user to press ENTER
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void pause()
	{
		String commandStr;
		char commandChar = 'x';
	
		System.out.printf ("Press ENTER to continue...");
		commandStr = input.nextLine();
	
	}


	/////////////////////////////////////////////////////////////////////////
	//
	// This function changes the location of the player to x,y
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void changePlayerLocation(int x, int y)
	{
		house[player.getY()][player.getX()] = ' ';
	
		house[y][x] = CATCHAR;
		
		player.setXY(x,y);
	}
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This function moves the player according to the command
	// The command should be 'N', 'S', 'E', or 'W'
	//
	// The player will move North, South, East, or West according to the 
	// command character unless there is an obstacle in that location.
	// If there is an obstacle in that location then the player does not move
	//
	/////////////////////////////////////////////////////////////////////////

	private static void tryMove(int x, int y) {
		if(!emptyLocation(x, y)) return;
		changePlayerLocation(x, y);
	}

	public static void movePlayer(char directionChar)
	{
		switch(directionChar) {
			case 'N': case 'n': tryMove(player.getX(),player.getY()-1); break;
			case 'S': case 's': tryMove(player.getX(),player.getY()+1); break;
			case 'W': case 'w': tryMove(player.getX()-1,player.getY()); break;
			case 'E': case 'e': tryMove(player.getX()+1,player.getY()); break;
		}
		//////////////////////////////////////////////////////////////
		// directionChar is 'N', 'S', 'E', or 'W'                   //
		// Use player.getX() and player.getY() to find the location //
		// Use changePlayerLocation(x,y) to change the location     //
		// Use emptyLocation(x,y) to test if a location is empty    //
		//////////////////////////////////////////////////////////////

	}

	
	/////////////////////////////////////////////////////////////////////////
	//
	// This function moves the specific dog, with the number dogNumber,
	// towards the player.  The direction is chosen to be North-South or
	// East-West at random.  If the dog cannot move in that direction
	// then the other direction is attempted.  If the dog cannot move in
	// either direction, then it does not move.
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void moveOneDog(int dogNumber)
	{
		dogs[dogNumber].moveDog();
	}
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This function moves all of the dogs
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void moveAllDogs()
	{
		int i;
	
		for (i=0; i<numDogs; i++)
		{
			moveOneDog(i);
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This function moves the mouse away from the player.  
	//
	// The direction is chosen to be North-South or
	// East-West at random.  If the mouse cannot move in that direction
	// then the other direction is attempted.  If the mouse cannot move in
	// either direction, then it does not move.
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void moveOneMouse(int mouseNum)
	{
		mouse[mouseNum].moveMouse();
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This function moves all of the mice
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void moveAllMice()
	{
		for(int i=0; i<numMice; i++) moveOneMouse(i);
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This function collects the mouse if the player has found a mouse
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void collectMouse()
	{
		int mouseIndex = mouseIndexAtLocation(player);
	
		// If there is no mouse here then quit this function
		if (mouseIndex == NOMOUSEINDEX) return;
	
		// Swap the last mouse in the array into this index
		copyMouse(mouseIndex, numMice-1);
		numMice--;
		miceCarried++;
		
	}
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This function moves the player and the dogs and the mice.  
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void movePlayerAndDogsAndMice(char directionChar)
	{
		// Move the mouse only if we are in house 3 or later
		if (houseNumber >= 3) moveAllMice();
	
		// Move the player according to the direction entered
		movePlayer(directionChar);
		collectMouse();

		// Move the dogs and mouse according to their moving strategy
		moveAllDogs();
		moveAllMice();
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This function changes the size of the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void changeHouseSize(int newHouseSize)
	{
		houseSize=newHouseSize;
		houseMinCoord = (HOUSESIZE-houseSize)/2;
		houseMaxCoord = HOUSESIZE-(HOUSESIZE-houseSize)/2;
		houseMidCoord = (houseMinCoord + houseMaxCoord-1)/2;
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This function climbs up the stairs to the 
	// next higher level of the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void climbUpStairs()
	{
		floorNum++;
		// Increase the size of the house
		changeHouseSize(houseSize-houseSizeIncrement);
		// Add one dog for the next level
		if (numDogs < MAXDOGS) numDogs++;
		// If we have not visited this level yet, then add mouse
		if (maxFloorNum < floorNum){
			maxFloorNum = floorNum;
			numMice = numDogs * houseNumber;
		} else {
			// If we have already visited this level, then there
			// are no mice on this level
			numMice = 0;
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This function climb down the stairs to the 
	// next lower level of the house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void climbDownStairs()
	{
		floorNum--;
		// Decrease the size of the house
		changeHouseSize(houseSize+houseSizeIncrement);
		// Remove one dog for the next level
		numDogs--;
	
		numMice = 0;
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// This function returns true if the player has reached the exit,
	// meaning that the house is finished.
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static boolean houseFinished()
	{
		if (exitLocation(player))
		{
			return true;
		}
		return false;
	}
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This is a boolean function.  It can return true or false
	//
	// This function returns true if a dog has reached the player,
	// meaning that the game is lost.
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static boolean gameLost()
	{
		if (dogLocation(player))
		{
			return true;
		}
		return false;
	}
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This function prints a message informing the user that he has won
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void printWinMessage()
	{
		System.out.printf("**********************************\n");
		System.out.printf("*** You Escaped with %d ", miceCarried);
		if (miceCarried == 1) System.out.printf("mouse! ***\n");
		else System.out.printf("mice! ***\n");
		System.out.printf("**********************************\n");
		System.out.printf("\n\n");
	}
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This function prints a message informing the user that he has lost
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void printLoseMessage()
	{
		System.out.printf("++++++++++++++++++++++++\n");
		System.out.printf(" You were Caught... T.T \n");
		System.out.printf("    Your score is %d \n", miceRemoved);
		System.out.printf("++++++++++++++++++++++++\n");
		System.out.printf("\n\n");
	}
	
	/////////////////////////////////////////////////////////////////////////
	//
	// This function starts the player in a new house
	//
	/////////////////////////////////////////////////////////////////////////
	
	public static void startHouseNumber(int number)
	{
		houseNumber=number;
		houseSizeIncrement = (HOUSESIZE - MINHOUSESIZE) / number;
		// Start each house with the biggest possible house
		changeHouseSize(HOUSESIZE);
		// Start each house with 1 dog
		numDogs=1;
		// Start each house with the same amount of mice as the house number
		numMice=houseNumber;
		// Start on the bottom floor, and reset the highest visited floor
		floorNum=1;
		maxFloorNum=floorNum;
	
		// Remove all mice carried
		miceRemoved += miceCarried;
		miceCarried = 0;
	
		// Update the high score with the new mice removed
		checkAndUpdateHighScore();
	
	}

	/////////////////////////////////////////////////////////////////////////
	//
	// This function initializes the global variables
	//
	/////////////////////////////////////////////////////////////////////////

	public static void initVariables()
	{
		int i;
		// Seed the random number generator
		randomNum = new Random(); 
		house = new char [HOUSESIZE][HOUSESIZE];
		player = new LocationType();
 		dogs = new DogType [MAXDOGS];
		for (i=0; i<MAXDOGS; i++) dogs[i] = new DogType(i, house, player, DOGCHAR);
		mouse = new MouseType [MAXMICE];
		for (i=0; i<MAXMICE; i++) mouse[i] = new MouseType(i,house,player,MOUSECHAR);
		upStairs = new LocationType();
		downStairs = new LocationType();
		houseExit = new LocationType();
	}


	///////////////////
	// main function //
	///////////////////
	
	
	/*public static void main(String [] args)
	{
		char command;

//		initVariables();

		// Load the previous high score from the high score file
//		loadHighScore();

		// Start the player in the first house
//startHouseNumber(1);

		// Loop until the player wins, loses, or quits
		while(true)
		{
			// Build walls and obstacles in the house
//			buildHouseWalls();
//			buildHouseObstacles();

			// Add the dogs, mice, cat, exit, and stairs to the house
//			addManyDogs();
//			addManyMice();
//			addPlayer();
//			addExitandStairs();

			// Stay in this house until the player reaches a stairs
			while(true)
			{
				// Print the house and the key explaining each letter
				printHouseAndKey();

				// Ask the user to enter a command and get that command
//				printMenu();
				command = getCommand();

				// If command is Q then quit the program
//				if (command == 'Q')
//				{
//					return;
//				}

				// Move the player, dogs, and mice
				// according to the command entered
				movePlayerAndDogsAndMice(command);

				// If the player is reached the up stairs, climb up
				if (upStairsLocation(player))
				{
					climbUpStairs();
					break;
				}

				// If the player is reached the down stairs, climb down
				if (downStairsLocation(player))
				{
					climbDownStairs();
					break;
				}

				// If the player wins then break out of the house
				// and advance to the next house
				if (houseFinished())
				{
					printHouseAndKey();
					printWinMessage();


					pause();

					startHouseNumber(houseNumber + 1);

					break;
				}

				// If the player loses, then quit the program
				if (gameLost())
				{
					printHouseAndKey();
					printLoseMessage();

					return;
				}

			}
		}
	}*/
}
