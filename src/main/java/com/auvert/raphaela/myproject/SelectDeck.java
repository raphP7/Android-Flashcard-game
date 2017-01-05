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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;

/**
 * Created by Raph on 23/12/2016.
 */

public class SelectDeck extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String authority ;
    private SimpleCursorAdapter listAdapter;
    private ListView listView;
    private HashSet<String> listeNameDeck;

    public SelectDeck() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listeNameDeck=new HashSet<>();
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

        if(getActivity()!=null){
            if(getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)!=null){
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(rootView.getWindowToken(),0);
            }
        }

        Button CREATE = (Button) rootView.findViewById(R.id.buttonCREATE);
        Button EDIT = (Button) rootView.findViewById(R.id.buttonEDIT);
        Button DELETE = (Button) rootView.findViewById(R.id.buttonDELETE);
        Button DEFINE = (Button) rootView.findViewById(R.id.buttonDEFINE);
        DEFINE.setVisibility(View.VISIBLE);
        CREATE.setText(R.string.CreateNewDeck);
        EDIT.setText(R.string.EditSelectDeck);
        EDIT.setVisibility(View.GONE);


        if(((MainActivity) getActivity()).downloadInProgress){
            DELETE.setText("DONWLOAD IN PROGRESS , DELETE DISABLE");
        }else{
            DELETE.setText(R.string.DeleteSelectDeck);
            DELETE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    supprimerDeck(v);
                }
            });
        }

        listView = (ListView) rootView.findViewById(R.id.listageList);
        listView.setAdapter(listAdapter);

        DEFINE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDeck(v);
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


                  TextView c =(TextView) view;
                  String se=c.getText().toString();


                  if(listeNameDeck.contains(se)){
                      listeNameDeck.remove(se);
                  }else{
                      listeNameDeck.add(se);
                  }
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

        if(listeNameDeck==null){
            return;
        }

        String[] arr = listeNameDeck.toArray(new String[listeNameDeck.size()]);

        if (ids.length!=1 || arr.length!=1){
            Toast toast = Toast.makeText(getActivity(),getString(R.string.Select1AndOnly1)+" "+getString(R.string.Deck), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        String  itemValue = arr[0];

        long idSelected = ids[0];

        ((MainActivity)getActivity()).setidDeckName(itemValue);
        ((MainActivity)getActivity()).setIdDeckInUse(idSelected);


        Toast toast = Toast.makeText(getActivity(),getString(R.string.Deck)+" "+itemValue+" "+getString(R.string.Select), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        Fragment fragment = new Question();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        ((MainActivity)getActivity()).setDrawerItemCheck(2);

    }

    public void callAjouterDeck(View view) {

        Fragment fragment = new AjouterDeck();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("ajouterDeck").commit();

    }

    public void supprimerDeck(View view) {


        ((MainActivity)getActivity()).setidDeckName("");
        ((MainActivity)getActivity()).setIdDeckInUse(-1);

        ((MainActivity)getActivity()).supprimerFromList(listView,"deck_table",getString(R.string.Deck)+"");
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