package it.polimi.ma.group07.briscola.controller.persistance;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import it.polimi.ma.group07.briscola.MainActivity;

/**
 * Persistence layer that uses {@link SQLiteDatabase} to save the data
 * abut the games played
 */

public class DatabaseRepository extends SQLiteOpenHelper implements DataRepository {
    private static final int DATABASE_VERSION= 1;
    private static final String DATABASE_NAME="Briscola";
    private static final String LOCAL_GAME_TABLE="LocalGame";
    private static final String ONLINE_GAME_TABLE="OnlineGame";
    private static final String RUNNING_GAME_TABLE ="RunningGame";
    /**
     * static reference to an instance of the repository
     */
    private static  DatabaseRepository instance;
    public DatabaseRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DataRepository getInstance() {
        if(instance==null)
            instance= new DatabaseRepository(MainActivity.context);
        return instance;
    }

    /**
     * Create the database
     * @param db reference to the SQLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query="CREATE TABLE "+LOCAL_GAME_TABLE+"(id INTEGER PRIMARY KEY AUTOINCREMENT,start VARCHAR(87),moves VARCHAR,state VARCHAR);";
        db.execSQL(query);
        query="CREATE TABLE "+ONLINE_GAME_TABLE+"(id INTEGER PRIMARY KEY AUTOINCREMENT,state VARCHAR);";
        db.execSQL(query);
        query="CREATE TABLE "+RUNNING_GAME_TABLE+"(id INTEGER PRIMARY KEY AUTOINCREMENT,start VARCHAR(87),moves VARCHAR)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Save a local game in the database
     * @param game object representation of the game to be saved
     */
    @Override
    public void saveLocalGame(LocalGame game) {
        String query="INSERT  INTO "+LOCAL_GAME_TABLE+"(start, moves, state) VALUES('";
        query+=game.startConfiguration+"','";
        query+=game.moves+"','";
        query+=game.state+"');";
        getWritableDatabase().execSQL(query);
    }
    /**
     * Save an online game in the database
     * @param game object representation of the game to be saved
     */
    @Override
    public void saveOnlineGame(OnlineGame game) {
        String query="INSERT  INTO "+ONLINE_GAME_TABLE+"(state) VALUES('";
        query+=game.state+"');";
        getWritableDatabase().execSQL(query);
    }

    /**
     *The following are all methods to save and/or retrieve
     * data about the games played
     */

    @Override
    public ArrayList<LocalGame> findAllLocalGames() {
        String query="SELECT start,moves,state FROM "+LOCAL_GAME_TABLE;
        Cursor cursor= getReadableDatabase().rawQuery(query, null);
        ArrayList<LocalGame> games=new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            games.add(new LocalGame(cursor.getString(0),cursor.getString(1),cursor.getString(2)));
            cursor.moveToNext();
        }
        return games;
    }

    @Override
    public ArrayList<OnlineGame> findAllOnlineGames() {
        String query="SELECT state FROM "+ONLINE_GAME_TABLE;
        Cursor cursor= getReadableDatabase().rawQuery(query, null);
        ArrayList<OnlineGame> games=new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            games.add(new OnlineGame(cursor.getString(0)));
            cursor.moveToNext();
        }
        return games;
    }

    @Override
    public ArrayList<LocalGame> findAllLocalGames(String state) {
        String query="SELECT start,moves,state FROM "+LOCAL_GAME_TABLE+" WHERE state='"+state+"'";
        Cursor cursor= getReadableDatabase().rawQuery(query, null);
        ArrayList<LocalGame> games=new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            games.add(new LocalGame(cursor.getString(0),cursor.getString(1),cursor.getString(2)));
            cursor.moveToNext();
        }
        return games;
    }

    @Override
    public ArrayList<OnlineGame> findAllOnlineGames(String state) {
        String query="SELECT state FROM "+ONLINE_GAME_TABLE+" WHERE state='"+state+"'";;
        Cursor cursor= getReadableDatabase().rawQuery(query, null);
        ArrayList<OnlineGame> games=new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            games.add(new OnlineGame(cursor.getString(0)));
            cursor.moveToNext();
        }
        return games;
    }

    @Override
    public int getNrGames() {
        return getNrLocalGames()+getNrOnlineGames();
    }

    @Override
    public int getNrGames(String state) {
        return getNrLocalGames(state)+getNrOnlineGames(state);
    }

    @Override
    public int getNrOnlineGames() {
        String query="SELECT count(*) FROM "+ONLINE_GAME_TABLE;
        try {
            Cursor c = getReadableDatabase().rawQuery(query, null);
            if (c != null && c.moveToFirst())
                return c.getInt(0);
        }
        catch (SQLiteException e){
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    @Override
    public int getNrOnlineGames(String state) {
        String query="SELECT count(*) FROM "+ONLINE_GAME_TABLE+" WHERE state LIKE '"+state+"';";
        try {
            Cursor c = getReadableDatabase().rawQuery(query, null);
            if (c != null && c.moveToFirst())
                return c.getInt(0);
        }
        catch (SQLiteException e){
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    @Override
    public int getNrLocalGames() {
        String query="SELECT count(*) FROM "+LOCAL_GAME_TABLE;
        try {
            Cursor c = getReadableDatabase().rawQuery(query, null);
            if (c != null && c.moveToFirst())
                return c.getInt(0);
        }
        catch (SQLiteException e){
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    @Override
    public int getNrLocalGames(String state) {
        String query="SELECT count(*) FROM "+LOCAL_GAME_TABLE+" WHERE state LIKE '"+state+"';";
        try {
            Cursor c = getReadableDatabase().rawQuery(query, null);
            if (c != null && c.moveToFirst())
                return c.getInt(0);
        }
        catch (SQLiteException e){
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    @Override
    public void saveCurrentGame(LocalGame game) {
        deleteCurrentGame();
        String query="INSERT  INTO "+RUNNING_GAME_TABLE+"(start, moves) VALUES('";
        query+=game.startConfiguration+"','";
        query+=game.moves+"');'";
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL(query);
        Log.i("Database","Saved Current Game");
    }

    @Override
    public LocalGame getCurrentGame() {
        String query="SELECT start,moves FROM "+RUNNING_GAME_TABLE+";";
        Cursor cursor= getReadableDatabase().rawQuery(query, null);
        Log.i("Database","LoadedCurrentGame");
        if(cursor.getCount()==0)
            return null;
        Log.i("Database","Returning Game");
        cursor.moveToFirst();
        return new LocalGame(cursor.getString(0),cursor.getString(1),"Running");
    }

    @Override
    public void deleteCurrentGame() {
        String query="DELETE from "+RUNNING_GAME_TABLE;
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL(query);
    }

}
