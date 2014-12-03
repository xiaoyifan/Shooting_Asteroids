package game.model;

import java.awt.*;

/**
 * Created by xiaoyifan on 11/25/14.
 */
public abstract class MoveAdapter implements Movable{

    @Override
    public void move() {
    }
    @Override
    public void draw(Graphics g) {
    }
    @Override
    public int points() {
        return 0;
    }
    @Override
    public Point getCenter() {
        return null;
    }
    @Override
    public int getRadius() {
        return 0;
    }
    @Override
    public void expire() {
    }
    @Override
    public void fadeInOut() {
    }
}
