package com.auvert.raphaela.myproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
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

    public  TextView timeShow;

    public ProgressBar progressBar;
    public int idCARD;

    public int dificulty;



    @Override
    public void onResume()
    {
        super.onResume();
        testPreference();
        Log.d("Question","onResume");
        getLoaderManager().restartLoader(1, null, this);
    }

    public boolean testPreference(){


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean activer=preferences.getBoolean("checkBoxTime",false);
        if(activer){
            ((MainActivity) getActivity() ).timeForQuestion=Integer.parseInt(preferences.getString("editTextTime"," "));
        }else{
            ((MainActivity) getActivity() ).timeForQuestion=-1;
        }
        Log.d("Question","TestPreference , retour :"+activer);
        return  activer;
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

        timeShow= (TextView ) rootView.findViewById(R.id.timeShow);

        progressBar =(ProgressBar) rootView.findViewById(R.id.progressBar);
        if(testPreference()){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(((MainActivity) getActivity() ).timeForQuestion);
            timeShow.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }



        txtDeckAndCard= (TextView ) rootView.findViewById(R.id.deckAndCarTitle);
        txtQuestion= (TextView ) rootView.findViewById(R.id.laQuestion);
        reponseUtilisateur =(EditText) rootView.findViewById(R.id.txtreponse);
        reponseUtilisateur.requestFocus();
        reponseUtilisateur.setVisibility(View.GONE);
        voirReponse= (Button) rootView.findViewById(R.id.voirReponse);
        voirReponse.setVisibility(View.GONE);
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
        args.putInt("niveau",dificulty);


        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);

        fragment.setArguments(args);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

    }

    public void setCardTxt(String strCard) {
        txtDeckAndCard.setText("" +
                getString(R.string.Deck) + ": " +
                ((MainActivity) getActivity()).getidDeckName() +
                " | " +
                getString(R.string.Card) +
                " " +
                strCard);
    }

    public  void setQuestionTxt(String strQuestion){
        txtQuestion.setText(strQuestion);
        reponseUtilisateur.setVisibility(View.VISIBLE);
        voirReponse.setVisibility(View.VISIBLE);

        final int time=((MainActivity) getActivity()).timeForQuestion;
        CountDownTimer countDownTimer = new CountDownTimer(time*1000,100) {
            private boolean warned = false;
            private int value=0;
            @Override
            public void onTick(long millisUntilFinished_) {
                int allowTime=(int) ((time-millisUntilFinished_)/time*100.0);
                progressBar.setProgress(allowTime);
                timeShow.setText("TIME : "+allowTime/5000);
            }

            @Override
            public void onFinish() {
                if(voirReponse!=null){
                    if(testPreference()){
                        voirReponse.performClick();
                    }
                }
            }
        };

        if(time!=-1){
            countDownTimer.start();
        }


    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri.Builder builder = (new Uri.Builder()).scheme("content")
                .authority(authority)
                .appendPath("card_table")
                .appendPath("deck");
        ContentUris.appendId(builder,((MainActivity)getActivity()).getIdDeckInUse());
        long date=System.currentTimeMillis();
        long milli=86400000;

        String requestData=" "+date+"> date + (niveau*"+milli+" )";


        return new CursorLoader(getActivity(), builder.build(),
                new String[]{"_id", "title", "question", "reponse","niveau"},
                "deck_id=" + ((MainActivity)getActivity()).getIdDeckInUse() +" AND niveau>0 AND "+requestData, null, null);
    }

    public  void showNOQUESTION(){
        Toast toast = Toast.makeText(getActivity(), getString(R.string.NoAvailableQuestion), Toast.LENGTH_SHORT);
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
                dificulty=cursor.getInt(cursor.getColumnIndex("niveau"));

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
