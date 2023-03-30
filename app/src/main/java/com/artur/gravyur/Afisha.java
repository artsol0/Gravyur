package com.artur.gravyur;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;

public class Afisha extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acrivity_afisha);

        TextView tvMovieTitle = (TextView) findViewById(R.id.tvMovieTitle);
        tvMovieTitle.setText(getIntent().getStringExtra("movieTile"));                  // get data from intent and set it

        String urlImg = getIntent().getStringExtra("img");
        new SetImageTask((ImageView) findViewById(R.id.ivMoviePoster)).execute(urlImg);

        TextView tvGenre = (TextView) findViewById(R.id.tvGenre);
        tvGenre.setText(getIntent().getStringExtra("genre"));

        TextView tvTime = (TextView) findViewById(R.id.tvTime);
        tvTime.setText(getIntent().getStringExtra("time"));

        TextView tvAbout= (TextView) findViewById(R.id.tvAbout);
        tvAbout.setText(getIntent().getStringExtra("about"));

        Button btnBuy = (Button) findViewById(R.id.btnBuy);
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = getIntent().getStringExtra("link");                        // getting link of buying page
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));        // set new intent
                startActivity(browserIntent);                                                 // open page in browser
            }
        });

    }

    // Getting page
    public class SetImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public SetImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];                                        // get page link
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
