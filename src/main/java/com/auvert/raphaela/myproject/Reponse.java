package com.auvert.raphaela.myproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by Raph on 23/12/2016.
 */

public class Reponse extends Fragment {

    private boolean reponse;
    private String theReponse;
    private RadioGroup radioButtonGroup;
    private TextView goodORfalse;
    private Button autreQuestion;
    private  int dificulty;
    private long idCard;
    private  RadioGroup radioGroup;
    private String authority ;

    public Reponse() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authority=getResources().getString(R.string.authority);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_reponse, container, false);

        idCard = getArguments().getLong("idCard");
        reponse = getArguments().getBoolean("reponse");
        theReponse = getArguments().getString("theReponse");
        dificulty = getArguments().getInt("niveau");

        radioGroup = (RadioGroup) rootView.findViewById(R.id.Radiogroup);


        autreQuestion= (Button) rootView.findViewById(R.id.buttonNewQuestion);
        autreQuestion= (Button) rootView.findViewById(R.id.buttonNewQuestion);
        goodORfalse= (TextView) rootView.findViewById(R.id.goodORfalse);

        if(reponse){
            goodORfalse.setText(R.string.GoodAnswer);
            goodORfalse.setTextColor(Color.parseColor("#008000"));
            dificulty++;
        }else{
            goodORfalse.setText(R.string.BasAnswer);
            goodORfalse.setTextColor(Color.parseColor("#FF0000"));
        }

        ((MainActivity)getActivity()).setCheckRadioGroup(radioGroup,dificulty);

        TextView TxtViewreponse= (TextView) rootView.findViewById(R.id.reponse);
        TxtViewreponse.setText(theReponse);
        radioButtonGroup = (RadioGroup) rootView.findViewById(R.id.Radiogroup);


        autreQuestion= (Button) rootView.findViewById(R.id.buttonNewQuestion);

        autreQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callQuestion(v);
            }
        });

        return rootView;
    }

    public void callQuestion(View view) {

        int dificulty2=((MainActivity)getActivity()).getDificultyValue(radioGroup.getCheckedRadioButtonId());


        long valueDate = System.currentTimeMillis();

        ContentValues valuesDeck = new ContentValues();
        valuesDeck.put("time",valueDate);

        ContentValues values = new ContentValues();
        //values.put("title",strTitle);
        //values.put("question",strQuestion);
        //values.put("reponse",strReponse);
        values.put("niveau", dificulty2);
        values.put("date", valueDate);

        long deckId= ((MainActivity) getActivity()).getIdDeckInUse();

        //values.put("deck_id", deckId);

        ContentResolver resolver = getActivity().getContentResolver();

        if (resolver != null) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").
                    authority(authority)
                    .appendPath("card_table");

            ContentUris.appendId(builder, idCard);
            Uri uri = builder.build();
            Log.d("EDITER id =", idCard + "");
            int res = resolver.update(uri, values, null, null);
            Log.d("result of EDIT=", res + "");

            Uri.Builder builderDeck = new Uri.Builder();
            builderDeck.scheme("content").
                    authority(authority).
                    appendPath("deck_table");
            ContentUris.appendId(builderDeck,deckId);
            Uri uriDeck = builderDeck.build();

            Log.d("EDITER DECK id =", deckId + "");
            res = resolver.update(uriDeck, valuesDeck, null, null);
            Log.d("result of EDIT=", res + "");

        }


        Fragment fragment = new Question();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

    }


}
