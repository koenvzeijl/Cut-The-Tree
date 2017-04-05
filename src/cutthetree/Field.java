package cutthetree;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

/**
 * The Field class is an empty field in the {@link PlayField}
 * and a base class for other custom fields
 */
public class Field {
    public static final int SIZE = 75;

    protected static Image grass;
    private boolean coin = false;

    /**
     * The current position on the play field
     */
    protected int xPos, yPos;
    protected boolean isSolid = false;

    public Field(int x, int y) {
        xPos = x;
        yPos = y;

        if (grass == null) loadImage();
    }

    /**
     * Loads the images required to paint this object on screen
     */
    private static void loadImage() {
        try {
            grass = ImageIO.read(Field.class.getResource("/img/grass.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Whether the player can walk over this field or not
     */
    public boolean isSolid() {
        return isSolid;
    }

    /**
     * Whether the field contains a coin
     */
    public boolean hasCoin(){return coin;}


    public void setCoin(boolean coin){this.coin = coin;}

    /**
     * Paint a representation of this field on screen
     */
    public void paint(Graphics g) {
        g.drawImage(grass, xPos * SIZE, yPos * SIZE, null);
    }
}
