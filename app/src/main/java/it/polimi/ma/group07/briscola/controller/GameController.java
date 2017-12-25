package it.polimi.ma.group07.briscola.controller;

import it.polimi.ma.group07.briscola.GameActivity;
import it.polimi.ma.group07.briscola.controller.persistance.DataRepository;
import it.polimi.ma.group07.briscola.model.PlayerState;

/**
 * Created by amari on 22-Nov-17.
 */

public interface GameController {
    PlayerState state = null;
    public void onPerformMove(final GameActivity activity, int index) ;

    PlayerState getState();
    void setState(PlayerState state);
    int getPlayerIndex();
    void finishGame(String reason);
    DataRepository getRepository();

    void onNewGame(GameActivity activity);
    void onMovePerformed(GameActivity activity);
    boolean isPlayable();

}
