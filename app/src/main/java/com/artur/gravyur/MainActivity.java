package com.artur.gravyur;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public String cinema = "";
    public String date = "";
    public String movie = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Button btnSearch = (Button) findViewById(R.id.btnSearch);

        Button btnBayda = (Button) findViewById(R.id.btnBayda);
        Button btnKosmos = (Button) findViewById(R.id.btnKosmos);
        Button btnKinoMax = (Button) findViewById(R.id.btnKinoMax);
        Button btnBlockbuster = (Button) findViewById(R.id.btnBlockbuster);

        Button btnToday = (Button) findViewById(R.id.btnToday);
        Button btnTomorrow = (Button) findViewById(R.id.btnTomorrow);
        Button btnDayAfter = (Button) findViewById(R.id.btnDayAfter);

        // Data clicking + get date
        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClickDesign(btnToday);       // change design of clicked element
                setDefaultDesign(btnTomorrow);  // change design of other
                setDefaultDesign(btnDayAfter);

                date = getDate();               // getting date
            }
        });
        btnTomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClickDesign(btnTomorrow);
                setDefaultDesign(btnToday);
                setDefaultDesign(btnDayAfter);

                date = getDate(1);
            }
        });
        btnDayAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClickDesign(btnDayAfter);
                setDefaultDesign(btnToday);
                setDefaultDesign(btnTomorrow);

                date = getDate(2);
            }
        });

        // Cinema clicking + set cinema
        btnBayda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClickDesign(btnBayda);               // change design of clicked element
                setDefaultDesign(btnKosmos);            // change design of other
                setDefaultDesign(btnKinoMax);
                setDefaultDesign(btnBlockbuster);

                cinema = "bayda";                       // setting cinema
            }
        });
        btnKosmos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClickDesign(btnKosmos);
                setDefaultDesign(btnBayda);
                setDefaultDesign(btnKinoMax);
                setDefaultDesign(btnBlockbuster);

                cinema = "kosmos-zp";
            }
        });
        btnKinoMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClickDesign(btnKinoMax);
                setDefaultDesign(btnKosmos);
                setDefaultDesign(btnBayda);
                setDefaultDesign(btnBlockbuster);

                cinema = "kinomax";
            }
        });
        btnBlockbuster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClickDesign(btnBlockbuster);
                setDefaultDesign(btnKosmos);
                setDefaultDesign(btnKinoMax);
                setDefaultDesign(btnBayda);

                cinema = "blockbuster-zp";
            }
        });

        //Start parsing
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText edFilmTitle = (EditText) findViewById(R.id.edFilmTitle);
                movie = edFilmTitle.getText().toString();                       // getting movie title

                if (movie.isEmpty() || cinema.equals("") || date.equals("")) {  // check if user provide all data
                    Toast.makeText(getBaseContext(),getResources().getString(R.string.error_message_input), Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    new searchThread().execute();
                }

            }
        });
    }

    // Parsing
    public class searchThread extends AsyncTask<String, Void, String> {

        TextView tvResult = (TextView) findViewById(R.id.tvResult);                 // provide result here
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);     // start rolling progressbar

        ArrayList<String> moviesTitle = new ArrayList<>();                          // array of movie titles
        String href = "";                                                           // link for buy ticket
        String imgSrc = "";                                                         // source of image
        String genre = "";                                                          // genre of film
        String time = "";                                                           // time of watching
        String about = "";                                                          // about film
        Document doc;                                                               // page for parsing
        String result;                                                              // result of searching

        @Override
        protected String doInBackground(String... strings) {

            try {

                doc = Jsoup.connect("https://vkino.com.ua/ua/cinema/zaporozhe/" + cinema + "?date=" + date + "#").timeout(1000).get();  // get page
                String title = doc.title();                                                                                                 // get title of page
                Log.d("TitleOfPage",title);

                Elements contents = doc.select(".film-name");                                                                       // get all movies titles
                moviesTitle.clear();
                for (Element titles: contents) {
                    moviesTitle.add(titles.text());                                                                                         // add title to array
                }

                if (moviesTitle.size() == 0) {                                                                                              // check if cinema have sessions
                    Log.d(cinema,getResources().getString(R.string.result0));
                    result = getResources().getString(R.string.result0);
                } else {

                    for (String titles: moviesTitle) {
                        if (movie.equals(titles)) {

                            result = getResources().getString(R.string.result1);

                            Elements links = doc.select("a");                                                                       // select all links to movies

                            for (Element link: links) {
                                if (link.text().equals(movie)) {                                                                            // get inner text of link(movie title) and compare with searched movie
                                    href = "https://vkino.com.ua" + link.attr("href");                                           // create link of movie that need
                                    break;
                                }
                            }
                            Log.d("MovieLink",href);

                            doc = Jsoup.connect(href).timeout(1000).get();                                                                  // connect by the new link
                            title = doc.title();
                            Log.d("TitleOfPage",title);

                            Element img = doc.select("img").first();                                                                // get movie image
                            if (img == null) {
                                throw new RuntimeException("Unable to locate image in " + title);
                            }
                            imgSrc = img.absUrl("data-src");                                                                     // get image source
                            Log.d("ImgSrc",imgSrc);

                            Elements contentAbout = doc.select(".text-block");                                                      // get all text content from .text-block tag, its contain About
                            about = contentAbout.text();
                            Log.d("About",about);

                            Element movieGenre = doc.select("dd").first();                                                          // get first content from tag dd, its contain Genre
                            if (movieGenre != null) {
                                genre = movieGenre.text();
                            }
                            Log.d("Data",genre);

                            Element movieTime = doc.select("dd").get(1);                                                            // get second content, its contain time of watching
                            if (movieTime != null) {
                                time = movieTime.text();
                            }
                            Log.d("Data",time);

                            break;
                        } else {
                            result = getResources().getString(R.string.result2);
                        }
                    }

                }

            } catch (IOException e) {
                result = getResources().getString(R.string.error_message_connection);
                Log.d("Error",e.toString());
            }
            progressBar.setVisibility(View.INVISIBLE);
            return null;
        }

        // Show current results on display
        @Override
        protected void onPostExecute(String s) {
            if (result.equals(getResources().getString(R.string.result1))) {

                // success of searching
                tvResult.setText(result);

                // create new activity
                Intent intentAfisha = new Intent(getApplicationContext(),Afisha.class);
                intentAfisha.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // provide data to other activity
                intentAfisha.putExtra("movieTile",movie).putExtra("img",imgSrc).putExtra("about",about).putExtra("genre"," " + genre).putExtra("time"," " + time).putExtra("link",href);

                // start new activity
                startActivity(intentAfisha);

            } else if (result.equals(getResources().getString(R.string.result2))) {

                // no session on searched film, show other sessions
                tvResult.setText(result + " " + "\"" + movie + "\"" + getResources().getString(R.string.result2Add) + " " + moviesTitle.toString().replace("[","").replace("]","").replace(",",";") + ".");

            } else if (result.equals(getResources().getString(R.string.result0))) {
                tvResult.setText(result);   // 0 films on this day
            } else {
                tvResult.setText(result);   // connection error
            }
        }
    }

    // Top bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_manu,menu);
        return true;
    }



    // Getting date
    public String getDate() {
        Calendar calendar = Calendar.getInstance();                                 // get current date
        Date today = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(today);
        return currentDate;
    }

    public String getDate(int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,i);                                       // get current date + i
        Date tomorrow = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String afterDate = dateFormat.format(tomorrow);
        return afterDate;
    }

    // Setting design of buttons
    public void setClickDesign(Button btn) {                                        // sets design of clicked element
        btn.setBackground(getResources().getDrawable(R.drawable.button_radius));
        btn.setTextColor(getResources().getColor(R.color.white));
    }
    public void setDefaultDesign(Button btn) {
        btn.setBackground(getResources().getDrawable(R.drawable.button_border));    // sets default design of other
        btn.setTextColor(getResources().getColor(R.color.purple_200));
    }

}