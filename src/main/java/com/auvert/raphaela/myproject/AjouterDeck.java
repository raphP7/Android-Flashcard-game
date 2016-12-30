package com.auvert.raphaela.myproject;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Raph on 23/12/2016.
 */

public class AjouterDeck extends Fragment {

    private String authority ;

    private EditText newValueEnter;

    public AjouterDeck() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authority=getResources().getString(R.string.authority);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //getActivity().getFragmentManager().popBackStack("selectDeck", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        View rootView = inflater.inflate(R.layout.layout_newdeck, container, false);


        TextView newValueTxt= (TextView) rootView.findViewById(R.id.newDeckValueTxt);
        newValueEnter= (EditText) rootView.findViewById(R.id.newDeckValueEnter);
        newValueEnter.requestFocus();
        Button newValueButton= (Button) rootView.findViewById(R.id.newDeckValueButton);

        newValueButton.setText(getString(R.string.NewDeck));
        newValueTxt.setText(getString(R.string.CreateNewDeck));
        newValueEnter.setHint(getString(R.string.name));

        newValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ajouter(v);
            }
        });

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        return rootView;
    }

    public void ajouter(View view) {

        String n = newValueEnter.getText().toString();
        if(n.length()<1){
            Toast toast = Toast.makeText(getActivity(), getString(R.string.EnterDeckName), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        newValueEnter.getText().clear();
        long date= 0;//System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put("nom",n);
        values.put("time",date);
        ContentResolver resolver = getActivity().getContentResolver();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("deck_table");
        Uri uri = builder.build();


        try{
            uri = resolver.insert(uri,values);
        }catch (android.database.sqlite.SQLiteConstraintException e){
            Toast toast = Toast.makeText(getActivity(), getString(R.string.DeckNameAlExist), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);

            Toast toast = Toast.makeText(getActivity(),getString(R.string.NewDeck)+" "+n+" "+getString(R.string.Create), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            /*
            Fragment fragment = new SelectDeck();
            FragmentManager fragmentManager = getActivity().getFragmentManager();

            getActivity().getFragmentManager().beginTransaction().

            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            */
    }
}