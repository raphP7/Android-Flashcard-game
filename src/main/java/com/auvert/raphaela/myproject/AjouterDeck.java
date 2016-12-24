package com.auvert.raphaela.myproject;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Raph on 23/12/2016.
 */

public class AjouterDeck extends Fragment {

    private String authority ;

    private String stringValueButton="NEW DECK";

    private String stringValueEnter="name";

    private String stringValueTxt="CREATE NEW DECK";

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

        View rootView = inflater.inflate(R.layout.layout_newvalue, container, false);

        Button newValueButton= (Button) rootView.findViewById(R.id.newValueButton);
        TextView newValueTxt= (TextView) rootView.findViewById(R.id.newValueTxt);
        newValueEnter= (EditText) rootView.findViewById(R.id.newValueEnter);

        newValueButton.setText(stringValueButton);
        newValueTxt.setText(stringValueTxt);
        newValueEnter.setText(stringValueEnter);

        newValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ajouter(v);
            }
        });

        return rootView;
    }

    public void ajouter(View view) {

        String n = newValueEnter.getText().toString();
        newValueEnter.getText().clear();
        ContentValues values = new ContentValues();
        values.put("nom",n);
        ContentResolver resolver = getActivity().getContentResolver();

        if(resolver!=null){
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath("deck_table");
            Uri uri = builder.build();
            uri = resolver.insert(uri,values);
        }
    }
}