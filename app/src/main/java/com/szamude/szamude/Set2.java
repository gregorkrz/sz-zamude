package com.szamude.szamude;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Set2 extends AppCompatActivity {

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
        setContentView(R.layout.activity_set2);
        final LinearLayout linl = (LinearLayout) findViewById(R.id.lla);
        List<String> abos = new ArrayList<String>(getAbos());
        for(int x=0;x<abos.size();x++) {
            addTrain(abos.get(x));
        }

        final SharedPreferences prefs = getSharedPreferences("szamude-trains",MODE_PRIVATE);
        boolean chk = prefs.getBoolean("chk",true);
        CheckBox cb =  (CheckBox) findViewById(R.id.checkBox);
        cb.setChecked(chk);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor edit = getSharedPreferences("szamude-trains",MODE_PRIVATE).edit();
                edit.putBoolean("chk",b);
                edit.apply();
            }
        });


        Button addnew = (Button) findViewById(R.id.addnew);
        addnew.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Set2.this);
                dialog.setContentView(R.layout.addnewlayout);
                dialog.setTitle("Številka vlaka");


                Button noBt = (Button) dialog.findViewById(R.id.btn_cancel);
                noBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Preklici, nic ne naredi
                        dialog.dismiss();
                    }
                });
                Button YeBt = (Button) dialog.findViewById(R.id.btn_save);
                // if button is clicked, close the custom dialog
                YeBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Shrani vlak
                        EditText trainNo = (EditText) dialog.findViewById(R.id.trainNumber);
                        String trainID = trainNo.getText().toString();
                        if(trainID != "" && trainID != "0") {
                            List<String> abos = new ArrayList<String>(getAbos());
                            boolean exists = false;

                            for(int x=0;x<abos.size();x++) {
                                    if(abos.get(x) == trainID) {
                                        exists = true;
                                        break;
                                    }
                            }

                            if(exists) {
                                // Train already exists

                            }
                            else {
                                addTrain(trainID);
                                SharedPreferences.Editor edit = getSharedPreferences("szamude-trains",MODE_PRIVATE).edit();
                                abos.add(trainID);
                                edit.putStringSet("abos",new HashSet<String>(abos));
                                edit.apply();

                            }
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

}

    public void addTrain(final String train) {
        final LinearLayout linl = (LinearLayout)findViewById(R.id.lla);
        final LinearLayout l = new LinearLayout(Set2.this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER);
        TextView tw = new TextView(this);
        ImageButton button = new ImageButton(this);
        button.setImageResource(R.drawable.ic_delete_black_24dp);
        //button.setText("X");
        //button.setWidth(50);
        //button.setHeight(50);
        tw.setText("Vlak " + train + "    ");
        tw.setTextColor(Color.BLACK);
        //button.setGravity(Gravity.CENTER);
        l.addView(tw);
        l.addView(button);
        linl.addView(l);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                linl.removeView(l);
                SharedPreferences.Editor edit = getSharedPreferences("szamude-trains",MODE_PRIVATE).edit();
                List<String> abos = new ArrayList<String>(getAbos());
                for(int x=0;x<abos.size();x++) {
                    if(abos.get(x) == train) {
                        abos.remove(x);
                    }
                }
                edit.putStringSet("abos",new HashSet<String>(abos));
                edit.apply();
            }
        });
    }
}
