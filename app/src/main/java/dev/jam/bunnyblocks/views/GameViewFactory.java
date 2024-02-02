package dev.jam.bunnyblocks.views;

import android.widget.Button;
import android.widget.TextView;

import dev.jam.bunnyblocks.presenter.GameView;

public class GameViewFactory {

    public static GameView newGameView(GameFrame gameFrame, TextView gameScoreText, TextView gameStatusText, Button gameCtlBtn) {
        return new GameViewImpl(gameFrame, gameScoreText, gameStatusText, gameCtlBtn);
    }
}
