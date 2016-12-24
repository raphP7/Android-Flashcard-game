package com.auvert.raphaela.myproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DeckDB extends SQLiteOpenHelper {
    private static int VERSION = 6;


    private static String DECK ="deck_table";
    private static String CARD ="card_table";

    private static String NOM ="nom";
    private static String TITLE="title";
    private static String QUESTION="question";
    private static String REPONSE="reponse";
    private static String ID="_id";
    private static String GAMEID=" game_id";

    private static String GAMESBD = "GamesBD.db";
    private static DeckDB instance;

    private String game_table = "create table "+DECK+" ( " +
            NOM+" varchar(30) not null, " +
            ID + " integer primary key " + ");";

    private String card_table = "create table "+CARD +" (" +
            TITLE + " varchar(50) not null, " +
            QUESTION + " varchar(50) not null ," +
            REPONSE + " varchar(50) not null ,"+
            GAMEID + " int references game_table , " +
            ID + " integer primary key " + ");";


    public static DeckDB getInstance(Context context){
        if( instance == null ){
            instance = new DeckDB(context);
        }
        return instance;
    }

    private DeckDB(Context context) {
        super(context, GAMESBD, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(game_table);
        db.execSQL(card_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists "+DECK);
            db.execSQL("drop table if exists "+CARD);
            onCreate(db);
        }
    }
}
