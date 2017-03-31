package cutthetree.menus;

import cutthetree.Game;
import cutthetree.GameState;

/**
 * The level select menu
 */
public class LevelSelectMenu extends StartMenu {
    private int currentChoice = 0;

    public LevelSelectMenu(Game game) {
        super(game, "Tutorial", "Easy", "Medium", "Hard", "Bonus", "Back");
    }

    @Override
    void itemSelected(int index) {
        if (index == choices.length - 1) {
            // Restore choice on back
            selected = currentChoice;
        } else {
            currentChoice = index;
            game.setDifficulty(index);
        }

        Game.changeState(GameState.START);
    }
}
