package com.auvert.raphaela.myproject;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

public class AddOrEditCard extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String authority ;

    private String stringValueTxt="CREATE NEW CARD";

    private String stringValueButtonCREATE ="CREATE THE CARD";

    private String stringValueButtonEDIT ="EDIT THE CARD";

    private long idEditCard=-1;
    private boolean modeEdit;

    private EditText titre;

    private EditText question;

    private EditText reponse;


    public AddOrEditCard() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authority=getResources().getString(R.string.authority);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //getActivity().getFragmentManager().popBackStack("selectCard", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        View rootView = inflater.inflate(R.layout.layout_newvalue, container, false);

        titre= (EditText) rootView.findViewById(R.id.newValueEnter);
        titre.requestFocus();

        modeEdit=getArguments().getBoolean("modeEdit");
        if(modeEdit){
            idEditCard = getArguments().getLong("idEditCard");
        }


        Button newValueButton= (Button) rootView.findViewById(R.id.newValueButton);
        TextView newValueTxt= (TextView) rootView.findViewById(R.id.newValueTxt);

        ;


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


        if(modeEdit){
            newValueTxt.setText("EDIT CARD in "+((MainActivity)getActivity()).getidDeckName());
            newValueButton.setText(stringValueButtonEDIT);
        }else {
            newValueTxt.setText("NEW CARD in "+((MainActivity)getActivity()).getidDeckName());
            doHint();
            newValueButton.setText(stringValueButtonCREATE);

        }
        newValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ajouterOrEditer(v);
            }
        });

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        return rootView;
    }


    public void doHint(){
        titre.setHint("addition facille");
        question.setHint("2+2");
        reponse.setHint("4");
    }
    public void ajouterOrEditer(View view) {

        Log.d("dans EDITER avec id =", idEditCard + "");

        if(((MainActivity)getActivity()).getIdDeckInUse()<0){
            Toast toast = Toast.makeText(getActivity(),"SELECT A DECK FIRST", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            Fragment fragment = new SelectDeck();
            getActivity().getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

            return;
        }
        String strTitle = titre.getText().toString();
        String strQuestion = question.getText().toString();
        String strReponse = reponse.getText().toString();

        if(strTitle.length()<1 || strQuestion.length()<1 || strReponse.length()<1){
            Toast toast = Toast.makeText(getActivity(),"FILL ALL THE FIELD", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("title",strTitle);
        values.put("question",strQuestion);
        values.put("reponse",strReponse);
        values.put("deck_id",((MainActivity)getActivity()).getIdDeckInUse());
        ContentResolver resolver = getActivity().getContentResolver();

        if(resolver!=null){
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").
                    authority(authority)
                    .appendPath("card_table");

            if(modeEdit){
                Log.d("EDITER id =", idEditCard + "");
                ContentUris.appendId(builder, idEditCard);
            }
            Uri uri = builder.build();
            if(modeEdit){
                int res = resolver.update(uri, values, null,null);
                Log.d("result of EDIT=", res + "");
            }else{
                uri = resolver.insert(uri,values);
            }

        }

        String Strmode="CREATE";
        if(modeEdit){
            Strmode="EDIT";
        }

        Toast toast = Toast.makeText(getActivity(),"CARD <"+strTitle+"> "+Strmode+" in DECK "+((MainActivity)getActivity()).getidDeckName(), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        doHint();
    }

    public  void setCardTxt(String strCard){
        titre.setText(strCard);
    }

    public  void setQuestionTxt(String strQuestion){
        question.setText(strQuestion);
    }

    public void setReponse(String strReponse){
        reponse.setText(strReponse);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri.Builder builder = (new Uri.Builder()).scheme("content")
                .authority(authority)
                .appendPath("card_table")
                .appendPath("deck");
        ContentUris.appendId(builder,((MainActivity)getActivity()).getIdDeckInUse());
        return new CursorLoader(getActivity(), builder.build(),
                new String[]{"_id", "title", "question", "reponse"},
                "deck_id=" + ((MainActivity)getActivity()).getIdDeckInUse()+" AND _id="+idEditCard, null, null);
    }

    public  void showNoEditQuestion(){
        Toast toast = Toast.makeText(getActivity(),"NO EDITABLE QUESTION", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        if(!modeEdit){
            return;
        }
        int taille=cursor.getCount();

        if(taille!=1){
            showNoEditQuestion();
            return;
        }

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        cursor.moveToFirst();
        String textQuestion="";
        String textCard="";
        String textReponse="";

        Log.d("DANS QUESTION "," ALEATOIR TROUVER");
        textCard=cursor.getString(cursor.getColumnIndex("title"));
        textQuestion=cursor.getString(cursor.getColumnIndex("question"));
        textReponse=cursor.getString(cursor.getColumnIndex("reponse"));
        //idCARD=cursor.getInt(cursor.getColumnIndex("_id"));
        cursor.moveToNext();

        Log.d("DANS EDIT -> VAL",""+textCard+" "+textQuestion+" "+textReponse);
        setQuestionTxt(textQuestion);
        setCardTxt(textCard);
        setReponse(textReponse);
    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {


    }
}