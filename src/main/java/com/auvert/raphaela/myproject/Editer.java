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

public class Editer extends Fragment {
    public Editer() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_listage, container, false);

        Button CREATE= (Button) rootView.findViewById(R.id.buttonCREATE);
        Button EDIT= (Button) rootView.findViewById(R.id.buttonEDIT);
        Button DELETE = (Button) rootView.findViewById(R.id.buttonDELETE);

        CREATE.setText("CREATE new CARD");
        EDIT.setText("EDIT selected CARD");
        DELETE.setText("DELETE selected CARD");


        return rootView;
    }
}