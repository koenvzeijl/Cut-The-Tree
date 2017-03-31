package cutthetree.menus;

import cutthetree.Game;
import cutthetree.GameFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Abstract class used to display a menu on screen.
 * <p>
 * Contains a unified way of displaying menu choices
 * and an easy interface for selecting an item.
 */
public abstract class Menu extends JComponent {
    protected static Image imageSound, imageNoSound;
    protected static Image imageFx, imageNoFx;
    protected static Font font;
    protected static Font fontSmall;

    protected final String[] choices;
    protected int selected = 0;
    protected Game game;

    protected Menu(Game game, String... choices) {
        this.game = game;
        this.choices = choices;

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                onKeyPress(e);
            }
        });

        if (imageSound == null || imageFx == null) loadImages();
        if (font == null) loadFont();
    }

    /**
     * Loads the images required to paint the sound controls on screen
     */
    private static void loadImages() {
        try {
            imageSound = ImageIO.read(Menu.class.getResource("/img/sound.png"));
            imageNoSound = ImageIO.read(Menu.class.getResource("/img/noSound.png"));
            imageFx = ImageIO.read(Menu.class.getResource("/img/fx.png"));
            imageNoFx = ImageIO.read(Menu.class.getResource("/img/noFx.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the font to display the menu choices in
     */
    private static void loadFont() {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, Menu.class.getResourceAsStream("/font/pokemon.ttf")).deriveFont(32f);
            fontSmall = font.deriveFont(12f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle a key press event on this menu.
     * <p>
     * Default behavior uses the up/down/enter keys but this can be overridden
     * for custom keys.
     */
    protected void onKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (selected > 0) selected--;
                break;
            case KeyEvent.VK_DOWN:
                if (selected < choices.length - 1) selected++;
                break;
            case KeyEvent.VK_ENTER:
                itemSelected(selected);
                break;
        }
    }

    /**
     * Add a sound toggler to this menu.
     * <p>
     * This only sets up the mouse listener, for the actual painting
     * of the icons see {@link #paintSoundToggles(Graphics, int, int)}
     */
    protected void enableSoundToggler(final int x, final int y) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getX() > x && e.getX() < x + 40 && e.getY() > y && e.getY() < y + 40) {
                    game.toggleSound();
                } else if (e.getX() > x + 45 && e.getX() < x + 85 && e.getY() > y && e.getY() < y + 40) {
                    game.toggleEffects();
                }
            }
        });
    }

    /**
     * Called when an item in this menu is selected
     *
     * @param index The index of the selected menu item
     */
    abstract void itemSelected(int index);

    @Override
    public abstract void paintComponent(Graphics g);

    /**
     * Paint the sound toggle icons on screen at the given coordinates
     * <p>
     * This only displays the sound toggle icons, for actual interaction
     * with the toggles, see {@link #enableSoundToggler(int, int)}
     *
     * @param g The graphics object to paint on
     * @param x The start x position to start painting on
     * @param y The y position to paint on
     */
    protected void paintSoundToggles(Graphics g, int x, int y) {
        g.drawImage(game.isSoundEnabled() ? imageSound : imageNoSound, x, y, null);
        g.drawImage(game.isEffectsEnabled() ? imageFx : imageNoFx, x + 45, y, null);
    }

    /**
     * Paint the set choices to the screen
     */
    protected void paintChoices(Graphics g) {
        g.setFont(font);

        for (int i = 0; i < choices.length; i++) {
            g.setColor(i == selected ? Color.RED : Color.WHITE);

            drawCentered(g, choices[i], 420 + (i * 32));
        }
    }

    /**
     * Paint the current version in the lower left corner of the screen
     */
    protected void paintVersion(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(fontSmall);
        g.drawString("Version: 1.0b", 0, 900);
    }

    /**
     * Draw the given string in aligned to the center of the screen
     */
    protected void drawCentered(Graphics g, String str, int y) {
        int width = g.getFontMetrics().stringWidth(str);

        g.drawString(str, GameFrame.FRAME_WIDTH / 2 - width / 2, y);
    }
}
