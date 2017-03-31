package cutthetree;

import java.awt.event.KeyEvent;

/**
 * This represents the current direction of a {@link Player}
 */
public enum Direction {
    DOWN(0, 1),
    UP(0, -1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    private int dx;
    private int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public static Direction fromKeyCode(int code) {
        switch (code) {
            case KeyEvent.VK_DOWN: return Direction.DOWN;
            case KeyEvent.VK_UP: return Direction.UP;
            case KeyEvent.VK_LEFT: return Direction.LEFT;
            case KeyEvent.VK_RIGHT: return Direction.RIGHT;
        }

        return null;
    }
}
