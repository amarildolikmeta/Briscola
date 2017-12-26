package it.polimi.ma.group07.briscola.controller.persistance;

import java.util.ArrayList;

/**
 * Interface the Data persistance layer has to implement
 */

public interface DataRepository {
    public void saveLocalGame(LocalGame game);

    public void saveOnlineGame(OnlineGame game);

    public ArrayList<LocalGame> findAllLocalGames();

    public ArrayList<OnlineGame> findAllOnlineGames();

    public ArrayList<LocalGame> findAllLocalGames(String state);

    public ArrayList<OnlineGame> findAllOnlineGames(String state);

    public int getNrGames();
    public int getNrGames(String state);
    public int getNrOnlineGames();
    public int getNrOnlineGames(String state);
    public int getNrLocalGames();
    public int getNrLocalGames(String state);
    public void saveCurrentGame(LocalGame game);
    public LocalGame getCurrentGame();

    void deleteCurrentGame();
}
