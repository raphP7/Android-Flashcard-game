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

public class AjouterCarte extends Fragment {

    private String authority ;

    private String stringValueTxt="CREATE NEW CARD";

    private String stringValueButton="CREATE THE CARD";

    private EditText titre;

    private EditText question;

    private EditText reponse;

    private int idDECK=-1;

    private boolean firsTime=true;

    public AjouterCarte() {
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

        titre= (EditText) rootView.findViewById(R.id.newValueEnter);


        Button newValueButton= (Button) rootView.findViewById(R.id.newValueButton);
        TextView newValueTxt= (TextView) rootView.findViewById(R.id.newValueTxt);
        newValueTxt.setText("NEW CARD");

        TextView txtTitre= (TextView) rootView.findViewById(R.id.textView1);
        txtTitre.setText("LE TITRE :");
        txtTitre.setVisibility(View.VISIBLE);

        TextView txtQuestion= (TextView) rootView.findViewById(R.id.textView2);
        txtQuestion.setText("LA QUESTION :");
        txtQuestion.setVisibility(View.VISIBLE);

        TextView txtReponse= (TextView) rootView.findViewById(R.id.textView3);
        txtReponse.setText("LA REPONSE");
        txtReponse.setVisibility(View.VISIBLE);

        View separator =(View) rootView.findViewById(R.id.separator);
        separator.setVisibility(View.VISIBLE);

        separator =(View) rootView.findViewById(R.id.separator2);
        separator.setVisibility(View.VISIBLE);


        question= (EditText) rootView.findViewById(R.id.newValueEnter2);
        question.setVisibility(View.VISIBLE);


        reponse= (EditText) rootView.findViewById(R.id.newValueEnter3);
        reponse.setVisibility(View.VISIBLE);



        newValueButton.setText(stringValueButton);
        newValueTxt.setText(stringValueTxt);

        doHint();

        newValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ajouter(v);
            }
        });

        return rootView;
    }


    public void doHint(){
        titre.setHint("addition facille");
        question.setHint("2+2");
        reponse.setHint("4");
    }
    public void ajouter(View view) {
        if(idDECK==-1){
            return;
        }
        String strTitle = titre.getText().toString();
        String strQuestion = question.getText().toString();
        String strReponse = reponse.getText().toString();

        ContentValues values = new ContentValues();
        values.put("nom",strTitle);
        values.put("question",strQuestion);
        values.put("reponse",strReponse);
        values.put("reponse",strReponse);
        ContentResolver resolver = getActivity().getContentResolver();

        if(resolver!=null){
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath("deck_table");
            Uri uri = builder.build();
            uri = resolver.insert(uri,values);
        }
        doHint();
    }
}