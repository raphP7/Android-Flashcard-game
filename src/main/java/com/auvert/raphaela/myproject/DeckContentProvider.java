package com.auvert.raphaela.myproject;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Raph on 23/12/2016.
 */

public class DeckContentProvider extends ContentProvider {
    private static final String LOG = "DeckContentProvider";
    private String authority = "com.auvert.raphaela.DeckContentProvider";
    private DeckDB helper;

    private static final int DECK= 1;
    private static final int CARD = 2;
    private static final int DECK_TITLE = 3;
    private static final int ONE_CARD = 4;
    private static final int CARDS_OF_ONE_DECK = 5;
    private static final int ONE_DECK = 6;

    private final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    {
        matcher.addURI(authority, "deck_table", DECK);
        matcher.addURI(authority, "card_table", CARD);
        matcher.addURI(authority, "deck/title", DECK_TITLE);
        matcher.addURI(authority, "card_table/#", ONE_CARD);
        matcher.addURI(authority, "deck_table/#", ONE_DECK);
        matcher.addURI(authority, "card_table/deck/#", CARDS_OF_ONE_DECK);
    }

    public DeckContentProvider() {

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int code = matcher.match(uri);
        int i;
        long id;
        Log.d(LOG, "delete uri=" + uri.toString());
        switch (code) {
            case ONE_CARD:
                id = ContentUris.parseId(uri);
                i = db.delete("card_table", "_id=" + id, null);
                break;
            case ONE_DECK:
                id = ContentUris.parseId(uri);
                i = db.delete("card_table", "deck_id=" + id, null);
                i = db.delete("deck_table", "_id =" + id, null);
                break;
            default:
                throw new UnsupportedOperationException("This delete not yet implemented");
        }
        return i;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        int insertCount = 0;
        SQLiteDatabase db = helper.getWritableDatabase();
        int code = matcher.match(uri);
        //Log.d(LOG, "Uri=" + uri.toString());
        long id = 0;

        //Log.d(LOG, "val code=" + code);

        switch (code) {
            case CARD:
                db.beginTransaction();
                for(int i=0; i<values.length;i++){
                    if(values[i]!=null){
                        id = db.insert("card_table", null, values[i]);
                        if(id>0){
                            insertCount++;
                        }
                    }

                }
                db.setTransactionSuccessful();
                break;
            default:
                throw new UnsupportedOperationException("this insert not yet implemented");
        }
        db.endTransaction();
        return insertCount;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {


        SQLiteDatabase db = helper.getWritableDatabase();
        int code = matcher.match(uri);
        //Log.d(LOG, "Uri=" + uri.toString());
        long id = 0;
        Uri.Builder builder = new Uri.Builder();

        //Log.d(LOG, "val code=" + code);

        switch (code) {
            case DECK:
                id = db.insertOrThrow("deck_table", null, values);
                builder.appendPath("deck_table");
                break;
            case CARD:
                id = db.insert("card_table", null, values);
                builder.appendPath("card_table");
                break;
            default:
                throw new UnsupportedOperationException("this insert not yet implemented");
        }

        builder.authority(authority);

        builder = ContentUris.appendId(builder, id);
        return builder.build();
    }

    @Override
    public boolean onCreate() {
        helper= DeckDB.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        int code = matcher.match(uri);

        Cursor cursor;
        switch (code) {
            case DECK:
                cursor = db.query("deck_table", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case CARD:
                cursor = db.query("card_table", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case DECK_TITLE:
                cursor = db.rawQuery("SELECT deck_table._id as _id, nom, title " +
                                " FROM deck_table, book_table " +
                                " where deck_table._id = card_table.deck_id"
                        , selectionArgs);
                break;
            case CARDS_OF_ONE_DECK:
                long id = ContentUris.parseId(uri);
                Log.d("IN QUERY","sort "+sortOrder);
                Log.d(" DANS DB ->  ",""+id);

                cursor = db.query("card_table", new String[]{"_id", "title","question","reponse","niveau","date"},
                        selection, selectionArgs, null, null, sortOrder);

                break;
            default:
                Log.d("Uri provider =", uri.toString());
                throw new UnsupportedOperationException("this query is not yet implemented  " +
                        uri.toString());
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {



        SQLiteDatabase db = helper.getWritableDatabase();
        int code = matcher.match(uri);
        int i;
        long id;
        Log.d(LOG, "UPDATE uri=" + uri.toString());
        switch (code) {
            case ONE_CARD:
                id = ContentUris.parseId(uri);
                i=db.update("card_table",values," _id ="+id ,null);
                break;
            case ONE_DECK:
                id = ContentUris.parseId(uri);
                i=db.update("deck_table",values," _id ="+id ,null);
                break;
            default:
                throw new UnsupportedOperationException("This delete not yet implemented");
        }
        return i;
    }
}
