package com.auvert.raphaela.myproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * Created by Raph on 23/12/2016.
 */

public class SelectDeck extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String authority ;
    private SimpleCursorAdapter listAdapter;
    private ListView listView;

    public SelectDeck() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authority = getResources().getString(R.string.authority);

        listAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_checked, null,
                new String[]{"nom"},
                new int[]{android.R.id.text1}, 0);

        LoaderManager manager = getLoaderManager();
        manager.initLoader(0, null, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.layout_listage, container, false);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(),0);

        Button CREATE = (Button) rootView.findViewById(R.id.buttonCREATE);
        Button EDIT = (Button) rootView.findViewById(R.id.buttonEDIT);
        Button DELETE = (Button) rootView.findViewById(R.id.buttonDELETE);
        Button DEFINE = (Button) rootView.findViewById(R.id.buttonDEFINE);
        DEFINE.setVisibility(View.VISIBLE);

        CREATE.setText("CREATE new DECK");
        EDIT.setText("EDIT selected DECK");
        DELETE.setText("DELETE selected DECK");


        listView = (ListView) rootView.findViewById(R.id.listageList);

        listView.setAdapter(listAdapter);

        DEFINE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDeck(v);
            }
        });

        DELETE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supprimerDeck(v);
            }
        });

        CREATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAjouterDeck(v);
            }
        });

        listView.setOnItemClickListener(new
          AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent,
                                      View view,
                                      int position, long id) {
                  listAdapter.notifyDataSetChanged();
              }
          });
        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }


    public void selectDeck(View view){
        long[] ids = listView.getCheckedItemIds();
        if (ids.length!=1){
            Toast toast = Toast.makeText(getActivity(),"SELECT ONE and ONLY ONE DECK ", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        String nameDeck="";
        int position=-1;


        SparseBooleanArray checked = listView.getCheckedItemPositions();

        for (int i = 0; i < checked.size(); i++) {
            if(checked.valueAt(i) == true) {
                Log.d("POSITION SELECT ",""+i);
                position=i;
            }
        }

        Cursor cursor=(Cursor) listView.getItemAtPosition(position);
        cursor.moveToPosition(position);
        nameDeck = cursor.getString(cursor.getColumnIndex("nom"));

        long idSelected = ids[0];

        ((MainActivity)getActivity()).setidDeckName(nameDeck);
        ((MainActivity)getActivity()).setIdDeckInUse(idSelected);


        Toast toast = Toast.makeText(getActivity(),"DECK <"+nameDeck+"> SELECT", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        Fragment fragment = new Question();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

    }

    public void callAjouterDeck(View view) {

        Fragment fragment = new AjouterDeck();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("ajouterDeck").commit();

    }

    public void supprimerDeck(View view) {

        ((MainActivity)getActivity()).supprimerFromList(listView,"deck_table","DECK");
        getLoaderManager().restartLoader(1, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content")
                .authority(authority)
                .appendPath("deck_table")
                .build();

        return new CursorLoader(getActivity(), uri, new String[]{"_id", "nom"},
                null, null, null);
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