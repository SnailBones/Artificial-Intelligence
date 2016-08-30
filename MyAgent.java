
package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;


public class MyAgent extends BasicMarioAIAgent implements Agent
{

	public MyAgent()
	{
		super("MyAgent");
		reset();
	}

	// Does (row, col) contain an enemy?
	public boolean hasEnemy(int row, int col) {
		return enemies[row][col] != 0;
	}

	// Is (row, col) empty?
	public boolean isEmpty(int row, int col) {
		return (levelScene[row][col] == 0);
	}

	public boolean isWall(int row, int col) {
		if (levelScene[row][col]  == 1 || levelScene[row][col]  == -60)
			return true;
		else
			return false;
	}

	public boolean beast_near;

	public boolean wallOrBeast(int row, int col) {
		if (isWall(row, col) ||  hasEnemy(row, col))
			return true;
		else
			return false;
	}


	// Display Mario's view of the world
	public void printObservation() {
		System.out.println("**********OBSERVATIONS**************");
		for (int i = 0; i < mergedObservation.length; i++) {
			for (int j = 0; j < mergedObservation[0].length; j++) {
				System.out.print(levelScene[i][j] + " ");
				// if (i == mergedObservation.length / 2 && j == mergedObservation.length / 2) {
				// 	System.out.print("M ");
				// }
				// else if (hasEnemy(i, j)) {
				// 	System.out.print("E ");
				// }
				// else if (!isEmpty(i, j)) {
				// 	System.out.print("B ");
				// }
				// else {
				// 	System.out.print(" ");
				//}
			}
			System.out.println();
		}
		System.out.println("************************");
	}

	// public boolean danger(int dist)	//max 9
	// {
	// 		int c;
	// 		for (c=9; c<10+ dist; c++)	//for each column in front
	// 		{
	// 				//System.out.println("at column " + c);
	// 				if (wallOrBeast(9, c))	//make sure it is safe
	// 				{
	// 					//System.out.println("not safe!");
	// 					return true;
	// 				}
	// 		}
	// 		//System.out.println("safe");
	// 		return false;
	// }
	public boolean doJump()
	{
			beast_near = false;
			boolean jump = false;
			for (int c=9; c<11; c++)	//for each column in front
			{
					//System.out.println("at column " + c);
					if (isWall(9, c ))	//make sure it is safe
						jump = true;
					if (hasEnemy(9,c))
					{
						beast_near = true;
						jump = true;
					}
			}

			for (int c=11; c<19; c++)
			{
				if (hasEnemy(9,c))
					beast_near = true;
			}
			if (isEmpty(10, 10) && isMarioOnGround)	//if mario is on a cliff
				jump = true;
			else if (!isMarioOnGround)
			{
					if ((isEmpty(10,10) || isEmpty(10,9)))
					{
						jump = true;
					}
					//else
			 }
			return jump;
	}

	public boolean beastBelow()
	{
		for (int i = 10; i<13; i++) {
			if (hasEnemy(i,9)) {
				return true;
			}
		}
		return false;
	}

	public boolean beastBeside()
	{
		for (int r = 7; r<=9; r++){
		//for (int r = 0; r<= 18; r++){
			for (int c = 10; c<= 12; c++){
			//for (int c = 0; c<= 18; c++){	//avoid all things above
				if (hasEnemy(r,c))
					return true;
			}
		}
		return false;
	}
	//boolean retreating;


	// Actually perform an action by setting a slot in the action array to be true
	public boolean[] getAction()
	{
		//action[Mario.KEY_SPEED] = action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
		//System.out.println(danger());
		//action[Mario.KEY_JUMP] = danger(5) && (isMarioAbleToJump || !isMarioOnGround);
		//System.out.println("wall in way? " + wall_in_way + " gap? " + gap_in_way);

		//action[Mario.KEY_SPEED] = beast_near;
		//if (beast_near && isMarioAbleToShoot)
		action[Mario.KEY_SPEED] = isMarioAbleToShoot;

		if (beastBeside())
		{
				System.out.println("going left");
				action[Mario.KEY_RIGHT]=false;
		}
				action[Mario.KEY_LEFT]=true;
		else
		{
			action[Mario.KEY_LEFT]=false;
		}
			action[Mario.KEY_RIGHT] = true;
		if (beastBelow())
		{
			action[Mario.KEY_JUMP] = false;
			action[Mario.KEY_RIGHT] = false;
		}
		else
			action[Mario.KEY_SPEED] = action[Mario.KEY_JUMP] = (doJump()) && (isMarioAbleToJump || !isMarioOnGround);
        //printObservation();
		return action;
	}

	// Do the processing necessary to make decisions in getAction
	public void integrateObservation(Environment environment)
	{
		super.integrateObservation(environment);
    	levelScene = environment.getLevelSceneObservationZ(2);
	}

	// Clear out old actions by creating a new action array
	public void reset()
	{
		action = new boolean[Environment.numberOfKeys];
	}
}
