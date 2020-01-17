package com.example.distillery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.print.PrintAttributes;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.flexbox.FlexboxLayout;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    FlexboxLayout lin;
    EditText text;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lin = findViewById(R.id.lin);
        text = findViewById(R.id.search_text);
        btn = findViewById(R.id.search_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImagesParser().execute(text.getText().toString());
            }
        });
    }
    class ImagesParser extends AsyncTask<String, Bitmap, Void>{
        @Override
        protected Void doInBackground(String... q) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://www.google.com/search?tbm=isch&q="+q[0]).openConnection();
                BufferedReader reader = new BufferedReader((new InputStreamReader(connection.getInputStream())));
                String line;
                StringBuilder builder = new StringBuilder();
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }
                String a = builder.toString();
                Pattern baseImageParser = Pattern.compile("<img(.+?)>");
                Matcher baseImageParserMatcher = baseImageParser.matcher(a);
                Pattern getUrl = Pattern.compile("class=\"rg_i.+?\".+?data-iurl=\"(.+?)\"");
                Pattern getUrl2 = Pattern.compile("data-src=\"(.+?)\".+?class=\"rg_i.+?\"");
                while(baseImageParserMatcher.find()){
                    String g = baseImageParserMatcher.group(1);
                    Matcher getUrlMatcher = getUrl.matcher(g);
                    Matcher getUrlMatcher2 = getUrl2.matcher(g);
                    if(getUrlMatcher.find()) {
                        String u = getUrlMatcher.group(1);
                        URLConnection url = new URL(u).openConnection();
                        Bitmap bitmap = BitmapFactory.decodeStream(url.getInputStream());
                        publishProgress(bitmap);
                    }else if(getUrlMatcher2.find()){
                        String u = getUrlMatcher2.group(1);
                        URLConnection url = new URL(u).openConnection();
                        Bitmap bitmap = BitmapFactory.decodeStream(url.getInputStream());
                        publishProgress(bitmap);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            lin.removeAllViews();
            btn.setEnabled(false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            btn.setEnabled(true);
        }

        @Override
        protected void onProgressUpdate(final Bitmap... values) {
            ImageView image = new ImageView(lin.getContext());
            image.setImageBitmap(values[0]);
            lin.addView(image);
        }
    }
}
