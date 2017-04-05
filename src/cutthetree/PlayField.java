package cutthetree;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
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
    private static Image imageCoin;
    private static Font font;

    private boolean finished = false;
    private int animationFrame = 0;
    private int coinCounter = 0;

    private Player player;
    private long start = System.currentTimeMillis();

    private int timeLeft = 60;
    private LevelType levelType;
    private Direction walking = null;
    private ArrayList<ArrayList<Field>> fields = new ArrayList<>();
    private long time = System.currentTimeMillis() + 61000;

    public PlayField(LevelType type, int levelNumber) {
        fields = Level.generateLevel(type, levelNumber);
        levelType = type;
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
        if (font == null) loadFont();
    }

    /**
     * Loads the images required to paint the backpack on screen.
     */
    private static void loadImages() {
        try {
            imageBackpack = ImageIO.read(PlayField.class.getResource("/img/backpack-icon.png"));
            imageAxe = ImageIO.read(PlayField.class.getResource("/img/axes.png"));
            imageCoin = ImageIO.read(PlayField.class.getResource("/img/coinSprite.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadFont() {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, Menu.class.getResourceAsStream("/font/pokemon.ttf")).deriveFont(32f);
        } catch (Exception e) {
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
            if(levelType == LevelType.BONUS && Game.getState() != GameState.BONUS){
                Game.changeState(GameState.BONUS);
                fields = Level.generateLevel(LevelType.BONUS, 1);
                levelType = LevelType.BONUS;
                player = new Player(1, 1);
                fields.get(1).set(1, player);
                setCoins();
            }else{
                Game.changeState(GameState.FINISHED);
                finished = true;
                fields.get(x).set(y, new Field(x, y));
            }
            return;
        }
        if (Game.getState() == GameState.BONUS){
            Field field = fields.get(x).get(y);
            if (field.hasCoin()){
                field.setCoin(false);
                coinCounter++;
            }
        }
        fields.get(x).set(y, new Field(x, y));
        fields.get(x + dx).set(y + dy, player);
    }

    private void setCoins(){
        for (ArrayList<Field> row : fields) {
            for (Field field : row) {
                // Paint the player last.
                if (field.getClass()  == Field.class){
                    field.setCoin(true);
                }

            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (walking != null) walk(walking);

        for (ArrayList<Field> row : fields) {
            for (Field field : row) {
                // Paint the player last.
                if (field instanceof Player) continue;
                field.paint(g);
                if (Game.getState() == GameState.BONUS && field.hasCoin()){
                    if (field.getClass()  == Field.class){
                        paintCoin(field.xPos, field.yPos,g);
                    }
                }
            }
        }

        paintBackpack(g);
        if (levelType == LevelType.BONUS) paintTimer(g);
        if (!finished) player.paint(g);
    }

    private void paintCoin(int x, int y, Graphics g) {
        long diff = System.currentTimeMillis() - start;
        x *= Field.SIZE;
        y *= Field.SIZE;
        if (diff > 120) {
            start = System.currentTimeMillis();

            animationFrame = (animationFrame + 1) % 6;
        }

        int offset = animationFrame * 75;


        g.drawImage(
                imageCoin, // Source image
                x, y, x + Field.SIZE, y + Field.SIZE, // Destination position
                offset, 0, offset + Field.SIZE, Field.SIZE, // Source position
                null
        );
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

    private void paintTimer(Graphics g) {
        int x = 10;
        int y = 50;

        g.setFont(font);
        g.setColor(Color.white);
        if (Game.getState() == GameState.PAUSED || Game.getState() == GameState.FINISHED) {
            time = timeLeft * 1000 + System.currentTimeMillis();
        } else {
            timeLeft = (int) ((time - System.currentTimeMillis())/1000);
        }
        g.drawString(String.valueOf(timeLeft), x, y);
        if (timeLeft == 0){
            Game.changeState(GameState.FINISHED);
            finished = true;
            fields.get(player.xPos).set(player.yPos, new Field(player.xPos, player.yPos));
        }
    }
}
