package cutthetree;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

/**
 * The Player class represents the current state of the player
 */
public class Player extends Field {
    // This cannot be static as the image might change depending on the set game avatar
    private Image image;

    /**
     * The current animation on the X axis
     */
    private int animX;

    /**
     * The current animation on the Y axis
     */
    private int animY;

    /**
     * The current state of the animation
     */
    private int animState = 8;

    private Lumberaxe axe;
    private String message = "";

    private Direction direction = Direction.DOWN;

    public Player(int xPos, int yPos) {
        super(xPos, yPos);

        if (image == null) loadImage();
    }

    /**
     * Loads the images required to paint this object on screen
     */
    private void loadImage() {
        try {
            Avatar avatar = Game.getAvatar();
            image = ImageIO.read(Player.class.getResource("/img/" + avatar.toString().toLowerCase() + "Sprite.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isMoving() {
        return animX + animY != 0;
    }

    public boolean move(int dx, int dy) {
        // Disallow move while animating
        if (isMoving()) return false;

        animX = dx;
        animY = dy;

        xPos += dx;
        yPos += dy;

        return true;
    }

    public void changeDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void grabLumberaxe(Lumberaxe axe) {
        this.axe = axe;
    }

    public Lumberaxe getAxe() {
        return this.axe;
    }

    /**
     * Let the player say the given message
     */
    public void say(String text) {
        message = text;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        int x = xPos * SIZE;
        int y = yPos * SIZE;
        int offsetX = 150;
        int offsetY = direction.ordinal() * SIZE;

        // Calculate current animation positions
        if (animX + animY != 0) {
            if (animState == 8) {
                animState = 0;
            } else {
                animState++;
            }

            x += animX * ((SIZE / 9) * (animState + 1)) + (SIZE * -animX);
            y += animY * ((SIZE / 9) * (animState + 1)) + (SIZE * -animY);

            offsetX = (animState / 4) * SIZE;
            if (animState == 8) {
                animX = 0;
                animY = 0;
            }
        }

        g.drawImage(
                image, // Source image,
                x, y, x + SIZE, y + SIZE, // Destination position
                offsetX, offsetY, offsetX + SIZE, offsetY + SIZE, // Source position
                null
        );

        if (!message.isEmpty()) {
            new SpeechBalloon(xPos, yPos, message).paint(g);
        }
    }
}
