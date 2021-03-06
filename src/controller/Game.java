package controller;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sound.sampled.Clip;

import game.model.*;
import game.view.*;
import sounds.Sound;

// ===============================================
// == This Game class is the CONTROLLER
// ===============================================

public class Game implements Runnable, KeyListener {

	// ===============================================
	// FIELDS
	// ===============================================

	public static final Dimension DIM = new Dimension(1300, 800); //the dimension of the game.
	private GamePanel gmpPanel;
	public static Random R = new Random();
	public final static int ANI_DELAY = 45; // milliseconds between screen
											// updates (animation)
	private Thread thrAnim;
    private Thread thrSec;
	private int nLevel = 1;
	private int nTick = 0;
	private ArrayList<Tuple> tupMarkForRemovals;
	private ArrayList<Tuple> tupMarkForAdds;
	private boolean bMuted = true;
	

	private final int PAUSE = 80, // p key
			QUIT = 81, // q key


			LEFT1 = 37, // rotate left; left arrow
			RIGHT1 = 39, // rotate right; right arrow
			UP1 = 38, // thrust; up arrow
            DOWN1 = 40,

			BEGIN = 66, // B key
			FIRE1 = 32, // space key
            FIRE2 = 84 , // T
			MUTE = 77, // m-key mute

            UP2 = 87, //W
            DOWN2 = 83, //S
            LEFT2 = 65, //A
            RIGHT2 = 68, //D
	// for possible future use
	         SHIELD1 = 73,  // I key
             SHIELD2 = 90, //Z KEY
	// NUM_ENTER = 10, 				// hyp
          SPECIAL1 = 70, //F,
	      SPECIAL2   = 74,// fire special weapon;  J key
          GROUPBULLET1 = 71, 					// fire group weapon;  G key
          GROUPBULLET2 = 75,
    DOUBLE = 86; //V

	private Clip clpThrust;
	private Clip clpMusicBackground;

	private static final int SPAWN_NEW_SHIP_FLOATER = 1200;



	// ===============================================
	// ==CONSTRUCTOR
	// ===============================================

	public Game() {

		gmpPanel = new GamePanel(DIM);
		gmpPanel.addKeyListener(this);

		clpThrust = Sound.clipForLoopFactory("whitenoise.wav");
		clpMusicBackground = Sound.clipForLoopFactory("music-background.wav");
	

	}

	// ===============================================
	// ==METHODS
	// ===============================================

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() { // uses the Event dispatch thread from Java 5 (refactored)
					public void run() {
						try {
							Game game = new Game(); // construct itself
							game.fireUpAnimThread();
                            game.fireUPSecondThread();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void fireUpAnimThread() { // called initially
		if (thrAnim == null) {
			thrAnim = new Thread(this); // pass the thread a runnable object (this)
			thrAnim.start();
		}
	}
    private void fireUPSecondThread()
    {
        if (thrSec == null){
            thrSec = new Thread(this);
            thrSec.start();
        }
    }

	// implements runnable - must have run method
	public void run() {

		// lower this thread's priority; let the "main" aka 'Event Dispatch'
		// thread do what it needs to do first
		thrAnim.setPriority(Thread.MIN_PRIORITY);
        thrSec.setPriority(Thread.MIN_PRIORITY);

		// and get the current time
		long lStartTime = System.currentTimeMillis();

		// this thread animates the scene
		while (Thread.currentThread() == thrAnim ) {
			tick();
			spawnNewShipFloater();
            spawnNewShield();
            spawnNewUFO();
            //spawnEnemyBullet();
			gmpPanel.update(gmpPanel.getGraphics()); // update takes the graphics context we must 
														// surround the sleep() in a try/catch block
														// this simply controls delay time between 
														// the frames of the animation

			//this might be a good place to check for collisions
			checkCollisions();
			//this might be a god place to check if the level is clear (no more foes)
			//if the level is clear then spawn some big asteroids -- the number of asteroids 
			//should increase with the level. 
			checkNewLevel();

			try {
				// The total amount of time is guaranteed to be at least ANI_DELAY long.  If processing (update) 
				// between frames takes longer than ANI_DELAY, then the difference between lStartTime - 
				// System.currentTimeMillis() will be negative, then zero will be the sleep time
				lStartTime += ANI_DELAY;
				Thread.sleep(Math.max(0,
						lStartTime - System.currentTimeMillis()));
			} catch (InterruptedException e) {
				// just skip this frame -- no big deal
				continue;
			}
		} // end while
	} // end run

	private void checkCollisions() {

		
		//@formatter:off
		//for each friend in movFriends
			//for each foe in movFoes
				//if the distance between the two centers is less than the sum of their radii
					//mark it for removal
		
		//for each mark-for-removal
			//remove it
		//for each mark-for-add
			//add it
		//@formatter:on
		
		//we use this ArrayList to keep pairs of movMovables/movTarget for either
		//removal or insertion into our arrayLists later on
		tupMarkForRemovals = new ArrayList<Tuple>();
		tupMarkForAdds = new ArrayList<Tuple>();

		Point pntFriendCenter, pntFoeCenter;
		int nFriendRadiux, nFoeRadiux;

		for (Movable movFriend : CommandCenter.movFriends) {
			for (Movable movFoe : CommandCenter.movFoes) {

				pntFriendCenter = movFriend.getCenter();
				pntFoeCenter = movFoe.getCenter();
				nFriendRadiux = movFriend.getRadius();
				nFoeRadiux = movFoe.getRadius();

				//detect collision
				if (pntFriendCenter.distance(pntFoeCenter) < (nFriendRadiux + nFoeRadiux)) {

					//falcon hits the enemy
					if ((movFriend instanceof Falcon )) {

                        System.out.println(((Falcon) movFriend).getName());
                        if (!CommandCenter.getFalcon1().getProtected() && ((Falcon) movFriend).getName().equals("player1")) {

                            if (((Falcon) movFriend).getShield() > 0) {
                                ((Falcon) movFriend).setShield(((Falcon) movFriend).getShield() - 1);

                                initiateDebris((Sprite) movFoe, tupMarkForAdds);
                                killFoe(movFoe);
                            } else {

                                tupMarkForRemovals.add(new Tuple(CommandCenter.movFriends, movFriend));
                                if (((Falcon) movFriend).getName().equals("player1")) {
                                    CommandCenter.spawnFalcon1(false);
                                }


                                initiateDebris((Sprite) movFriend, tupMarkForAdds);
                                initiateDebris((Sprite) movFoe, tupMarkForAdds);
                                killFoe(movFoe);
                            }

                            //System.out.printf("the shield value is %d\n", ((Falcon) movFriend).getShield());
                            Sound.playSound("shipExplode.WAV");
                        }

                        if (CommandCenter.getFalcon2()!=null)
                        {
                        if (!CommandCenter.getFalcon2().getProtected() && ((Falcon) movFriend).getName().equals("player2")) {

                            if (((Falcon) movFriend).getShield() > 0) {
                                ((Falcon) movFriend).setShield(((Falcon) movFriend).getShield() - 1);

                                initiateDebris((Sprite) movFoe, tupMarkForAdds);
                                killFoe(movFoe);
                            } else {

                                tupMarkForRemovals.add(new Tuple(CommandCenter.movFriends, movFriend));
                                if (((Falcon) movFriend).getName().equals("player2")) {
                                    CommandCenter.spawnFalcon2(false);
                                }

                                initiateDebris((Sprite) movFriend, tupMarkForAdds);
                                initiateDebris((Sprite) movFoe, tupMarkForAdds);
                                killFoe(movFoe);
                            }

                            //System.out.printf("the shield value is %d\n", ((Falcon) movFriend).getShield());
                            Sound.playSound("shipExplode.WAV");
                        }
                    }
					}
					//cruise hits the enemy
                    else if (movFriend instanceof Cruise)
                    {
                        killFoe(movFoe);
                        if (((Cruise) movFriend).getFalcon().getName().equals("player1")) {
                            CommandCenter.setScore1(CommandCenter.getScore() + 10);
                        }
                        if (((Cruise) movFriend).getFalcon().getName().equals("player2")) {
                            CommandCenter.setScore2(CommandCenter.getScore2() + 10);
                        }
                        CommandCenter.movDebris.add(new Explosion(movFoe,10,10));
                        initiateDebris((Sprite) movFoe,tupMarkForAdds);
                        Sound.playSound("explode.wav");
                    }
					else if (movFriend instanceof Bullet ){//the bullet hits the enemy
						tupMarkForRemovals.add(new Tuple(CommandCenter.movFriends, movFriend));
						killFoe(movFoe);
                        if (((Bullet) movFriend).getFalcon().getName().equals("player1")){
                            CommandCenter.setScore1(CommandCenter.getScore() + 10);
                        }
                        if (((Bullet) movFriend).getFalcon().getName().equals("player2")) {
                            CommandCenter.setScore2(CommandCenter.getScore2() + 10);
                        }
                        CommandCenter.movDebris.add(new Explosion(movFoe,10,10));
                        initiateDebris((Sprite) movFoe,tupMarkForAdds);
                        Sound.playSound("explode.wav");
                        //add the score
					}//end else
                    else if (movFriend instanceof specialBullet ){//the bullet hits the enemy
                        tupMarkForRemovals.add(new Tuple(CommandCenter.movFriends, movFriend));
                        killFoe(movFoe);
                        if (((specialBullet) movFriend).getFalcon().getName().equals("player1")){
                            CommandCenter.setScore1(CommandCenter.getScore() + 10);
                        }
                        if (((specialBullet) movFriend).getFalcon().getName().equals("player2")) {
                            CommandCenter.setScore2(CommandCenter.getScore2() + 10);
                        }
                        CommandCenter.movDebris.add(new Explosion(movFoe,10,10));
                        initiateDebris((Sprite) movFoe,tupMarkForAdds);
                        Sound.playSound("explode.wav");
                        //add the score
                    }//end else

					//explode/remove foe


				
				}//end if 
			}//end inner for
		}//end outer for


		//check for collisions between falcon and floaters
		if (CommandCenter.getFalcon1() != null){
			Point pntFalCenter = CommandCenter.getFalcon1().getCenter();
			int nFalRadiux = CommandCenter.getFalcon1().getRadius();
			Point pntFloaterCenter;
			int nFloaterRadiux;
			
			for (Movable movFloater : CommandCenter.movFloaters) {
				pntFloaterCenter = movFloater.getCenter();
				nFloaterRadiux = movFloater.getRadius();
	
				//detect collision
				if (pntFalCenter.distance(pntFloaterCenter) < (nFalRadiux + nFloaterRadiux)) {
	
					
					tupMarkForRemovals.add(new Tuple(CommandCenter.movFloaters, movFloater));
                    CommandCenter.setNumFalcons1(CommandCenter.getNumFalcons1() + 1);
					Sound.playSound("pacman_eatghost.wav");
	
				}//end if 
			}//end inner for
		}//end if not null

        if (CommandCenter.getFalcon2() != null){
            Point pntFalCenter = CommandCenter.getFalcon2().getCenter();
            int nFalRadiux = CommandCenter.getFalcon2().getRadius();
            Point pntFloaterCenter;
            int nFloaterRadiux;

            for (Movable movFloater : CommandCenter.movFloaters) {
                pntFloaterCenter = movFloater.getCenter();
                nFloaterRadiux = movFloater.getRadius();

                //detect collision
                if (pntFalCenter.distance(pntFloaterCenter) < (nFalRadiux + nFloaterRadiux)) {


                    tupMarkForRemovals.add(new Tuple(CommandCenter.movFloaters, movFloater));
                    CommandCenter.setNumFalcons2(CommandCenter.getNumFalcons2() + 1);
                    Sound.playSound("pacman_eatghost.wav");

                }//end if
            }//end inner for
        }//end if not null





        if (CommandCenter.getFalcon1() != null){
            Point pntFalCenter = CommandCenter.getFalcon1().getCenter();
            int nFalRadiux = CommandCenter.getFalcon1().getRadius();
            Point pntShieldCenter;
            int nShieldRadiux;

            for (Movable movShield : CommandCenter.movShield) {
                pntShieldCenter = movShield.getCenter();
                nShieldRadiux = movShield.getRadius();

                //detect collision
                if (pntFalCenter.distance(pntShieldCenter) < (nFalRadiux + nShieldRadiux)) {


                    tupMarkForRemovals.add(new Tuple(CommandCenter.movShield, movShield));
                    CommandCenter.getFalcon1().setShield(3);
                    Sound.playSound("shieldup.wav");

                }//end if
            }//end inner for
        }//end if not null


        if (CommandCenter.getFalcon2() != null){
            Point pntFalCenter = CommandCenter.getFalcon2().getCenter();
            int nFalRadiux = CommandCenter.getFalcon2().getRadius();
            Point pntShieldCenter;
            int nShieldRadiux;

            for (Movable movShield : CommandCenter.movShield) {
                pntShieldCenter = movShield.getCenter();
                nShieldRadiux = movShield.getRadius();

                //detect collision
                if (pntFalCenter.distance(pntShieldCenter) < (nFalRadiux + nShieldRadiux)) {


                    tupMarkForRemovals.add(new Tuple(CommandCenter.movShield, movShield));
                    CommandCenter.getFalcon2().setShield(3);
                    Sound.playSound("shieldup.wav");

                }//end if
            }//end inner for
        }//end if not null







		
		//remove these objects from their appropriate ArrayLists
		//this happens after the above iterations are done
		for (Tuple tup : tupMarkForRemovals) 
			tup.removeMovable();
		
		//add these objects to their appropriate ArrayLists
		//this happens after the above iterations are done
		for (Tuple tup : tupMarkForAdds) 
			tup.addMovable();

		//call garbage collection
		System.gc();
		
	}//end meth


    public void initiateDebris(Sprite sprite, ArrayList tupleList)
    {
       Point[] array =  sprite.getObjectPoints();

       for (int count=0;count<array.length-1;count++)
       {
           Debris debris = new Debris(sprite, array[count],array[count+1]);
           tupleList.add(new Tuple(CommandCenter.movDebris, debris));

       }
        Debris debris = new Debris(sprite, array[0],array[array.length-1]);
        tupleList.add(new Tuple(CommandCenter.movDebris, debris));

    }

	private void killFoe(Movable movFoe) {
		
		if (movFoe instanceof Asteroid){

			//we know this is an Asteroid, so we can cast without threat of ClassCastException
			Asteroid astExploded = (Asteroid)movFoe;
			//big asteroid 
			if(astExploded.getSize() == 0){
				//spawn two medium Asteroids
				tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new Asteroid(astExploded)));
				tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new Asteroid(astExploded)));
				
			} 
			//medium size aseroid exploded
			else if(astExploded.getSize() == 1){
				//spawn three small Asteroids
				tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new Asteroid(astExploded)));
				tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new Asteroid(astExploded)));

			}
			//remove the original Foe	
			tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
		
			
		} 
		//not an asteroid
		else {
			//remove the original Foe
			tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
		}
		
		
		

		
		
		
		
	}

	//some methods for timing events in the game,
	//such as the appearance of UFOs, floaters (power-ups), etc. 
	public void tick() {
		if (nTick == Integer.MAX_VALUE)
			nTick = 0;
		else
			nTick++;
	}

	public int getTick() {
		return nTick;
	}

	private void spawnNewShipFloater() {

		if (nTick % (SPAWN_NEW_SHIP_FLOATER - nLevel * 400) == 0) {
			CommandCenter.movFloaters.add(new NewShipFloater());
		}
	}



    private void spawnNewShield() {

        if (nTick % (SPAWN_NEW_SHIP_FLOATER - nLevel * 500) == 0) {
            CommandCenter.movShield.add(new newShield());
        }
    }

    private void spawnNewUFO() {

        if (nTick % (SPAWN_NEW_SHIP_FLOATER - nLevel * 600) == 0) {
            CommandCenter.movFoes.add(new UFO());
        }
    }

	// Called when user presses 's'
	private void startGame(int number) {
		CommandCenter.clearAll();
		CommandCenter.initGame(number);
		CommandCenter.setLevel(0);
		CommandCenter.setPlaying(true);
		CommandCenter.setPaused(false);
		//if (!bMuted)
		   // clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
	}

	//this method spawns new asteroids
	private void spawnAsteroids(int nNum) {
		for (int nC = 0; nC < nNum; nC++) {
			//Asteroids with size of zero are big
			CommandCenter.movFoes.add(new Asteroid(0));
		}
	}
	
	
	private boolean isLevelClear(){
		//if there are no more Asteroids on the screen
		
		boolean bAsteroidFree = true;
		for (Movable movFoe : CommandCenter.movFoes) {
			if (movFoe instanceof Asteroid){
				bAsteroidFree = false;
				break;
			}
		}
		
		return bAsteroidFree;

		
	}
	
	private void checkNewLevel(){
		
		if (isLevelClear() ){
			if (CommandCenter.getFalcon1() !=null)
				CommandCenter.getFalcon1().setProtected(true);
			
			spawnAsteroids(CommandCenter.getLevel() + 2);
			CommandCenter.setLevel(CommandCenter.getLevel() + 1);

		}
	}
	
	
	

	// Varargs for stopping looping-music-clips
	private static void stopLoopingSounds(Clip... clpClips) {
		for (Clip clp : clpClips) {
			clp.stop();
		}
	}

	// ===============================================
	// KEYLISTENER METHODS
	// ===============================================

	@Override
	public void keyPressed(KeyEvent e) {
		Falcon fal1 = CommandCenter.getFalcon1();
        Falcon fal2 = CommandCenter.getFalcon2();
		int nKey = e.getKeyCode();
		// System.out.println(nKey);

		if (nKey == BEGIN && !CommandCenter.isPlaying())
			startGame(1);

        if (nKey == DOUBLE && !CommandCenter.isPlaying())
            startGame(2);

		if (fal1 != null) switch (nKey) {
            case PAUSE:
                CommandCenter.setPaused(!CommandCenter.isPaused());
                if (CommandCenter.isPaused())
                    stopLoopingSounds(clpMusicBackground, clpThrust);
                else
                    clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
                break;
            case QUIT:
                System.exit(0);
                break;
            case UP1:
                fal1.stopRotating();
                fal1.rotateUp();
                fal1.thrustOn();
                if (!CommandCenter.isPaused())
                    clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
                break;
            case LEFT1:
                fal1.stopRotating();
                fal1.rotateLeft();
                fal1.thrustOn();
                break;
            case RIGHT1:
                fal1.stopRotating();
                fal1.rotateRight();
                fal1.thrustOn();
                break;
            case DOWN1:
                fal1.stopRotating();
                fal1.rotateDown();
                fal1.thrustOn();
                break;
            // possible future use
            // case KILL:
            case SHIELD1:
                fal1.setShield(3);
                Sound.playSound("shieldup.wav");
                //System.out.printf("the shield value is set to %d\n", fal.getShield());
                break;
            // case NUM_ENTER:

            default:
                break;
        }

        if (fal2 != null) switch (nKey) {
            case PAUSE:
                CommandCenter.setPaused(!CommandCenter.isPaused());
                if (CommandCenter.isPaused())
                    stopLoopingSounds(clpMusicBackground, clpThrust);
                else
                    clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
                break;
            case QUIT:
                System.exit(0);
                break;
            case UP2:
                fal2.stopRotating();
                fal2.rotateUp();
                fal2.thrustOn();
                if (!CommandCenter.isPaused())
                    clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
                break;
            case LEFT2:
                fal2.stopRotating();
                fal2.rotateLeft();
                fal2.thrustOn();
                break;
            case RIGHT2:
                fal2.stopRotating();
                fal2.rotateRight();
                fal2.thrustOn();
                break;
            case DOWN2:
                fal2.stopRotating();
                fal2.rotateDown();
                fal2.thrustOn();
                break;
            // possible future use
            // case KILL:
            case SHIELD2:
                fal2.setShield(3);
                Sound.playSound("shieldup.wav");
                //System.out.printf("the shield value is set to %d\n", fal.getShield());
                break;
            // case NUM_ENTER:

            default:
                break;
        }

	}

	@Override
	public void keyReleased(KeyEvent e) {
		Falcon fal1 = CommandCenter.getFalcon1();
        Falcon fal2 = CommandCenter.getFalcon2();
		int nKey = e.getKeyCode();
		 System.out.println(nKey);

		if (fal1 != null) {
			switch (nKey) {
			case FIRE1:
				CommandCenter.movFriends.add(new Bullet(fal1));
				Sound.playSound("laser.wav");
				break;
            case GROUPBULLET1:
                for (int i=0;i<23;i++)
                {
                    CommandCenter.movFriends.add(new specialBullet(fal1,i*15));
                }
                Sound.playSound("laser.wav");
                break;

			//special is a special weapon, current it just fires the cruise missile. 
			case SPECIAL1:
				CommandCenter.movFriends.add(new Cruise(fal1));
				//Sound.playSound("laser.wav");
				break;
				
			case LEFT1:
				fal1.stopRotating();
                fal1.thrustOff();
                clpThrust.stop();
				break;
			case RIGHT1:
				fal1.stopRotating();
                fal1.thrustOff();
                clpThrust.stop();
				break;
            case DOWN1:
                fal1.stopRotating();
                fal1.thrustOff();
                clpThrust.stop();
                break;
			case UP1:
                fal1.stopRotating();
                fal1.thrustOff();
                clpThrust.stop();
				break;
				
			case MUTE:
				if (!bMuted){
					stopLoopingSounds(clpMusicBackground);
					bMuted = !bMuted;
				} 
				else {
					clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
					bMuted = !bMuted;
				}
				break;
				
				
			default:
				break;
			}
		}

        if (fal2 != null) {
            switch (nKey) {
                case FIRE2:
                    CommandCenter.movFriends.add(new Bullet(fal2));
                    Sound.playSound("laser.wav");
                    break;
                case GROUPBULLET2:
                    for (int i=0;i<23;i++)
                    {
                        CommandCenter.movFriends.add(new specialBullet(fal2,i*15));
                    }
                    Sound.playSound("laser.wav");
                    break;

                //special is a special weapon, current it just fires the cruise missile.
                case SPECIAL2:
                    CommandCenter.movFriends.add(new Cruise(fal2));
                    //Sound.playSound("laser.wav");
                    break;

                case LEFT2:
                    fal2.stopRotating();
                    fal2.thrustOff();
                    clpThrust.stop();
                    break;
                case RIGHT2:
                    fal2.stopRotating();
                    fal2.thrustOff();
                    clpThrust.stop();
                    break;
                case DOWN2:
                    fal2.stopRotating();
                    fal2.thrustOff();
                    clpThrust.stop();
                    break;
                case UP2:
                    fal2.stopRotating();
                    fal2.thrustOff();
                    clpThrust.stop();
                    break;

                case MUTE:
                    if (!bMuted){
                        stopLoopingSounds(clpMusicBackground);
                        bMuted = !bMuted;
                    }
                    else {
                        clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
                        bMuted = !bMuted;
                    }
                    break;


                default:
                    break;
            }
        }

	}

	@Override
	// Just need it b/c of KeyListener implementation
	public void keyTyped(KeyEvent e) {
	}
	

	
}

// ===============================================
// ==A tuple takes a reference to an ArrayList and a reference to a Movable
//This class is used in the collision detection method, to avoid mutating the array list while we are iterating
// it has two public methods that either remove or add the movable from the appropriate ArrayList 
// ===============================================

class Tuple{
	//this can be any one of several CopyOnWriteArrayList<Movable>
	private CopyOnWriteArrayList<Movable> movMovs;
	//this is the target movable object to remove
	private Movable movTarget;
	
	public Tuple(CopyOnWriteArrayList<Movable> movMovs, Movable movTarget) {
		this.movMovs = movMovs;
		this.movTarget = movTarget;
	}
	
	public void removeMovable(){
		movMovs.remove(movTarget);
	}
	
	public void addMovable(){
		movMovs.add(movTarget);
	}

}
