package com.auvert.raphaela.myproject;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    //private SimpleCursorAdapter adapter;
    private SimpleCursorAdapter listAdapter;
    private ListView titre;

    private View selectedView;
    public SelectDeck() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authority = getResources().getString(R.string.authority);

        listAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, null,
                new String[]{"nom"},
                new int[]{android.R.id.text1}, 0);

        LoaderManager manager = getLoaderManager();
        manager.initLoader(0, null, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.layout_listage, container, false);

        if (selectedView != null) {
            selectedView.setBackgroundColor(Color.GRAY);
        }

        Button CREATE = (Button) rootView.findViewById(R.id.buttonCREATE);
        Button EDIT = (Button) rootView.findViewById(R.id.buttonEDIT);
        Button DELETE = (Button) rootView.findViewById(R.id.buttonDELETE);

        CREATE.setText("CREATE new DECK");
        EDIT.setText("EDIT selected DECK");
        DELETE.setText("DELETE selected DECK");


        titre = (ListView) rootView.findViewById(R.id.listageList);

        titre.setAdapter(listAdapter);

        CREATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAjouterDeck(v);
            }
        });

        ListView lv = (ListView) rootView.findViewById(R.id.listageList);

        lv.setAdapter(listAdapter);

        lv.setOnItemClickListener(new
          AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent,
                                      View view,
                                      int position, long id) {
                  //String item = listAdapter.getItem(position);
                  Toast.makeText(getActivity(), "DONE",
                          Toast.LENGTH_SHORT).show();
                  if (selectedView != null) {
                      selectedView.setBackgroundColor(Color.WHITE);
                  }
                  selectedView = view;
                  view.setBackgroundColor(Color.GRAY);
                  //listAdapter.remove(item);
                  listAdapter.notifyDataSetChanged();
              }
          });

        return rootView;
    }

    public void callAjouterDeck(View view) {
        System.out.println("DANS onclick call Ajouter Deck");

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