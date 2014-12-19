package game.model;


import java.awt.*;
import java.util.ArrayList;

import controller.Game;

public class UFO extends Sprite {


    private int nSpin;

    //radius of a large asteroid
    private final int RAD = 40;

    private final int fireSplit = 50;
    private int ticker = 0;

    //nSize determines if the Asteroid is Large (0), Medium (1), or Small (2)
    //when you explode a Large asteroid, you should spawn 2 or 3 medium asteroids
    //same for medium asteroid, you should spawn small asteroids
    //small asteroids get blasted into debris
    public UFO(){

        //call Sprite constructor
        super();
        ArrayList<Point> pntCs = new ArrayList<Point>();
        pntCs.add(new Point(0, 2));
        pntCs.add(new Point(2, 2));
        pntCs.add(new Point(4, 4));
        pntCs.add(new Point(2, 2));
        pntCs.add(new Point(2, 0));
        pntCs.add(new Point(2, -2));
        pntCs.add(new Point(4, -4));
        pntCs.add(new Point(2, -2));
        pntCs.add(new Point(0, -2));
        pntCs.add(new Point(-2, -2));
        pntCs.add(new Point(-4, -4));
        pntCs.add(new Point(-2, -2));
        pntCs.add(new Point(-2, 0));
        pntCs.add(new Point(-2, 2));
        pntCs.add(new Point(-4, 4));
        pntCs.add(new Point(-2, 2));
        pntCs.add(new Point(0, 2));

        assignPolarPoints(pntCs);

        //the spin will be either plus or minus 0-9
        int nSpin = Game.R.nextInt(10);
        if(nSpin %2 ==0)
            nSpin = -nSpin;
        setSpin(nSpin);

        //random delta-x
        int nDX = Game.R.nextInt(4);
        if(nDX %2 ==0)
            nDX = -nDX;
        setDeltaX(nDX);

        //random delta-y
        int nDY = Game.R.nextInt(4);
        if(nDY %2 ==0)
            nDY = -nDY;
        setDeltaY(nDY);


        setRadius(RAD);

        setCenter(new Point(Game.R.nextInt(Game.DIM.width),
                Game.R.nextInt(Game.DIM.height)));


        Paint paint=new GradientPaint(0, 0, Color.GREEN, 50,50, Color.BLUE, true);
        setPaint(paint);

        //put falcon in the middle.
        setCenter(new Point(Game.DIM.width / 2, Game.DIM.height / 2));

        //with random orientation
        setOrientation(Game.R.nextInt(360));

        //this is the size of the falcon
        setRadius(50);

        //these are falcon specific
        setFadeValue(5);

    }




    //overridden
    public void move(){
        super.move();

        //an asteroid spins, so you need to adjust the orientation at each move()
        setOrientation(getOrientation() + getSpin());

        if (ticker == fireSplit)
        {
            CommandCenter.movFoes.add(new enemyBullets(this, CommandCenter.getFalcon1()));
            ticker = 0;


        }
        else {
            ticker ++;
        }


    }


    public int getSpin() {
        return this.nSpin;
    }


    public void setSpin(int nSpin) {
        this.nSpin = nSpin;
    }


    @Override
    public void draw(Graphics g) {
        super.draw(g);
        //fill this polygon (with whatever color it has)
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
        //now draw a white border
        g.setColor(Color.cyan);
        g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
        Graphics2D gg =  (Graphics2D)g;

        gg.drawOval(getCenter().x - getRadius(),
                getCenter().y - getRadius(), getRadius() * 2,
                getRadius() * 2);

    }

}
