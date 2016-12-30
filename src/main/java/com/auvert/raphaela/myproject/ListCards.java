package com.auvert.raphaela.myproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * Created by Raph on 23/12/2016.
 */

public class ListCards extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private String authority ;
    private SimpleCursorAdapter listAdapter;
    private ListView listView;

    public ListCards() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authority = getResources().getString(R.string.authority);

        listAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_checked, null,
                new String[]{"title","date"},
                new int[]{android.R.id.text1,}, 0);

        if(savedInstanceState!=null){
            LoaderManager manager = getLoaderManager();
            manager.initLoader(0, null, this);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_listage, container, false);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(),0);


        Button CREATE= (Button) rootView.findViewById(R.id.buttonCREATE);
        Button EDIT= (Button) rootView.findViewById(R.id.buttonEDIT);
        Button DELETE = (Button) rootView.findViewById(R.id.buttonDELETE);

        String createStr=getString(R.string.CreateNewCard)+" "+" IN "+((MainActivity) getActivity()).getidDeckName();
        CREATE.setText(createStr);
        EDIT.setText(R.string.EditSelectCard);
        DELETE.setText(R.string.DeleteSelectCard);

        CREATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAjouterCarte(v);
            }
        });

        EDIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEditCard(v);
            }
        });

        DELETE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supprimerCards(v);
            }
        });


        listView = (ListView) rootView.findViewById(R.id.listageList);

        listView.setAdapter(listAdapter);

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    public void callAjouterCarte(View view) {

        Fragment addCard=new AddOrEditCard();

        Bundle args = new Bundle();
        args.putBoolean("modeEdit", false);

        addCard.setArguments(args);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,addCard ).addToBackStack("selectCard").commit();

    }

    public void callEditCard(View view){

        long[] ids = listView.getCheckedItemIds();
        if (ids.length!=1){
            Toast toast = Toast.makeText(getActivity(), getString(R.string.Select1AndOnly1), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        long idSelected = ids[0];

        Fragment fragmentEdit=new AddOrEditCard();

        Bundle args = new Bundle();
        args.putBoolean("modeEdit", true);
        args.putLong("idEditCard",idSelected);

        fragmentEdit.setArguments(args);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,fragmentEdit ).addToBackStack("selectCard").commit();

    }

    public void supprimerCards(View view) {
        if(((MainActivity)getActivity()).supprimerFromList(listView,"card_table","CARD")){
            getLoaderManager().restartLoader(1, null, this);
        }

    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri.Builder builder = (new Uri.Builder()).scheme("content")
                .authority(authority)
                .appendPath("card_table")
                .appendPath("deck");

        ContentUris.appendId(builder,((MainActivity)getActivity()).getIdDeckInUse());

        return new CursorLoader(getActivity(), builder.build(),
                new String[]{"_id", "title","date"},
                "deck_id=" + ((MainActivity)getActivity()).getIdDeckInUse(), null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        listAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        listAdapter.swapCursor(null);
    }


}