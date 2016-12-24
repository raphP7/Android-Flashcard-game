package com.auvert.raphaela.myproject;

import android.app.Fragment;
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

public class Telecharger extends Fragment {

    private String stringValueButton="TELECHARGER";

    private String stringValueEnter="URL";

    private String stringValueTxt="TELECHARGER NEW DECK";

    public Telecharger() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.layout_newvalue, container, false);

        Button newValueButton= (Button) rootView.findViewById(R.id.newValueButton);
        TextView newValueTxt= (TextView) rootView.findViewById(R.id.newValueTxt);
        EditText newValueEnter= (EditText) rootView.findViewById(R.id.newValueEnter);

        newValueButton.setText(stringValueButton);
        newValueTxt.setText(stringValueTxt);
        newValueEnter.setText(stringValueEnter);


        newValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("DANS onclick download");
            }
        });

        return rootView;
    }


}
