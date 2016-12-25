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


        View rootView = inflater.inflate(R.layout.layout_newdeck, container, false);

        Button newValueButton= (Button) rootView.findViewById(R.id.newDeckValueButton);
        TextView newValueTxt= (TextView) rootView.findViewById(R.id.newDeckValueTxt);
        EditText newValueEnter= (EditText) rootView.findViewById(R.id.newDeckValueEnter);

        newValueButton.setText(stringValueButton);
        newValueTxt.setText(stringValueTxt);
        newValueEnter.setHint(stringValueEnter);


        newValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDownload(v);
            }
        });

        return rootView;
    }
    public void doDownload(View view) {
        
    }

}
