package com.szamude.szamude;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Timetables extends AppCompatActivity {
    public Set<String> getAbos() {
        final SharedPreferences prefs = getSharedPreferences("szamude-trains", MODE_PRIVATE);
        Set<String> abos = prefs.getStringSet("abos",null);
        if(abos != null) {
            return abos;
        }
        return new HashSet<String>();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetables);
        final LinearLayout linl = (LinearLayout) findViewById(R.id.choose);
        List<String> abos = new ArrayList<String>(getAbos());
        for(int x=0;x<abos.size();x++) {
            addTrain(abos.get(x));
        }



        Button button = new Button(this);
        button.setText("poišči vlak");
        button.setGravity(Gravity.CENTER);
        linl.addView(button);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // open timetable
                final Dialog dialog = new Dialog(Timetables.this);
                dialog.setContentView(R.layout.findtrain);
                dialog.setTitle("Številka vlaka");
                Button YeBt = (Button) dialog.findViewById(R.id.btnok);
                // if button is clicked, close the custom dialog
                YeBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Shrani vlak
                        EditText trainNo = (EditText) dialog.findViewById(R.id.trainNumber);
                        String trainID = trainNo.getText().toString();
                        if(trainID != "" && trainID != "0") {
                            Intent intent = new Intent(Timetables.this, DisplayTimetableActivity.class);
                            intent.putExtra("train",trainID);
                            startActivity(intent);
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    public void addTrain(final String train) {
        final LinearLayout linl = (LinearLayout)findViewById(R.id.choose);

        Button button = new Button(this);
        button.setText(train);
        //button.setText("X");
        //button.setWidth(50);
        //button.setHeight(50);
        button.setGravity(Gravity.CENTER);
        linl.addView(button);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // open timetable
                Intent intent = new Intent(Timetables.this, DisplayTimetableActivity.class);
                intent.putExtra("train",train);
                startActivity(intent);
            }
        });
    }
}
