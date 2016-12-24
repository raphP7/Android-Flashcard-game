package com.auvert.raphaela.myproject;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by Raph on 23/12/2016.
 */

public class Reponse extends Fragment {

    boolean reponse;
    String theReponse;
    RadioGroup radioButtonGroup;
    TextView goodORfalse;
    Button autreQuestion;

    public Reponse() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_reponse, container, false);
        reponse = getArguments().getBoolean("reponse");
        theReponse = getArguments().getString("theReponse");

        autreQuestion= (Button) rootView.findViewById(R.id.buttonNewQuestion);

        autreQuestion= (Button) rootView.findViewById(R.id.buttonNewQuestion);
        goodORfalse= (TextView) rootView.findViewById(R.id.goodORfalse);

        if(reponse){
            goodORfalse.setText("BONNE REPONSE");
            goodORfalse.setTextColor(Color.parseColor("#008000"));
        }else{
            goodORfalse.setText("MAUVAISE REPONSE");
            goodORfalse.setTextColor(Color.parseColor("#FF0000"));
        }


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

        System.out.println("DANS call autre QUESTION");
        int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
        RadioButton radioButton =(RadioButton) radioButtonGroup.findViewById(radioButtonID);

        if(radioButton!=null){
            int idx = radioButtonGroup.indexOfChild(radioButton);
            System.out.println("Radio select : "+radioButton.getText());
        }
    }


}
