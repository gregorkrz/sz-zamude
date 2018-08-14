package com.szamude.szamude;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DisplayTimetableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_timetable);
        Intent intent = getIntent();
        String train = intent.getStringExtra("train");
        WebView wb = (WebView) findViewById(R.id.web);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        String date = (sdf.format(calendar.getTime()));
        //String date = calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
        final String url = "http://www.slo-zeleznice.si/sl/component/sz_timetable/?view=train&vl=" + train + "&da="+date+"T00:00:00&tmpl=component%&loadStyles=true&iframe=true&width=90%&height=90%";
        Button button = (Button)findViewById(R.id.copyurl);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("url", url);
                clipboard.setPrimaryClip(clip);

            }
        });
        wb.loadUrl(url);
    }
}
