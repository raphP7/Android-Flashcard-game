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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Raph on 23/12/2016.
 */

public class AddOrEditCard extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String authority ;
    private long idEditCard=-1;
    private boolean modeEdit;

    private EditText titre;
    private EditText question;
    private EditText reponse;
    private TextView txtDate;
    private RadioGroup radioGroup;

    private RadioButton desactiver;
    private RadioButton tresfacile;
    private RadioButton facile;
    private RadioButton moyen;
    private RadioButton difficile;


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


        Button newValueButton = (Button) rootView.findViewById(R.id.newValueButton);
        TextView newValueTxt = (TextView) rootView.findViewById(R.id.newValueTxt);

        radioGroup = (RadioGroup) rootView.findViewById(R.id.Radiogroup);


        desactiver = (RadioButton) rootView.findViewById(R.id.Radiobutton0);
        tresfacile = (RadioButton) rootView.findViewById(R.id.Radiobutton1);
        facile = (RadioButton) rootView.findViewById(R.id.Radiobutton2);
        moyen = (RadioButton) rootView.findViewById(R.id.Radiobutton3);
        difficile = (RadioButton) rootView.findViewById(R.id.Radiobutton4);

        txtDate= (TextView) rootView.findViewById(R.id.textViewDate);

        TextView txtTitre= (TextView) rootView.findViewById(R.id.textView1);
        txtTitre.setText(R.string.theTitle);
        txtTitre.setVisibility(View.VISIBLE);

        TextView txtQuestion= (TextView) rootView.findViewById(R.id.textView2);
        txtQuestion.setText(R.string.theQuestion);
        txtQuestion.setVisibility(View.VISIBLE);

        TextView txtReponse= (TextView) rootView.findViewById(R.id.textView3);
        txtReponse.setText(R.string.theAnswer);
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
            newValueTxt.setText(getString(R.string.editCardIn)+((MainActivity)getActivity()).getidDeckName());
            newValueButton.setText(getString(R.string.EditTheCard));
        }else {
            newValueTxt.setText(getString(R.string.newCardIn)+((MainActivity)getActivity()).getidDeckName());
            doHint();
            newValueButton.setText(getString(R.string.CreateTheCard));

        }
        newValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ajouterOrEditer(v);
            }
        });

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
            Toast toast = Toast.makeText(getActivity(), getString(R.string.SelectDiskFirst), Toast.LENGTH_SHORT);
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
            Toast toast = Toast.makeText(getActivity(), getString(R.string.FillAllField), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        int dificulty=((MainActivity)getActivity()).getDificultyValue(radioGroup.getCheckedRadioButtonId());

        if(dificulty==-1){
            dificulty=0;
        }


        long valueDate= System.currentTimeMillis()-86400000;

        setTxtDate(valueDate);
        setDificulty(dificulty);

        ContentValues values = new ContentValues();
        values.put("title",strTitle);
        values.put("question",strQuestion);
        values.put("reponse",strReponse);
        values.put("niveau",dificulty);
        values.put("date",valueDate);

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

    public void setTxtDate(long date){
        if(date==0){
            txtDate.setText("NEVER");
        }else{
            txtDate.setText(DateFormat.getDateTimeInstance().format(new Date(date)).toString());
        }
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

    public void setDificulty(int dificulty){

        ((MainActivity)getActivity()).setCheckRadioGroup(radioGroup,dificulty);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri.Builder builder = (new Uri.Builder()).scheme("content")
                .authority(authority)
                .appendPath("card_table")
                .appendPath("deck");
        ContentUris.appendId(builder,((MainActivity)getActivity()).getIdDeckInUse());
        return new CursorLoader(getActivity(), builder.build(),
                new String[]{"_id", "title", "question", "reponse","niveau"},
                "deck_id=" + ((MainActivity)getActivity()).getIdDeckInUse()+" AND _id="+idEditCard, null, null);
    }

    public  void showNoEditQuestion(){
        Toast toast = Toast.makeText(getActivity(), getString(R.string.NoEditableQuestion), Toast.LENGTH_SHORT);
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
        int intDificulty;
        long date;

        textCard=cursor.getString(cursor.getColumnIndex("title"));
        textQuestion=cursor.getString(cursor.getColumnIndex("question"));
        textReponse=cursor.getString(cursor.getColumnIndex("reponse"));
        intDificulty=cursor.getInt(cursor.getColumnIndex("niveau"));
        date=cursor.getLong(cursor.getColumnIndex("date"));
        //idCARD=cursor.getInt(cursor.getColumnIndex("_id"));
        cursor.moveToNext();

        Log.d("IN EDIT CARD ->"," N:"+textCard+"| Q:"+textQuestion+"| A:"+textReponse+"| LVL:"+intDificulty);
        setQuestionTxt(textQuestion);
        setCardTxt(textCard);
        setReponse(textReponse);
        setDificulty(intDificulty);
        setTxtDate(date);
    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {


    }
}