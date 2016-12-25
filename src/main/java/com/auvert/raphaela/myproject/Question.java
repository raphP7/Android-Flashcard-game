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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by Raph on 23/12/2016.
 */

public class Question extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    public TextView txtDeckAndCard;
    public TextView txtQuestion;
    public EditText reponseUtilisateur;
    public Button voirReponse;
    public String reponseOfCard;
    public String authority;

    public ProgressBar progressBar;
    public int idCARD;


    @Override
    public void onResume()
    {
        super.onResume();

        getLoaderManager().restartLoader(1, null, this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authority=getResources().getString(R.string.authority);
        LoaderManager manager = getLoaderManager();
        manager.initLoader(1, null, this);
    }

    public Question() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_question, container, false);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, this).commit();

        //String planet = getResources().getStringArray(R.array.menu_array)[i];

            /*

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                            "drawable", getActivity().getPackageName());

            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle(planet);

            */

        txtDeckAndCard= (TextView ) rootView.findViewById(R.id.deckAndCarTitle);

        progressBar =(ProgressBar) rootView.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        txtQuestion= (TextView ) rootView.findViewById(R.id.laQuestion);

        reponseUtilisateur =(EditText) rootView.findViewById(R.id.txtreponse);
        reponseUtilisateur.requestFocus();

        voirReponse= (Button) rootView.findViewById(R.id.voirReponse);

        voirReponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callReponse(v);
            }
        });

        return  rootView;

    }




    public void callReponse(View view) {

        if(reponseOfCard==null || reponseOfCard.length()==0){
            showNOQUESTION();
            return;
        }

        Fragment fragment = new Reponse();

        Bundle args = new Bundle();
        args.putLong("idCard", this.idCARD);

        String n = reponseUtilisateur.getText().toString();
        if (n.length() > 0 && reponseOfCard.equals(n)) {

            args.putBoolean("reponse", true);

        }
        else{
            args.putBoolean("reponse",false );
        }

        args.putString("theReponse",reponseOfCard);


        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);

        fragment.setArguments(args);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

    }

    public  void setCardTxt(String strCard){
        txtDeckAndCard.setText("Deck : "+((MainActivity)getActivity()).getIdDeckInUse()+" | CARD : "+strCard);
    }

    public  void setQuestionTxt(String strQuestion){
        txtQuestion.setText(strQuestion);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri.Builder builder = (new Uri.Builder()).scheme("content")
                .authority(authority)
                .appendPath("card_table")
                .appendPath("deck");
        ContentUris.appendId(builder,((MainActivity)getActivity()).getIdDeckInUse());
        return new CursorLoader(getActivity(), builder.build(),
                new String[]{"_id", "title", "question", "reponse"},
                "deck_id=" + ((MainActivity)getActivity()).getIdDeckInUse(), null, null);
    }

    public  void showNOQUESTION(){
        Toast toast = Toast.makeText(getActivity(),"NO AVAILABLE QUESTION", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        int taille=cursor.getCount();

        if(taille<1){
            showNOQUESTION();
            return;
        }

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        Log.d("DANS QUESTION ","Taille cursor : "+taille);
        Random r = new Random();
        int Low = 0;
        int result = r.nextInt(taille-Low) + Low;

        Log.d("DANS QUESTION "," ALEATOIR CHOISIT : "+result);
        cursor.moveToFirst();
        String textQuestion="";
        String textCard="";
        int cmp=0;
        while (!cursor.isAfterLast()) {

            if(result==cmp){
                Log.d("DANS QUESTION "," ALEATOIR TROUVER");
                textCard=cursor.getString(cursor.getColumnIndex("title"));
                textQuestion=cursor.getString(cursor.getColumnIndex("question"));
                reponseOfCard=cursor.getString(cursor.getColumnIndex("reponse"));
                idCARD=cursor.getInt(cursor.getColumnIndex("_id"));

                Log.d("VAL PRISE ",""+textCard+" "+textQuestion+" "+reponseOfCard+" "+idCARD);
                break;
            }
            cmp++;
            cursor.moveToNext();
        }
        setQuestionTxt(textQuestion);
        setCardTxt(textCard);
    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {


    }

}
