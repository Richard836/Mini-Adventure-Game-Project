import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.*;

public class MiniGame {

	int MAXLEVEL = 10;
	int ItemsBought = 0;
	int RestSite = 0;
	int PlayerLevel = 1;
	int PlayerEXP = 0;
	int PlayerGold = 0;
	int UNIVERSAL_CONDITION = 0;
	int TimesBrokeLimit = 1;
	
	double PlayerHealth = PlayerLevel*20;
	double EnemyHealth;
	
	boolean cool1 = false, cool2 = false;
	DecimalFormat df = new DecimalFormat("#.##");;
	
	String[][] PlayerMoveSet = { {"1. Punch,  Damage: ", "5"}, {"2. Kamehameha,  Damage: ", "20"} };
	String[][] EnemyMoveSet = { {"1. Knockout,  Damage: ", "7"}, {"2. SuperBeam,  Damage: ", "10"} };
	String[][] StoreItems = { {"Small Health Potion", "30", "60"}, {"Medium Health Potion", "70", "150"}, {"Large Health Potion", "150", "350"} };  //NAME, EFFECT, COST
	String[][] PlayerItems = new String[this.ItemsBought][2];
	
	static File audioFile = new File("EncounterSound.wav");
	
	public void TheGame() throws InputMismatchException, InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException {
		Scanner input = new Scanner(System.in);
		Random rng= new Random();
		String option;
		
		if(UNIVERSAL_CONDITION == 0) {
			System.out.println("\nIn this game you have a level and a moveset. To increase your level and ability damage defeat enemies (Max level = 10).\n"
					+ "The stronger the enemy the more experience you recieve to level up. Enemy encounters are random, have random levels and come with\n"
					+ "their own moveset. The higher the enemy level the more damage they do so BE CAREFUL! If an enemy proves to be too strong you can flee\n"
					+ "the fight at any time. You and the enemy have resistances however the difference between you and the enemy is that you have a base\n"
					+ "resistance state starting at 1% which will increase by 1% per level therefore making you tougher. You can check your current status by\n"
					+ "typing and entering 'status'. Every 3 fights you will enter a rest site and recieve a full heal. If you level up then you will also be full healed.\n"
					+ "Unless you are in a fight you may exit the game at anytime. To exit type and enter 'exit'. To get into an encounter/play the game type anything in the console. \n\n"
					+"(NOTE: Positive resistance = whoever is attacking will do less damage to the person that has a positive resistance\nNegative resistance = whoever is attacking will do more damage to a person that has a negative resistance)\n");
					System.out.println("Chance to encounter an enemy = 50%\n");
		}
		
		else if(UNIVERSAL_CONDITION == 1) {
			System.out.println("\nPlease type anything into the console to walk until you encounter an enemy!\n");
			this.UNIVERSAL_CONDITION = 2; 
		}
		
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if(this.RestSite == 3) {    	//IF THE PLAYER ENTERS A REST SITE AFTER 3 FIGHTS
			TimeUnit.SECONDS.sleep(1);
			System.out.println("\nYou have entered a rest site. Would you like to visit the store? Enter 'y' to shop or 'n' to skip!\n");
			RestSite();
		}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		this.cool1 = false; cool2 = false;   //makes sure cooldowns are reset
		this.UNIVERSAL_CONDITION = 1;
		option = input.next();
		
		if(option.contentEquals("exit")) {
			System.out.println("Good Bye!!");
			System.exit(0);
		}
			
		if(option.contentEquals("status")) {
			CheckStatus();
		}
			
		if(!option.contentEquals("status")) {
			int number = (int)rng.nextInt(10) + 1; //generates a random number between 1-10

			if(number <= 5) {
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
				AudioFormat format = audioStream.getFormat();
				
				DataLine.Info info = new DataLine.Info(Clip.class, format);
				Clip audioClip = (Clip) AudioSystem.getLine(info);
				audioClip.open(audioStream);
				audioClip.start();
				Encounter();
			}
			else {
				System.out.println("you walked and found no enemies\n");
				this.UNIVERSAL_CONDITION = 2;
				TheGame();
			}
		}
	}
	
	
	//This method handles encounters with enemies
	public void Encounter() throws InputMismatchException, InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException{
		Random rng = new Random();
		
		int max = this.PlayerLevel + 3, min = this.PlayerLevel - 3;
		int EnemyLevel = rng.nextInt((max-min) + 1) + min;  //number between 
		
		if(this.PlayerLevel == 1) {		//if player level is 1 special condition
			EnemyLevel = rng.nextInt(2) + 1;
		}
		else if(this.PlayerLevel < 4 && this.PlayerLevel > 1) { //if player level is 2 or 3 special condition
			int smallmax = this.PlayerLevel + 1, smallmin = this.PlayerLevel - 1;
			EnemyLevel = rng.nextInt((smallmax-smallmin) + 1) + smallmin;
		}
		
		double EnemyResistance = EnemyResistance();
		double PlayerResistance = PlayerResistance();

		this.EnemyHealth = 20 * EnemyLevel;
		this.cool1 = false; cool2 = false;   //makes sure cooldowns are reset

		System.out.println("\n\nYou have encountered an enemy! Enter 1 or 2 for a move and 3 to use items. To flee enter 4: (Fights until rest site = " + (3-this.RestSite) + ")");
		System.out.println("------------------------------------------------------------------------------------------------------------------");
		System.out.println("Your level: " + PlayerLevel + "\tYour Health: " + df.format(this.PlayerHealth) + "\t\tPlayers Resistance to enemy: " + df.format(PlayerResistance*100) + "\n");
		for(int i = 0; i < PlayerMoveSet.length; i++) {
			System.out.println(PlayerMoveSet[i][0] + Integer.parseInt(PlayerMoveSet[i][1])*PlayerLevel);
		}

		System.out.println("\n\nEnemy level: " + EnemyLevel + "\t  Enemy Health: " + df.format(this.EnemyHealth) + "\t\tEnemies Resistance to you: " + df.format(EnemyResistance*100) + "\n");
		for(int i = 0; i < EnemyMoveSet.length; i++) {
			System.out.println(EnemyMoveSet[i][0] + Integer.parseInt(EnemyMoveSet[i][1])*EnemyLevel);
		}

		this.cool1 = false; cool2 = false;   //makes sure cooldowns are reset
		MoveChoice(rng, EnemyLevel, EnemyResistance, PlayerResistance);  //CALL THE MOVE CHOICE METHOD
	}
	
	
	//This method handles the move selection of players and enemies, damage, health/gold gain and loss, and what dictates the end of a fight
	public void MoveChoice(Random rng, int enemylevel, double EnemyResistance, double PlayerResistance) throws InputMismatchException, InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException {
		Scanner input = new Scanner(System.in);
		int answer, Enemyanswer;
		
		try {
			TimeUnit.SECONDS.sleep(1);
			answer = input.nextInt();
			System.out.println();
			Enemyanswer = rng.nextInt(2);
			String[] enemymove = {"Knockout", "Superbeam"};
			String[] playermove = {"punch", "kamehameha"};
			TimeUnit.SECONDS.sleep(1);
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
			if(answer == 1 && this.cool1 == false) {
				
				this.cool1 = true;
				this.cool2 = false;
				
				if(EnemyDodgeChance() == 1) {
					System.out.println("You used: '" + playermove[answer-1] + "'. The enemy dodged the attack");
					TimeUnit.SECONDS.sleep(1);
				}
				else { 
					if(PlayerCritChance() <= 3) {
						System.out.println("You used: '" + playermove[answer-1] + "' it dealt: " + df.format((((Integer.parseInt(PlayerMoveSet[answer-1][1])*this.PlayerLevel) - ((Integer.parseInt(PlayerMoveSet[answer-1][1])*this.PlayerLevel)*EnemyResistance))*1.5)) + " damage.\n It was a critical hit!!");
						this.EnemyHealth -= (((Integer.parseInt(PlayerMoveSet[answer-1][1])*this.PlayerLevel) - ((Integer.parseInt(PlayerMoveSet[answer-1][1])*this.PlayerLevel)*EnemyResistance))*1.5);
					}
					else {
						System.out.println("You used: '" + playermove[answer-1] + "' it dealt: " + ((Integer.parseInt(PlayerMoveSet[answer-1][1])*this.PlayerLevel) - ((Integer.parseInt(PlayerMoveSet[answer-1][1])*this.PlayerLevel)*EnemyResistance)) + " damage\n");
						this.EnemyHealth -= ((Integer.parseInt(PlayerMoveSet[answer-1][1])*this.PlayerLevel) - ((Integer.parseInt(PlayerMoveSet[answer-1][1])*this.PlayerLevel)*EnemyResistance));
					}
					TimeUnit.SECONDS.sleep(1);
				}
				
			///////////////////////////////////////////IF THE ENEMY DIES/////////////////////////////////////////////////////////////	
				if(this.EnemyHealth <= 0) {     //CHECKS TO SEE IF ENEMY DIED SINCE PLAYER GOES FIRST
					
					if (this.PlayerLevel < this.MAXLEVEL) {	//check to see for max level OR Broken your limit
						this.PlayerEXP += 20*enemylevel;
					}
					
					this.cool1 = false;
					this.UNIVERSAL_CONDITION = 1;
					this.RestSite += 1;
					this.PlayerGold = this.PlayerGold + (20*enemylevel);
					System.out.println("Congratulations you defeated the level: " + enemylevel + " enemy!!\tYou gained: " + (20*enemylevel) + " exp and " + (20*enemylevel) + " gold\n");
					TimeUnit.SECONDS.sleep(1);
					
					if(this.PlayerLevel < this.MAXLEVEL && this.PlayerEXP >= 70*this.PlayerLevel) {
						this.PlayerEXP = this.PlayerEXP - (50*this.PlayerLevel);
						this.PlayerLevel = this.PlayerLevel + 1;
						this.PlayerHealth = 20 * this.PlayerLevel;
						this.RestSite = 0;
						this.PlayerGold = this.PlayerGold + 50;
						System.out.println("Congratulations you leveled up!! Now you are level: " + PlayerLevel + ", and you gained 50 gold for leveling up\n");
						TimeUnit.SECONDS.sleep(1);
					}
					TheGame();
				}
			/////////////////////////////////////////////////DOING DAMAGE TO ENEMY///////////////////////////////////////////////////////////////	
				
				if(PlayerDodgeChance() == 1) {
					System.out.println("The enemy used: '" + enemymove[Enemyanswer] + "'. You dodged the attack");
					TimeUnit.SECONDS.sleep(1);
				}
				else {	
					if(EnemyCritChance() < 3) {
						System.out.println("The enemy used: '" + enemymove[Enemyanswer] + "' it dealt: " + df.format((((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel) - ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel)*PlayerResistance))*1.5)) + " damage.\n It was a critical hit!!\n");
						this.PlayerHealth -= (((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel) - ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel)*PlayerResistance))*1.5); 
					}
					else {
						System.out.println("The enemy used: '" + enemymove[Enemyanswer] + "' it dealt: " + ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel) - ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel)*PlayerResistance)) + " damage\n");
						this.PlayerHealth -= ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel) - ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel)*PlayerResistance)); 
					}
					TimeUnit.SECONDS.sleep(1);
				}
				
			/////////////////////////////////////////////////IF THE PLAYER DIES/////////////////////////////////////////////////////////////////
				if(this.PlayerHealth <= 0) {
					System.out.println("You have died. Your level will remain but you lost 75% of your gold and all your exp gained so far");
					this.PlayerEXP = 0;
					this.cool1 = false;
					this.PlayerHealth = 20 * this.PlayerLevel;
					this.UNIVERSAL_CONDITION = 1;
					this.PlayerGold = (int)(this.PlayerGold * 0.25);
					TimeUnit.SECONDS.sleep(1);
					TheGame();
				}
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				System.out.println("\n(Fights until rest site = " + (3-this.RestSite) + ")");
				System.out.println("------------------------------------------------------------------------------------------------------------------");
				System.out.println("\nYour level: " + PlayerLevel + "\tYour Health: " + df.format(this.PlayerHealth) + "\t\tPlayers Resistance to enemy: " + df.format(PlayerResistance*100) + "\n");
				for(int i = 0; i < PlayerMoveSet.length; i++) {
					System.out.println(PlayerMoveSet[i][0] + Integer.parseInt(PlayerMoveSet[i][1])*PlayerLevel);
				}
				
				
				System.out.println("\n\nEnemy level: " + enemylevel + "\t  Enemy Health: " + df.format(this.EnemyHealth) + "\tEnemies Resistance to you: " + df.format(EnemyResistance*100) + "\n");
				for(int i = 0; i < EnemyMoveSet.length; i++) {
					System.out.println(EnemyMoveSet[i][0] + Integer.parseInt(EnemyMoveSet[i][1])*enemylevel);
				}
				MoveChoice(rng, enemylevel, EnemyResistance, PlayerResistance);
			}
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------		
			else if(answer == 1 && cool1 == true) {
				System.out.println("Sorry ability 1 is on cooldown. Please choose another one.\n");
				TimeUnit.SECONDS.sleep(1);
				this.UNIVERSAL_CONDITION = 1;
				MoveChoice(rng, enemylevel, EnemyResistance, PlayerResistance);
			}
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
			else if(answer == 2 && this.cool2 == false) {
				
				this.cool2 = true;
				this.cool1 = false;
				
				if(EnemyDodgeChance() == 1) {
					System.out.println("You used: '" + playermove[answer-1] + "'. The enemy dodged the attack");
					TimeUnit.SECONDS.sleep(1);
				}
				else { 
					if(PlayerCritChance() <= 3) {
						System.out.println("You used: '" + playermove[answer-1] + "' it dealt: " + df.format((((Integer.parseInt(PlayerMoveSet[answer-1][1])*this.PlayerLevel) - ((Integer.parseInt(PlayerMoveSet[answer-1][1])*this.PlayerLevel)*EnemyResistance))*1.5)) + " damage.\n It was a critical hit!!");
						this.EnemyHealth -= (((Integer.parseInt(PlayerMoveSet[answer-1][1])*this.PlayerLevel) - ((Integer.parseInt(PlayerMoveSet[answer-1][1])*this.PlayerLevel)*EnemyResistance))*1.5);
					}
					else {
						System.out.println("You used: '" + playermove[answer-1] + "' it dealt: " + ((Integer.parseInt(PlayerMoveSet[answer-1][1])*this.PlayerLevel) - ((Integer.parseInt(PlayerMoveSet[answer-1][1])*this.PlayerLevel)*EnemyResistance)) + " damage\n");
						this.EnemyHealth -= ((Integer.parseInt(PlayerMoveSet[answer-1][1])*this.PlayerLevel) - ((Integer.parseInt(PlayerMoveSet[answer-1][1])*this.PlayerLevel)*EnemyResistance));
					}
					TimeUnit.SECONDS.sleep(1);
				}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				if(this.EnemyHealth <= 0) {     //CHECKS TO SEE IF ENEMY DIED SINCE PLAYER GOES FIRST
					
					if (this.PlayerLevel < this.MAXLEVEL) {	//check to see for max level OR Broken your limit
						this.PlayerEXP += 20*enemylevel;
					}
					
					this.UNIVERSAL_CONDITION = 1;
					this.cool2 = false;
					this.RestSite += 1;
					this.PlayerGold = this.PlayerGold + (20*enemylevel);
					System.out.println("Congratulations you defeated the level: " + enemylevel + " enemy!!\tYou gained: " + (20*enemylevel) + " exp and " + (20*enemylevel) + " gold\n");
					TimeUnit.SECONDS.sleep(1);
					
					if(this.PlayerLevel < this.MAXLEVEL && this.PlayerEXP >= (70*this.PlayerLevel)) {
						this.PlayerEXP = this.PlayerEXP - (50*this.PlayerLevel);
						this.PlayerLevel = this.PlayerLevel + 1;
						this.PlayerHealth = 20 * this.PlayerLevel;
						this.RestSite = 0;
						this.PlayerGold = this.PlayerGold + 50;
						System.out.println("Congratulations you leveled up!! Now you are level: " + PlayerLevel + "\n");
						TimeUnit.SECONDS.sleep(1);
					}
					TheGame();
				}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				if(PlayerDodgeChance() == 1) {
					System.out.println("The enemy used: '" + enemymove[Enemyanswer] + "'. You dodged the attack");
					TimeUnit.SECONDS.sleep(1);
				}
				else {	
					if(EnemyCritChance() < 3) {
						System.out.println("The enemy used: '" + enemymove[Enemyanswer] + "' it dealt: " + df.format((((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel) - ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel)*PlayerResistance))*1.5)) + " damage.\n It was a critical hit!!\n");
						this.PlayerHealth -= (((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel) - ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel)*PlayerResistance))*1.5); 
					}
					else {
						System.out.println("The enemy used: '" + enemymove[Enemyanswer] + "' it dealt: " + ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel) - ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel)*PlayerResistance)) + " damage\n");
						this.PlayerHealth -= ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel) - ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel)*PlayerResistance)); 
					}
					TimeUnit.SECONDS.sleep(1);
				}	
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
				if(this.PlayerHealth <= 0) {
					System.out.println("You have died. Your level will remain but you will lose all your exp gained so far");
					this.PlayerEXP = 0;
					this.cool2 = false;
					this.PlayerHealth = 20 * this.PlayerLevel;
					this.UNIVERSAL_CONDITION = 1;
					this.PlayerGold = (int)(this.PlayerGold * 0.25);
					TimeUnit.SECONDS.sleep(1);
					TheGame();
				}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
				System.out.println("\n(Fights until rest site = " + (3-this.RestSite) + ")");
				System.out.println("------------------------------------------------------------------------------------------------------------------");
				System.out.println("\nYour level: " + PlayerLevel + "\tYour Health: " + df.format(this.PlayerHealth) + "\t\tPlayers Resistance to enemy: " + df.format(PlayerResistance*100) + "\n");
				for(int i = 0; i < PlayerMoveSet.length; i++) {
					System.out.println(PlayerMoveSet[i][0] + Integer.parseInt(PlayerMoveSet[i][1])*PlayerLevel);
				}
				
				
				System.out.println("\n\nEnemy level: " + enemylevel + "\t  Enemy Health: " + df.format(this.EnemyHealth) + "\tEnemies Resistance to you: " + df.format(EnemyResistance*100) + "\n");
				for(int i = 0; i < EnemyMoveSet.length; i++) {
					System.out.println(EnemyMoveSet[i][0] + Integer.parseInt(EnemyMoveSet[i][1])*enemylevel);
				}
				MoveChoice(rng, enemylevel, EnemyResistance, PlayerResistance);
			}
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
			else if(answer == 2 && this.cool2 == true) {
				System.out.println("Sorry ability 2 is on cooldown. Please choose another one.\n");
				this.UNIVERSAL_CONDITION = 1;
				TimeUnit.SECONDS.sleep(1);
				MoveChoice(rng, enemylevel, EnemyResistance, PlayerResistance);
			}
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
			else if(answer == 3) {      //for item use
				this.UNIVERSAL_CONDITION = 1;
				TimeUnit.SECONDS.sleep(1);
				ItemUsed(rng, enemylevel, EnemyResistance, PlayerResistance, enemymove, Enemyanswer);
			}
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------			
			else if(answer == 4) {   //FLEE THE BATTLE
				System.out.println("You fled the battle\n\n");
				TimeUnit.SECONDS.sleep(1);
				this.UNIVERSAL_CONDITION = 1;
				TheGame();
			}
			
			else { //if they enter a invalid option
				System.out.println("not an option enter from 1 to 4");
				TimeUnit.SECONDS.sleep(1);
				this.UNIVERSAL_CONDITION = 1;
				MoveChoice(rng, enemylevel, EnemyResistance, PlayerResistance);
			}
		}
		catch(InputMismatchException ex) {  //catch when they enter a letter or non-number
			System.out.println("Sorry this is not a number\n\n");
			TimeUnit.SECONDS.sleep(1);
			this.UNIVERSAL_CONDITION = 1;
			MoveChoice(rng, enemylevel, EnemyResistance, PlayerResistance);
		}
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void ItemUsed(Random rng, int enemylevel, double EnemyResistance, double PlayerResistance, String[] enemymove, int Enemyanswer) throws InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException {
		try {
			System.out.println("\nChoose the item you wish to use: (enter 0 to go back to move options)\n---------------------------------------------------");
			
			for(int i = 0; i < PlayerItems.length; i++) { //prints items for player to see what to choose
				System.out.println((i+1) + ". " + PlayerItems[i][0] + ", Effect = " + this.PlayerItems[i][1]);
			}
			
			Scanner input = new Scanner(System.in);
			int answer = input.nextInt(); //answer to see which item to use
			
			if(answer == 0) {   //cancel item use
				System.out.println("\nYou cancelled the action\n");
				System.out.println("\n(Fights until rest site = " + (3-this.RestSite) + ")");
				System.out.println("------------------------------------------------------------------------------------------------------------------");
				System.out.println("\nYour level: " + PlayerLevel + "\tYour Health: " + df.format(this.PlayerHealth) + "\t\tPlayers Resistance to enemy: " + df.format(PlayerResistance*100) + "\n");
				for(int i = 0; i < PlayerMoveSet.length; i++) {
					System.out.println(PlayerMoveSet[i][0] + Integer.parseInt(PlayerMoveSet[i][1])*PlayerLevel);
				}
				
				System.out.println("\n\nEnemy level: " + enemylevel + "\t  Enemy Health: " + df.format(this.EnemyHealth) + "\tEnemies Resistance to you: " + df.format(EnemyResistance*100) + "\n");
				for(int i = 0; i < EnemyMoveSet.length; i++) {
					System.out.println(EnemyMoveSet[i][0] + Integer.parseInt(EnemyMoveSet[i][1])*enemylevel);
				}
				System.out.println();
				MoveChoice(rng, enemylevel, EnemyResistance, PlayerResistance);  //goes back to original method
			}
			
			if(answer > this.PlayerItems.length) { //player chooses something that the they do not have
				System.out.println("\nSorry this is not an option\n");
				ItemUsed(rng, enemylevel, EnemyResistance, PlayerResistance, enemymove, Enemyanswer);
			}
			
			else {
				String[][] temp = this.PlayerItems.clone(); //store player items in temporary list
				this.PlayerHealth += Integer.parseInt(this.PlayerItems[answer-1][1]); //increase player health depending on item effect used
				System.out.println("\nPlayer is healed by = " + Integer.parseInt(this.PlayerItems[answer-1][1]) + "\n"); //player is healed information
				
		//////////////////////////////////USED UP YOUR TURN FOR ITEMS SO ENEMY ATTACKS//////////////////////////////////////////////////////////////////				
				if(PlayerDodgeChance() == 1) {
					System.out.println("The enemy used: '" + enemymove[Enemyanswer] + "'. You dodged the attack");
				}
				else {	
					if(EnemyCritChance() < 3) {
						System.out.println("The enemy used: '" + enemymove[Enemyanswer] + "' it dealt: " + df.format((((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel) - ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel)*PlayerResistance))*1.5)) + " damage.\n It was a critical hit!!\n");
						this.PlayerHealth -= (((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel) - ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel)*PlayerResistance))*1.5); 
					}
					else {
						System.out.println("The enemy used: '" + enemymove[Enemyanswer] + "' it dealt: " + ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel) - ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel)*PlayerResistance)) + " damage\n");
						this.PlayerHealth -= ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel) - ((Integer.parseInt(EnemyMoveSet[Enemyanswer][1])*enemylevel)*PlayerResistance)); 
					}
				}
				
				if(this.PlayerHealth <= 0) {  //checks if player dies
					System.out.println("You have died. Your level will remain but you will lose all your exp gained so far");
					this.PlayerEXP = 0;
					this.cool2 = false;
					this.cool1 = false;
					this.PlayerHealth = 20 * this.PlayerLevel;
					this.UNIVERSAL_CONDITION = 1;
					this.PlayerGold = (int)(this.PlayerGold * 0.25);
					TheGame();
				}
		//////////////////////////////////USED UP YOUR TURN FOR ITEMS SO ENEMY ATTACKS//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////Reprints the options for the fight////////////////////////////////////////////////////////////////////////////
				System.out.println("\nYour cooldowns are reset\n");
				System.out.println("\n(Fights until rest site = " + (3-this.RestSite) + ")");
				System.out.println("------------------------------------------------------------------------------------------------------------------");
				System.out.println("\nYour level: " + PlayerLevel + "\tYour Health: " + df.format(this.PlayerHealth) + "\t\tPlayers Resistance to enemy: " + df.format(PlayerResistance*100) + "\n");
				for(int i = 0; i < PlayerMoveSet.length; i++) {
					System.out.println(PlayerMoveSet[i][0] + Integer.parseInt(PlayerMoveSet[i][1])*PlayerLevel);
				}
				
				System.out.println("\n\nEnemy level: " + enemylevel + "\t  Enemy Health: " + df.format(this.EnemyHealth) + "\tEnemies Resistance to you: " + df.format(EnemyResistance*100) + "\n");
				for(int i = 0; i < EnemyMoveSet.length; i++) {
					System.out.println(EnemyMoveSet[i][0] + Integer.parseInt(EnemyMoveSet[i][1])*enemylevel);
				}
				System.out.println();
		//////////////////////////////////Reprints the options for the fight////////////////////////////////////////////////////////////////////////////
				
				this.ItemsBought -= 1;  //decrements items bought
				this.PlayerItems = new String[this.ItemsBought][2]; //decrease size of player item list
				
				for(int i = 0; i < temp.length; i++) {
					if(i < answer-1) {
						this.PlayerItems[i] = temp[i];  //add items bought back into the player item list before chosen removed item
					}
					else if(i > answer-1) {
						this.PlayerItems[i-1] = temp[i]; //add items bought back into player item list after chosen removed item
					}
				}
				this.cool2 = false;
				this.cool1 = false;
				MoveChoice(rng, enemylevel, EnemyResistance, PlayerResistance);
			}
		}
		catch(InputMismatchException ex) {
			System.out.println("\nThis answer is invalid please choose the number indicating the item\n");
			ItemUsed(rng, enemylevel, EnemyResistance, PlayerResistance, enemymove, Enemyanswer);
		}
	}
	
	
	
	//METHOD TO CHECK THE STATUS OF PLAYER
	public void CheckStatus() throws InputMismatchException, InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException {
		TimeUnit.SECONDS.sleep(1);
		
		System.out.println("\n\nYour level: " + PlayerLevel + "\tYour Health: " + df.format(this.PlayerHealth) + "\t\tCurrent EXP: " + PlayerEXP + "\t\tEXP to Level Up: " + 70*this.PlayerLevel + "\tFights until rest site: " + (3-this.RestSite));
		System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
		
		for(int i = 0; i < PlayerMoveSet.length; i++) {
			System.out.println(PlayerMoveSet[i][0] + Integer.parseInt(PlayerMoveSet[i][1])*PlayerLevel);
		}
		System.out.println("\n\nGold = " + this.PlayerGold + "\n");
		System.out.println("\nPlayer Items:\n---------------------------------------------------");
		
		for(int i = 0; i < PlayerItems.length; i++) {
			System.out.println((i+1) + ". " + PlayerItems[i][0] + ", Effect = " + this.PlayerItems[i][1]);
		}
		TheGame();
	}
	

	public double EnemyResistance(){               //ENEMY RESISTANCE
		Random randint = new Random();
		int resistanceSign = randint.nextInt(2);
		double enemyresistance = randint.nextInt(25);    		
		if(resistanceSign == 0) {
			return enemyresistance/100;
		}
		else {
			return (enemyresistance/100)*-1;
		}
	}
	
	public double PlayerResistance() {               //PLAYER RESISTANCE
		Random randint = new Random();
		double PlayerLevel = this.PlayerLevel;
		int resistanceSign = randint.nextInt(2);
		double playerResistance = randint.nextInt(15);   
		
		if(resistanceSign == 0) {
			return ((playerResistance/100) + (PlayerLevel/100));
		}
		else {
			return (((playerResistance/100)*-1) + (PlayerLevel/100));
		}
	}
	
	public int PlayerDodgeChance() {               //PLAYER DODGE CHANCE
		Random randint = new Random();
		int chance = randint.nextInt(20) + 1;
		return chance;
	}
	
	public int EnemyDodgeChance() {               //ENEMY DODGE CHANCE
		Random randint = new Random();
		int chance = randint.nextInt(20) + 1;
		return chance;
	}
	
	public int PlayerCritChance() {               //PLAYER CRIT CHANCE
		Random randint = new Random();
		int chance = randint.nextInt(40) + 1;
		return chance;
	}
	
	public int EnemyCritChance() {               //ENEMY CRIT CHANCE
		Random randint = new Random();
		int chance = randint.nextInt(40) + 1;
		return chance;
	}
	
	public void RestSite() throws InterruptedException {					//REST SITE 
		Scanner input = new Scanner(System.in);
		String answer = input.next();
		
		TimeUnit.SECONDS.sleep(1);
		
		if(answer.contentEquals("y")) {
			Shop(); //take them to the shop
		}
		else if(answer.contentEquals("n")) {
			System.out.println("\nYou skipped the rest site\n");
		}
		else {
			System.out.println("\nSorry this is not an option.\n");
			RestSite(); //re-run the method
		}
		
		this.PlayerHealth = 20 * this.PlayerLevel;
		this.RestSite = 0;
		System.out.println("Player health has been fully restored. RestSite counter has been reset\n\t\tHealth = " + this.PlayerHealth + ",		restsite = " + this.RestSite);
		System.out.println("\nPlease type anything into the console to walk until you encounter an enemy!\n");
	}
	
	public void Shop() throws InputMismatchException, InterruptedException{					//PLAYER SHOP
		Scanner in = new Scanner(System.in);
		int answer;
		TimeUnit.SECONDS.sleep(1);
		
		System.out.println("\nHello! Welcome to the shop. Below are the following items for sale: By as many items as you wish (To leave the store enter 0)");
		System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
		System.out.println("\nYour Gold = " + this.PlayerGold + "\n");
		
		if (this.PlayerLevel == 10 && this.PlayerGold >= 1000) {
			//future stuff
		}
		
		for(int i = 0; i < StoreItems.length; i++) {
			System.out.print((i+1) + ". " + StoreItems[i][0]);
			System.out.print("\t\tEffect = " + StoreItems[i][1]);
			System.out.print("\t\tCost = " + StoreItems[i][2] + "\n");
		}
		
		try {
			System.out.println(); answer = in.nextInt(); 
			if(answer != 0 && answer <= StoreItems.length && this.PlayerGold >= Integer.parseInt(StoreItems[answer-1][2])) {

				String[][] temp = this.PlayerItems.clone(); //store player items in temporary list
				this.ItemsBought += 1;  //increments current items bought
				this.PlayerItems = new String[this.ItemsBought][2]; //increase size of player item list
				
				for(int i = 0; i < temp.length; i++) {
					this.PlayerItems[i] = temp[i];  //add the items bought back into the player item list
				}
				
				this.PlayerItems[this.ItemsBought-1] = this.StoreItems[answer-1];  //add in the recently bought item
				this.PlayerGold = this.PlayerGold - Integer.parseInt(StoreItems[answer-1][2]);  //decrease gold
				
				System.out.println("\nYou bought: " + this.PlayerItems[this.ItemsBought-1][0] + ",	You spent = " + this.StoreItems[answer-1][2] + " gold\n");
				Shop(); //re-run the method
			}
			
			else if(answer != 0 && answer <= StoreItems.length && this.PlayerGold < Integer.parseInt(StoreItems[answer-1][2])) {
				System.out.println("\nSorry You do not have enough gold.");
				Shop(); //when you don't have enough gold re-run the
			}
			
			else if(answer == 0) {
				//DO NOTHING
			}
			
			else {
				System.out.println("\nSorry this is not an option");
				Shop();
			}
		}
		catch(InputMismatchException ex) {
			System.out.println("\nSorry this is not a number");
			Shop();
		}
	}
	
	public static void main(String[] args) throws InputMismatchException, InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException {
		
		MiniGame callgame = new MiniGame();
		callgame.TheGame(); //Runs the game
	}
}

