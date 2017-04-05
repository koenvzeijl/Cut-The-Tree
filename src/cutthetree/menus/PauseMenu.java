package cutthetree.menus;

import cutthetree.Game;
import cutthetree.GameState;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * The paused menu
 */
public class PauseMenu extends Menu {
    private static Image background;

    public PauseMenu(Game game) {
        super(game, "Continue", "Restart", "Exit");

        enableSoundToggler(168, 594);
        if (background == null) loadImage();
    }

    /**
     * Loads the images required to paint this menu on screen
     */
    private static void loadImage() {
        try {
            background = ImageIO.read(PauseMenu.class.getResource("/img/pauseScreen.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onKeyPress(KeyEvent e) {
        // Restore playing state on ESC, else delegate to parent.
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (Game.)
            Game.changeState(GameState.PLAYING);
            selected = 0;
        } else {
            super.onKeyPress(e);
        }
    }

    @Override
    void itemSelected(int index) {
        if (index == 0) {
            Game.changeState(GameState.PLAYING);
        } else if (index == 1) {
            game.restart();
        } else if (index == 2) {
            Game.changeState(GameState.START);
            Game.loadSound("opening.wav");
        }

        selected = 0;
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(background, 0, 0, null);

        paintSoundToggles(g, 168, 594);
        paintChoices(g);
    }
}
