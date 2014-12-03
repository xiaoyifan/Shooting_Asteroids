package game.model;

import controller.Game;

import java.awt.*;

/**
 * Created by xiaoyifan on 11/25/14.
 */
public class Explosion extends MoveAdapter {

    private int mExpiry;
    private Point mCenter;
    private int mRadiux;
    private int increment;

    public Explosion(Movable mov, int mRadiux,int increment) {
        mCenter = mov.getCenter();
        this.mRadiux = mRadiux;
        this.increment = increment;
        mExpiry = 20;
    }
    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(Game.R.nextInt(256),Game.R.nextInt(256),Game.R.nextInt(256)));
        g.fillOval(mCenter.x - mRadiux/2, mCenter.y - mRadiux/2, mRadiux, mRadiux);
// super.draw(g);
    }
    @Override
    public void expire() {
// super.expire();
        if (mExpiry > 0) {
            mRadiux += this.increment;
            mExpiry--;
        } else {
            CommandCenter.getMovDebris().remove(this);
        }
    }

    @Override
    public void move() {
        super.move();

    }
}
