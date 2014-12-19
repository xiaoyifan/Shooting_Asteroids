package game.view;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;


import controller.Game;
import game.model.CommandCenter;
import game.model.Falcon;
import game.model.Movable;

import javax.imageio.ImageIO;


public class GamePanel extends Panel {
	
	// ==============================================================
	// FIELDS 
	// ============================================================== 
	 
	// The following "off" vars are used for the off-screen double-bufferred image. 
	private Dimension dimOff;
	private Image imgOff;
     private Image myImage;
	private Graphics grpOff;
	
	private GameFrame gmf;
	private Font fnt = new Font("SansSerif", Font.BOLD, 12);
	private Font fntBig = new Font("SansSerif", Font.BOLD + Font.ITALIC, 36);
	private FontMetrics fmt; 
	private int nFontWidth;
	private int nFontHeight;
	private String strDisplay = "";
	

	// ==============================================================
	// CONSTRUCTOR 
	// ==============================================================
	
	public GamePanel(Dimension dim){
	    gmf = new GameFrame();
		gmf.getContentPane().add(this);
		gmf.pack();
		initView();
		
		gmf.setSize(dim);
		gmf.setTitle("Game Base");
		gmf.setResizable(false);
		gmf.setVisible(true);
		this.setFocusable(true);
	}
	
	
	// ==============================================================
	// METHODS 
	// ==============================================================
	
	private void drawScore(Graphics g) {
		g.setColor(Color.yellow);
		g.setFont(fnt);

        String scoreStr1 = "";
		if (CommandCenter.getScore() != 0 ) {
            scoreStr1 = "SCORE1 :  " + CommandCenter.getScore();
			//g.drawString("SCORE :  " + CommandCenter.getScore(), nFontWidth, nFontHeight);

		} else {
            scoreStr1 = "NO SCORE1";
			//g.drawString("NO SCORE", nFontWidth, nFontHeight);
		}

        String scoreStr2 = "";
        if (CommandCenter.getScore2() != 0 ) {
            scoreStr2 = "SCORE2 :  " + CommandCenter.getScore2();
            //g.drawString("SCORE :  " + CommandCenter.getScore(), nFontWidth, nFontHeight);

        } else {
            scoreStr2 = "NO SCORE2";
            //g.drawString("NO SCORE", nFontWidth, nFontHeight);
        }


        String shieldStr1 = "";

        if (CommandCenter.getFalcon1() != null)
        {
            shieldStr1 = "SHIELD1: "+CommandCenter.getFalcon1().getShield();
        }
        else{
            shieldStr1 = "NO SHIELD1";
        }

        String shieldStr2 = "";

        if (CommandCenter.getFalcon2() != null)
        {
            shieldStr2 = "SHIELD2: "+CommandCenter.getFalcon2().getShield();
        }
        else{
            shieldStr2 = "NO SHIELD2";
        }


        String levelStr = "";
        if (CommandCenter.getLevel()!=0)
        {
            levelStr = "LEVEL: "+ CommandCenter.getLevel();
        }
        else{
            levelStr = "NO LEVEL";
        }



        g.drawString(scoreStr1+" \n"+shieldStr1+ " \n"+ scoreStr2+" \n"+shieldStr2+ " \n"+ levelStr , nFontWidth, nFontHeight);

	}


	
	@SuppressWarnings("unchecked")
	public void update(Graphics g){
		if (grpOff == null || Game.DIM.width != dimOff.width
				|| Game.DIM.height != dimOff.height) {
			dimOff = Game.DIM;
			imgOff = createImage(Game.DIM.width, Game.DIM.height);
			grpOff = imgOff.getGraphics();
		}
		// Fill in background with dark gray.
		grpOff.setColor(Color.darkGray);

        String Path = "src/background.jpg";

        if (CommandCenter.isPlaying() == false) {
            try {
                myImage = ImageIO.read(new File(Path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            grpOff.drawImage(myImage, 0, 0, Game.DIM.width, Game.DIM.height, Color.black, null);
        }
        else {

            grpOff.fillRect(0, 0, Game.DIM.width, Game.DIM.height);

        }
		drawScore(grpOff);

		
		if (!CommandCenter.isPlaying()) {
			displayTextOnScreen();
		} else if (CommandCenter.isPaused()) {
			strDisplay = "Game Paused";
			grpOff.drawString(strDisplay,
					(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4);
		}
		
		//playing and not paused!
		else {
			
			//draw them in decreasing level of importance
			//friends will be on top layer and debris on the bottom
			iterateMovables(grpOff, 
					   CommandCenter.movDebris,
			           CommandCenter.movFloaters, 
			           CommandCenter.movFoes,
                    CommandCenter.movShield,
			           CommandCenter.movFriends);
			
			
			drawNumberShipsLeft1(grpOff);
            if (CommandCenter.getFalcon2()!=null) {
                drawNumberShipsLeft2(grpOff);
            }
			if (CommandCenter.isGameOver()) {
				CommandCenter.setPlaying(false);
				//bPlaying = false;
			}
		}
		//draw the double-Buffered Image to the graphics context of the panel
		g.drawImage(imgOff, 0, 0, this);
	} 


	
	//for each movable array, process it.
	private void iterateMovables(Graphics g, CopyOnWriteArrayList<Movable>...movMovz){
		
		for (CopyOnWriteArrayList<Movable> movMovs : movMovz) {
			for (Movable mov : movMovs) {

				mov.move();
				mov.draw(g);
				mov.fadeInOut();
				mov.expire();
			}
		}
		
	}
	

	// Draw the number of falcons left on the bottom-right of the screen. 
	private void drawNumberShipsLeft1(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(new BasicStroke(1.0f));
		Falcon fal1 = CommandCenter.getFalcon1();
		double[] dLens = fal1.getLengths();
		int nLen = fal1.getDegrees().length;
		Point[] pntMs = new Point[nLen];
		int[] nXs = new int[nLen];
		int[] nYs = new int[nLen];
	
		//convert to cartesean points
		for (int nC = 0; nC < nLen; nC++) {
			pntMs[nC] = new Point((int) (10 * dLens[nC] * Math.sin(Math
					.toRadians(90) + fal1.getDegrees()[nC])),
					(int) (10 * dLens[nC] * Math.cos(Math.toRadians(90)
							+ fal1.getDegrees()[nC])));
		}
		
		//set the color to white
		g.setColor(Color.white);
		//for each falcon left (not including the one that is playing)
		for (int nD = 1; nD < CommandCenter.getNumFalcons1(); nD++) {
			//create x and y values for the objects to the bottom right using cartesean points again
			for (int nC = 0; nC < fal1.getDegrees().length; nC++) {
				nXs[nC] = pntMs[nC].x + Game.DIM.width - (20 * nD);
				nYs[nC] = pntMs[nC].y + Game.DIM.height - 80;
			}
			g.drawPolygon(nXs, nYs, nLen);
		}
        g2d.setStroke(new BasicStroke(2.0f));
	}

     private void drawNumberShipsLeft2(Graphics g) {
         Graphics2D g2d = (Graphics2D)g;
         g2d.setStroke(new BasicStroke(1.0f));
         Falcon fal2 = CommandCenter.getFalcon2();
         double[] dLens = fal2.getLengths();
         int nLen = fal2.getDegrees().length;
         Point[] pntMs = new Point[nLen];
         int[] nXs = new int[nLen];
         int[] nYs = new int[nLen];

         //convert to cartesean points
         for (int nC = 0; nC < nLen; nC++) {
             pntMs[nC] = new Point((int) (10 * dLens[nC] * Math.sin(Math
                     .toRadians(90) + fal2.getDegrees()[nC])),
                     (int) (10 * dLens[nC] * Math.cos(Math.toRadians(90)
                             + fal2.getDegrees()[nC])));
         }

         //set the color to white
         g.setColor(Color.yellow);
         //for each falcon left (not including the one that is playing)
         for (int nD = 1; nD < CommandCenter.getNumFalcons2(); nD++) {
             //create x and y values for the objects to the bottom right using cartesean points again
             for (int nC = 0; nC < fal2.getDegrees().length; nC++) {
                 nXs[nC] = pntMs[nC].x + Game.DIM.width - (20 * nD);
                 nYs[nC] = pntMs[nC].y + Game.DIM.height - 40;
             }
             g.drawPolygon(nXs, nYs, nLen);
         }
         g2d.setStroke(new BasicStroke(2.0f));
     }
	
	private void initView() {
		Graphics g = getGraphics();			// get the graphics context for the panel
		g.setFont(fnt);						// take care of some simple font stuff
		fmt = g.getFontMetrics();
		nFontWidth = fmt.getMaxAdvance();
		nFontHeight = fmt.getHeight();
		g.setFont(fntBig);					// set font info
	}
	
	// This method draws some text to the middle of the screen before/after a game
	private void displayTextOnScreen() {

		strDisplay = "GAME IS GOING TO START";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 5);

		strDisplay = "PLAYER1 use the arrow keys to turn and thrust";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 5
						+ nFontHeight + 40);

        strDisplay = "PLAYER2 use the W,S,A,D keys to turn and thrust";
        grpOff.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 5
                        + nFontHeight + 80);

		strDisplay = "PLAYER1 use the space bar to fire";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 5
						+ nFontHeight + 120);

        strDisplay = "PLAYER2 use T key to fire";
        grpOff.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 5
                        + nFontHeight + 160);

		strDisplay = "'B' to Start";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 5
						+ nFontHeight + 200);

		strDisplay = "'P' to Pause";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 5
						+ nFontHeight + 240);

		strDisplay = "'Q' to Quit";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 5
						+ nFontHeight + 280);
		strDisplay = "PLAYER1 press 'I' for Shield, PLAYER2 press 'Z' for shield";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 5
						+ nFontHeight + 320);

		strDisplay = "PLAYER1 press 'F' for SPECIAL WEAPON, PLAYER2 press 'J' for SPECIAL WEAPON";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 5
						+ nFontHeight + 360);

		strDisplay = "PLAYER1 press 'G' for SUPER FIRE, PLAYER2 press 'K' for SUPER FIRE";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 5
						+ nFontHeight + 400);


        strDisplay = "press 'B' to start single player game, press 'V'for double player";
        grpOff.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 5
                        + nFontHeight + 450);
	}
	
	public GameFrame getFrm() {return this.gmf;}
	public void setFrm(GameFrame frm) {this.gmf = frm;}	
}