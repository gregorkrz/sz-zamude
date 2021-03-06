package com.szamude.szamude;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainAct2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this,bkgService.class));

        setContentView(R.layout.activity_main_act2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        refresh(findViewById(android.R.id.content));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                refresh(view);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    public Set<String> getAbos() {
        final SharedPreferences prefs = getSharedPreferences("szamude-trains", MODE_PRIVATE);
        Set<String> abos = prefs.getStringSet("abos",null);
        if(abos != null) {
            return abos;
        }
        return new HashSet<String>();
    }
     void refresh(final View view){
        Parser parser = new Parser();
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        final SharedPreferences chk = getSharedPreferences("szamude-trains",MODE_PRIVATE);
        final boolean abosOnly = chk.getBoolean("chk",true);

        parser.execute("http://91.209.49.136/Web/RSS.aspx?lang=en");
        parser.onFinish(new Parser.OnTaskCompleted() {

            @Override
            public void onTaskCompleted(ArrayList<Article> list) {
                progress.dismiss();
                //what to do when the parsing is done
                //the Array List contains all article's data. For example you can use it for your adapter.
                List<String> abos = new ArrayList<String>(getAbos());
                clearAllCards();
                boolean addNothing = true;
                for (int x=0;x<list.size();x++) {
                    if(abosOnly){
                        for(int y=0;y<abos.size();y++) {
                            if((list.get(x).getDescription().contains("train "+abos.get(y)+" "))) {
                                addCard(list.get(x).getDescription());
                                addNothing = false;
                            }
                        }
                    }
                    else {
                        addCard(list.get(x).getDescription());
                        addNothing = false;
                    }
                }
                if(addNothing == true) {
                   noDelays();
                }
            }

            @Override
            public void onError() {
                progress.dismiss();
                Snackbar.make(view, "Napaka pri osveževanju podatkov", Snackbar.LENGTH_LONG)
                        .setAction("error", null).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void addCard(String name) {

        CardView b = new CardView(getApplicationContext());
        b.setContentPadding(0,15,0,15);
// sets width to wrap content and height to 10 dp ->
        View child = getLayoutInflater().inflate(R.layout.delayinfo, null);
        TextView t = child.findViewById(R.id.tno1);
        String w = name.substring(name.indexOf("the train ")+10,name.indexOf(" ",name.indexOf("the train ")+10));
        t.setText(w);
        t = child.findViewById(R.id.sta1);
        w = name.substring(name.indexOf("on station ")+11,name.indexOf(" - ", name.indexOf("on station ")+11));
        t.setText(w);
        t = child.findViewById(R.id.del1);
        w=name.substring(name.indexOf(" - ",name.indexOf("on station ")+11)+3,name.indexOf(" min",name.indexOf(" - ",name.indexOf("on station ")+10)))+"'";
        if(w.length()>5) t.setTextSize(10);
        t.setText(w);
        b.addView(child);
        LinearLayout lay = (LinearLayout) findViewById(R.id.linear);
        lay.addView(b);

    }

    public void noDelays() {

        CardView b = new CardView(getApplicationContext());
        b.setContentPadding(0,15,0,15);
        View child = getLayoutInflater().inflate(R.layout.nodelays, null);
        b.addView(child);
        LinearLayout lay = (LinearLayout) findViewById(R.id.linear);
        lay.addView(b);

    }
    public void clearAllCards() {
        LinearLayout lay = (LinearLayout) findViewById(R.id.linear);
        if(lay.getChildCount() > 0) {
            lay.removeAllViews();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_act2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,Set2.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_timetable) {
            Intent intent = new Intent(this,Timetables.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
           Intent intent = new Intent(this,Set2.class);
           startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
