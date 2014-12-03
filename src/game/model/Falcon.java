package game.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controller.Game;


public class Falcon extends Sprite {

	// ==============================================================
	// FIELDS 
	// ==============================================================
	
	private final double THRUST = .65;

	final int DEGREE_STEP = 20;
	
	private boolean bShield = false;
	private boolean bFlame = false;
	private boolean bProtected; //for fade in and out
	
	private boolean bThrusting = false;
	private boolean bTurningRight = false;
	private boolean bTurningLeft = false;
    private boolean bTurningDown = false;
    private boolean bTurningUp =false;


	
	private int nShield;
			
	private final double[] FLAME = { 23 * Math.PI / 24 + Math.PI / 2,
			Math.PI + Math.PI / 2, 25 * Math.PI / 24 + Math.PI / 2 };

	private int[] nXFlames = new int[FLAME.length];
	private int[] nYFlames = new int[FLAME.length];

	private Point[] pntFlames = new Point[FLAME.length];

	
	// ==============================================================
	// CONSTRUCTOR 
	// ==============================================================
	
	public Falcon() {
		super();

		ArrayList<Point> pntCs = new ArrayList<Point>();
		
		// top of ship
		pntCs.add(new Point(0, 45));

		//right points
		pntCs.add(new Point(10, 35));


		pntCs.add(new Point(10, -5));
        pntCs.add(new Point(5, -5));
        pntCs.add(new Point(5, 15));
        pntCs.add(new Point(0, 20));
        pntCs.add(new Point(-5, 15));
        pntCs.add(new Point(5, 15));
        pntCs.add(new Point(-5, 15));
        pntCs.add(new Point(-5, -5));
        pntCs.add(new Point(-10, -5));
        pntCs.add(new Point(5, -5));
        pntCs.add(new Point(10, -10));
        pntCs.add(new Point(-10, -10));
        pntCs.add(new Point(-5, -5));

		pntCs.add(new Point(20, -5));
		pntCs.add(new Point(20, 20));
		pntCs.add(new Point(35, -20));
		pntCs.add(new Point(5, -20));
		pntCs.add(new Point(0, -25));
		pntCs.add(new Point(-5, -20));
		pntCs.add(new Point(-35, -20));

		//left points
		pntCs.add(new Point(-20, 20));
		pntCs.add(new Point(-20, -5));
		pntCs.add(new Point(-10, -5));
		pntCs.add(new Point(-10, 35));



		assignPolarPoints(pntCs);

		setColor(Color.white);
		
		//put falcon in the middle.
		setCenter(new Point(Game.DIM.width / 2, Game.DIM.height / 2));
		
		//with random orientation
		setOrientation(Game.R.nextInt(360));
		
		//this is the size of the falcon
		setRadius(45);

		//these are falcon specific
		setProtected(true);
		setFadeValue(0);
        setColor(Color.yellow);
	}
	
	
	// ==============================================================
	// METHODS 
	// ==============================================================

	public void move() {
		super.move();
		if (bThrusting) {
			bFlame = true;
			double dAdjustX = Math.cos(Math.toRadians(getOrientation()))
					* THRUST;
			double dAdjustY = Math.sin(Math.toRadians(getOrientation()))
					* THRUST;
			setDeltaX(dAdjustX*20);
			setDeltaY(dAdjustY*20);
		}
        else{
            setDeltaX(0);
            setDeltaY(0);
        }

      /*  if (bTurningLeft) {

            if (getOrientation() <= 0 && bTurningLeft) {
                setOrientation(360);
            }
            setOrientation(getOrientation() - DEGREE_STEP);

        }*/
        if (bTurningLeft) {

            if (getOrientation()>180&& getOrientation()<=360)
            {

                    setOrientation(getOrientation() - DEGREE_STEP);
                    if (getOrientation()<180)
                    {
                        setOrientation(180);
                    }

            }
            else{


                    setOrientation(getOrientation() + DEGREE_STEP);
                    if (getOrientation()>180)
                    {
                        setOrientation(180);
                    }

            }


		} 
		if (bTurningRight) {

            if (getOrientation()>180 && getOrientation()<360)
            {

                    setOrientation(getOrientation() + DEGREE_STEP);
                    if (getOrientation()>360)
                    {
                        setOrientation(360);
                    }

            }

            else if (getOrientation()<=180 && getOrientation()>0){


                    setOrientation(getOrientation() - DEGREE_STEP);
                    if (getOrientation()<0)
                    {
                        setOrientation(360);
                    }

            }
		}

        if (bTurningDown) {

            if (getOrientation()>90 && getOrientation()<270)
            {

                    setOrientation(getOrientation() - DEGREE_STEP);
                    if (getOrientation()<90)
                    {
                        setOrientation(90);
                    }

            }
            else{

                if(getOrientation()>=270)
                {
                    setOrientation(getOrientation() + DEGREE_STEP);
                }
                if (getOrientation() >= 360)
                {
                    setOrientation(0);
                }
                if(getOrientation()<90)
                {
                    setOrientation(getOrientation() + DEGREE_STEP);
                    if (getOrientation()>90)
                    {
                        setOrientation(90);
                    }
                }
            }
        }
        if (bTurningUp) {

            if (getOrientation()>90 && getOrientation()<270)
            {

                    setOrientation(getOrientation() + DEGREE_STEP);
                    if (getOrientation()>270)
                    {
                        setOrientation(270);
                    }

            }
            else{

                if(getOrientation()<=90)
                {
                    setOrientation(getOrientation() - DEGREE_STEP);
                }

                if (getOrientation() <= 0)
                {
                    setOrientation(360);
                }

                if(getOrientation()>270)
                {
                    setOrientation(getOrientation() - DEGREE_STEP);
                    if (getOrientation()<270)
                    {
                        setOrientation(270);
                    }
                }


            }
        }
    } //end move

	public void rotateLeft() {
		bTurningLeft = true;
	}

    public void rotateDown(){
        bTurningDown = true;
    }

	public void rotateRight() {
		bTurningRight = true;
	}

    public void rotateUp()
    {
        bTurningUp = true;
    }

	public void stopRotating() {
		bTurningRight = false;
		bTurningLeft = false;
        bTurningDown = false;
        bTurningUp = false;
	}

	public void thrustOn() {
		bThrusting = true;
	}

	public void thrustOff() {
		bThrusting = false;
		bFlame = false;
	}

	private int adjustColor(int nCol, int nAdj) {
		if (nCol - nAdj <= 0) {
			return 0;
		} else {
			return nCol - nAdj;
		}
	}

	public void draw(Graphics g) {

		//does the fading at the beginning or after hyperspace
		Color colShip;
		if (getFadeValue() == 255) {
			colShip = Color.yellow;
		} else {
			colShip = new Color(adjustColor(getFadeValue(), 200), adjustColor(
					getFadeValue(), 175), getFadeValue());
		}




		if (bShield && nShield > 0) {

			g.setColor(Color.GREEN);
            Graphics2D gg =  (Graphics2D)g;
            gg.setStroke(new BasicStroke(4.0f));
			gg.drawOval(getCenter().x - getRadius(),
					getCenter().y - getRadius(), getRadius() * 2,
					getRadius() * 2);

            gg.setStroke(new BasicStroke(2.0f));
		} //end if shield

		//thrusting
		if (bFlame) {
			g.setColor(colShip);

            Graphics2D g2d = (Graphics2D) g;
            Paint paint=new GradientPaint(0, 0, Color.RED, 50,50, Color.ORANGE, true);
            g2d.setPaint(paint);

			for (int nC = 0; nC < FLAME.length; nC++) {
				if (nC % 2 != 0) //odd
				{
					pntFlames[nC] = new Point((int) (getCenter().x + 2
							* getRadius()
							* Math.sin(Math.toRadians(getOrientation())
									+ FLAME[nC])), (int) (getCenter().y - 2
							* getRadius()
							* Math.cos(Math.toRadians(getOrientation())
									+ FLAME[nC])));

				} else //even
				{
					pntFlames[nC] = new Point((int) (getCenter().x + getRadius()
							* 1.1
							* Math.sin(Math.toRadians(getOrientation())
									+ FLAME[nC])),
							(int) (getCenter().y - getRadius()
									* 1.1
									* Math.cos(Math.toRadians(getOrientation())
											+ FLAME[nC])));

				} //end even/odd else

			} //end for loop

			for (int nC = 0; nC < FLAME.length; nC++) {
				nXFlames[nC] = pntFlames[nC].x;
				nYFlames[nC] = pntFlames[nC].y;

			} //end assign flame points

			//g.setColor( Color.white );
			g.fillPolygon(nXFlames, nYFlames, FLAME.length);

		} //end if flame

		drawShipWithColor(g, colShip);

	} //end draw()

	public void drawShipWithColor(Graphics g, Color col) {
		super.draw(g);
		g.setColor(col);
		g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
	}

	public void fadeInOut() {
		if (getProtected()) {
			setFadeValue(getFadeValue() + 3);
		}
		if (getFadeValue() == 255) {
			setProtected(false);
		}
	}
	
	public void setProtected(boolean bParam) {
		if (bParam) {
			setFadeValue(0);
		}
		bProtected = bParam;
	}

	public void setProtected(boolean bParam, int n) {
		if (bParam && n % 3 == 0) {
			setFadeValue(n);
		} else if (bParam) {
			setFadeValue(0);
		}
		bProtected = bParam;
	}	

	public boolean getProtected() {return bProtected;}
	public void setShield(int n) {
        bShield = true;
        nShield = n;
    }
	public int getShield() {return nShield;}


   
} //end class
