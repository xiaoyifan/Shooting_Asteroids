package game.model;

import controller.Game;

import java.awt.*;

/**
 * Created by xiaoyifan on 11/25/14.
 */
public class Debris extends Sprite{

    private Color color;
    private int Spin;


    public Debris(Sprite sprite, Point begin, Point end)
    {
        super();
        double radius = Math.sqrt(Math.pow(end.getY()-begin.getY(),2)+Math.pow(end.getX()-begin.getX(),2))/2;

        Point middle = new Point((int)(end.getX()+begin.getX())/2, (int)(end.getY()+begin.getY())/2);

        setRadius((int)radius);
        setCenter(middle);

        setDeltaX(sprite.getDeltaX()+(middle.getX()-sprite.getCenter().x)/6);
        setDeltaY(sprite.getDeltaY()+(middle.getY()-sprite.getCenter().y)/6);


        //setOrientation((int)Math.toDegrees(Math.atan2(end.y-middle.y, end.x-middle.x)));
        double dRadians = Math.atan2(begin.y - middle.y, begin.x - middle.x);
        setOrientation((int)Math.toDegrees(dRadians));

       setLengths(new double[]{1,1});
       setDegrees(new double[]{Math.PI / 2, 180 * Math.PI / 360 + Math.PI});

        if(Game.R.nextBoolean())
            setSpin(Game.R.nextInt(4));
        else
            setSpin(-Game.R.nextInt(4));


        setExpire(30);
        setFadeValue( 255 );

        color = sprite.getColor();
        this.setColor(color);

    }

    public void move(){
        super.move();
        setOrientation(getOrientation() + getSpin());

    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getSpin() {
        return Spin;
    }

    public void setSpin(int spin) {
        this.Spin = spin;
    }

    public void expire(){
        if (getExpire() == 0)
        {

            CommandCenter.movDebris.remove(this);
        }
        else if(getExpire() == 25)
        {
            CommandCenter.movDebris.add(new Explosion( this ,1,1));
            setExpire(getExpire() - 1);
        }
        else
            setExpire(getExpire() - 1);
    }

    public void fadeInOut() {

        Color color = getColor();

        if (getFadeValue()>20)
            setFadeValue(getFadeValue()-10);

        int R = 255 - color.getRed();
        int G = 255 - color.getGreen();
        int B = 255 - color.getBlue();

        setColor(new Color( Math.abs( getFadeValue() - R),Math.abs( getFadeValue() - G ),Math.abs( getFadeValue() - B ) ));

    }
}
