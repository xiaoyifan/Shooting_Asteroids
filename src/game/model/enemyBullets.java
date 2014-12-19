package game.model;

import java.awt.*;
import java.util.ArrayList;

import controller.Game;


public class enemyBullets extends Sprite {

    private final double FIRE_POWER = 15.0;



    public enemyBullets(UFO ufo, Falcon fal){

        super();


        //defined the points on a cartesean grid
        ArrayList<Point> pntCs = new ArrayList<Point>();

        pntCs.add(new Point(0,3)); //top point

        pntCs.add(new Point(1,-1));
        pntCs.add(new Point(0,-2));
        pntCs.add(new Point(-1,-1));

        assignPolarPoints(pntCs);

        //a bullet expires after 20 frames
        setExpire( 20 );
        setRadius(6);


        //everything is relative to the falcon ship that fired the bullet
        setDeltaX(
                Math.cos(Math.toRadians(CalculateShootingOrientation(ufo,fal))) * FIRE_POWER );
        setDeltaY(
                Math.sin(Math.toRadians(CalculateShootingOrientation(ufo,fal))) * FIRE_POWER );
        setCenter(ufo.getCenter());

        //set the bullet orientation to the falcon (ship) orientation
        setOrientation(CalculateShootingOrientation(ufo,fal));

        setColor(Color.cyan);


    }



    private int CalculateShootingOrientation(UFO ufo,Falcon fal)
    {
        Point self = ufo.getCenter();

        Point falconCenter = fal.getCenter();

        int dx = falconCenter.x - self.x;
        int dy = falconCenter.y - self.y;

        double inRads = Math.atan2(dy,dx);

        // We need to map to coord system when 0 degree is at 3 O'clock, 270 at 12 O'clock
        if (inRads < 0)
            inRads = Math.abs(inRads);
        else
            inRads = 2*Math.PI - inRads;

        inRads = inRads*(-1);



        int degree =  (int)Math.toDegrees(inRads);
        if (degree<0)
        {
            degree+=360;
        }


        return degree;
    }

    //override the expire method - once an object expires, then remove it from the arrayList. 
    public void expire(){
        if (getExpire() == 0) {
            // CommandCenter.movDebris.add(new Explosion(this));
            CommandCenter.movFoes.remove(this);
        }
        else
            setExpire(getExpire() - 1);
    }


    @Override
    public void draw(Graphics g) {
        super.draw(g);
        //fill this polygon (with whatever color it has)
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
        //now draw a white border
        g.setColor(Color.blue);
        g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }

}
