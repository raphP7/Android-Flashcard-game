package com.auvert.raphaela.myproject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Raph on 23/12/2016.
 */

public class Question extends Fragment {

    public static final String argQuestion = "planet_number";

    public Button voirReponse;

    public Question() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_question, container, false);
        int i = getArguments().getInt(argQuestion);
        //String planet = getResources().getStringArray(R.array.menu_array)[i];

            /*

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                            "drawable", getActivity().getPackageName());

            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle(planet);

            */


        voirReponse= (Button) rootView.findViewById(R.id.voirReponse);

        voirReponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callReponse(v);
            }
        });

        return rootView;
    }

    public void callReponse(View view) {
        System.out.println("DANS call VOIR REPONSE");

    }

}
