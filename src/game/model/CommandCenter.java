package game.model;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

import sounds.Sound;

// I only want one Command Center and therefore this is a perfect candidate for static
// Able to get access to methods and my movMovables ArrayList from the static context.
public class CommandCenter {

	private static int nNumFalcon;
    private static int nNumFalcon2;
	private static int nLevel;
	private static long lScore;
    private static long lScore2;
	private static Falcon falShip;
    private static Falcon falShip2;
	private static boolean bPlaying;
	private static boolean bPaused;
	
	// These ArrayLists are thread-safe
	public static CopyOnWriteArrayList<Movable> movDebris = new CopyOnWriteArrayList<Movable>();
	public static CopyOnWriteArrayList<Movable> movFriends = new CopyOnWriteArrayList<Movable>();
	public static CopyOnWriteArrayList<Movable> movFoes = new CopyOnWriteArrayList<Movable>();
	public static CopyOnWriteArrayList<Movable> movFloaters = new CopyOnWriteArrayList<Movable>();
    public static CopyOnWriteArrayList<Movable> movShield = new CopyOnWriteArrayList<Movable>();

	// Constructor made private - static Utility class only
	private CommandCenter() {}
	
	public static void initGame(int number){
		setLevel(1);
		setScore1(0);
        setScore2(0);
        if (number == 1) {
            setNumFalcons1(3);
        }
        else {
            setNumFalcons1(3);
            setNumFalcons2(3);
        }
		spawnFalcon1(true);
        spawnFalcon2(true);
	}
	
	// The parameter is true if this is for the beginning of the game, otherwise false
	// When you spawn a new falcon, you need to decrement its number
	public static void spawnFalcon1(boolean bFirst) {

		if (getNumFalcons1() != 0) {
			falShip = new Falcon("player1", Color.white);
			movFriends.add(falShip);
			if (!bFirst)
			    setNumFalcons1(getNumFalcons1() - 1);
		}
		
		Sound.playSound("shipspawn.wav");

	}

    public static void spawnFalcon2(boolean bFirst) {

        if (getNumFalcons2() != 0) {
            falShip2 = new Falcon("player2",Color.YELLOW);
            movFriends.add(falShip2);
            if (!bFirst)
                setNumFalcons2(getNumFalcons2() - 1);
        }
        System.out.println("the second ship is built");

        Sound.playSound("shipspawn.wav");

    }
	
	public static void clearAll(){
		movDebris.clear();
		movFriends.clear();
		movFoes.clear();
		movFloaters.clear();
        movShield.clear();
	}

	public static boolean isPlaying() {
		return bPlaying;
	}

	public static void setPlaying(boolean bPlaying) {
		CommandCenter.bPlaying = bPlaying;
	}

	public static boolean isPaused() {
		return bPaused;
	}

	public static void setPaused(boolean bPaused) {
		CommandCenter.bPaused = bPaused;
	}
	
	public static boolean isGameOver() {		//if the number of falcons is zero, then game over
		if (getNumFalcons1() == 0 && getNumFalcons2() == 0) {
			return true;
		}
		return false;
	}

	public static int getLevel() {
		return nLevel;
	}

	public  static long getScore() {
		return lScore;
	}

    public  static long getScore2() {
        return lScore2;
    }

	public static void setScore1(long lParam) {
		lScore = lParam;
	}
    public static void setScore2(long lParam) {
        lScore2 = lParam;
    }

	public static void setLevel(int n) {
		nLevel = n;
	}

	public static int getNumFalcons1() {
		return nNumFalcon;
	}

	public static void setNumFalcons1(int nParam) {
		nNumFalcon = nParam;
	}

    public static int getNumFalcons2() {
        return nNumFalcon2;
    }

    public static void setNumFalcons2(int nParam) {
        nNumFalcon2 = nParam;
    }
	
	public static Falcon getFalcon1(){
		return falShip;
	}
	
	public static void setFalcon(Falcon falParam){
		falShip = falParam;
	}


    public static Falcon getFalcon2(){
        return falShip2;
    }

    public static void setFalcon2(Falcon falParam){
        falShip2 = falParam;
    }

	public static CopyOnWriteArrayList<Movable> getMovDebris() {
		return movDebris;
	}



	public static CopyOnWriteArrayList<Movable> getMovFriends() {
		return movFriends;
	}



	public static CopyOnWriteArrayList<Movable> getMovFoes() {
		return movFoes;
	}


	public static CopyOnWriteArrayList<Movable> getMovFloaters() {
		return movFloaters;
	}

    public static CopyOnWriteArrayList<Movable> getMovShield() {
        return movShield;
    }

	
	
}
