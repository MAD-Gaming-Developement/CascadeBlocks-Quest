package dev.jam.bunnyblocks.models;

import dev.jam.bunnyblocks.presenter.GameModel;

public class GameModelFactory {

    public static GameModel newGameModel(GameType gameType) {
        switch (gameType) {
            case TETRIS:
                return new TetrisGameModel();
            default:
                return null;
        }
    }
}
