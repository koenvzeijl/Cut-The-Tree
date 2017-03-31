package cutthetree;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The PlayField class is responsible for the game itself.
 * <p>
 * It will paint the current level and will respond
 * to key presses for walking or cutting.
 */
public class PlayField extends JComponent {
    private static Image imageAxe;
    private static Image imageBackpack;

    private boolean finished = false;

    private Player player;

    private Direction walking = null;
    private ArrayList<ArrayList<Field>> fields = new ArrayList<>();

    public PlayField(LevelType type, int levelNumber) {
        fields = Level.generateLevel(type, levelNumber);

        player = new Player(1, 1);
        fields.get(1).set(1, player);

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (walking == Direction.fromKeyCode(e.getKeyCode())) {
                    walking = null;
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                player.say("");

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        Game.changeState(GameState.PAUSED);
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_RIGHT:
                        walking = Direction.fromKeyCode(e.getKeyCode());
                        break;
                    case KeyEvent.VK_SPACE:
                        cut();
                        break;
                }
            }
        });

        if (imageBackpack == null || imageAxe == null) loadImages();
    }

    /**
     * Loads the images required to paint the backpack on screen.
     */
    private static void loadImages() {
        try {
            imageBackpack = ImageIO.read(PlayField.class.getResource("/img/backpack-icon.png"));
            imageAxe = ImageIO.read(PlayField.class.getResource("/img/axes.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Try to cut the tree the player is looking at.
     */
    private void cut() {
        int x = player.xPos + player.getDirection().getDx();
        int y = player.yPos + player.getDirection().getDy();

        if (!(fields.get(x).get(y) instanceof Tree)) return;

        Tree tree = (Tree) fields.get(x).get(y);
        if (!tree.isSolid() || tree.isBeingCut()) return;

        if (tree.cut(player.getAxe())) {
            Game.loadSound("chopping.wav");
        } else {
            player.say("I need a " + tree.getColor() + " axe to cut this tree");
        }
    }

    /**
     * Let the player walk in the given direction.
     */
    private void walk(Direction direction) {
        if (player.isMoving()) return;

        player.changeDirection(direction);

        int x = player.xPos;
        int y = player.yPos;

        int dx = direction.getDx();
        int dy = direction.getDy();

        if (fields.get(x + dx).get(y + dy).isSolid()) return;

        if (!player.move(dx, dy)) return;

        if (fields.get(x + dx).get(y + dy) instanceof Lumberaxe) {
            player.grabLumberaxe((Lumberaxe) fields.get(x + dx).get(y + dy));
            Game.loadSound("grab.wav");
        }

        if (fields.get(x + dx).get(y + dy) instanceof Finish) {
            Game.loadSound("winning.wav");
            Game.changeState(GameState.FINISHED);
            finished = true;
            fields.get(x).set(y, new Field(x, y));

            return;
        }

        fields.get(x).set(y, new Field(x, y));
        fields.get(x + dx).set(y + dy, player);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (walking != null) walk(walking);

        for (ArrayList<Field> row : fields) {
            for (Field field : row) {
                // Paint the player last.
                if (field instanceof Player) continue;

                field.paint(g);
            }
        }

        paintBackpack(g);
        if (!finished) player.paint(g);
    }

    /**
     * Paint the player's backpack in the upper right corner.
     */
    private void paintBackpack(Graphics g) {
        int x = (fields.size() - 1) * Field.SIZE;
        int y = 0;

        g.drawImage(imageBackpack, x, y, null);

        if (player.getAxe() != null) {
            int offset = player.getAxe().getColor().ordinal() * Field.SIZE;

            g.drawImage(
                    imageAxe, // Source image
                    x, y, x + Field.SIZE, y + Field.SIZE, // Destination position
                    offset, 0, offset + Field.SIZE, Field.SIZE, // Source position
                    null
            );
        }
    }
}
