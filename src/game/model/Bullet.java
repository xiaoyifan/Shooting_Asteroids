package game.model;

import java.awt.*;
import java.util.ArrayList;

import controller.Game;


public class Bullet extends Sprite {

	  private final double FIRE_POWER = 35.0;

        private Falcon falcon;

	 
	
public Bullet(Falcon fal){
		
		super();
		setFalcon(fal);
		
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
	    setDeltaX( fal.getDeltaX() +
	               Math.cos( Math.toRadians( fal.getOrientation() ) ) * FIRE_POWER );
	    setDeltaY( fal.getDeltaY() +
	               Math.sin( Math.toRadians( fal.getOrientation() ) ) * FIRE_POWER );
	    setCenter( fal.getCenter() );

	    //set the bullet orientation to the falcon (ship) orientation
	    setOrientation(fal.getOrientation());

        setColor(Color.RED);


	}

    public Falcon getFalcon() {
        return falcon;
    }

    public void setFalcon(Falcon falcon) {
        this.falcon = falcon;
    }

    //override the expire method - once an object expires, then remove it from the arrayList.
	public void expire(){
 		if (getExpire() == 0) {
           // CommandCenter.movDebris.add(new Explosion(this));
            CommandCenter.movFriends.remove(this);
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
        g.setColor(Color.orange);
        g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }

}
