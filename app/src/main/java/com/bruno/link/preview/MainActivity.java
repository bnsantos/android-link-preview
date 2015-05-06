package com.bruno.link.preview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    private TextView mTitle;
    private TextView mAuthor;
    private TextView mDescription;
    private TextView mWebsite;
    private ImageView mImage;
    private Button mPreview;

    private Spinner mInput;

    private ScrollView mScroll;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.websites, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mInput.setAdapter(adapter);
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScroll.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                preview();
            }
        });


        mScroll.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews(){
        mInput = (Spinner) findViewById(R.id.input);
        mTitle = (TextView) findViewById(R.id.linkTitle);
        mAuthor = (TextView) findViewById(R.id.linkAuthor);
        mDescription = (TextView) findViewById(R.id.linkDescription);
        mImage = (ImageView) findViewById(R.id.linkImage);
        mPreview = (Button) findViewById(R.id.previewBtn);
        mWebsite = (TextView) findViewById(R.id.linkWebsite);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mScroll = (ScrollView) findViewById(R.id.scrollView);
    }

    private void preview(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(mInput.getItemAtPosition(mInput.getSelectedItemPosition()).toString()).userAgent("Mozilla").get();
                    updateViews(extractTitle(doc), extractDescription(doc), extractImageUrl(doc), extractAuthor(doc), extractWebsite(doc));
                } catch (IOException e) {
                    Log.e(TAG, "Ops", e);
                    show(R.string.error_unhandled_content);
                }
            }
        }).start();
    }

    private String extractTitle(Document doc){
        String title = doc.getElementsByAttributeValue("property", Constants.OpenGraphProtocol.OG_TITLE).attr("content");
        if(title==null||title.length()==0){
            title = doc.getElementsByAttributeValue("name", Constants.TwitterCards.TWITTER_TITLE).attr("content");
        }
        if(title==null||title.length()==0){
            title = doc.title();
        }
        return title;
    }

    private String extractDescription(Document doc){
        String description = doc.getElementsByAttributeValue("name", Constants.DESCRIPTION).attr("content");
        if(description==null||description.length()==0){
            description = doc.getElementsByAttributeValue("property", Constants.OpenGraphProtocol.OG_DESCRIPTION).attr("content");
        }
        if(description==null||description.length()==0){
            description = doc.getElementsByAttributeValue("name", Constants.TwitterCards.TWITTER_DESCRIPTION).attr("content");
        }
        return description;
    }

    private String extractImageUrl(Document doc){
        String imageUrl = doc.getElementsByAttributeValue("property", Constants.OpenGraphProtocol.OG_IMAGE).attr("content");
        if(imageUrl==null||imageUrl.length()==0){
            imageUrl = doc.getElementsByAttributeValue("name", Constants.TwitterCards.TWITTER_IMAGE).attr("content");
            if(imageUrl!=null&&imageUrl.length()==0){
                imageUrl = doc.getElementsByAttributeValue("name", Constants.TwitterCards.TWITTER_IMAGE_SRC).attr("content");
            }
        }
        return imageUrl;
    }

    private String extractAuthor(Document doc){
        return "";
    }

    private String extractWebsite(Document doc){
        return doc.baseUri();
    }

    private void updateViews(final String title, final String description, final String imageUrl, final String author, final String website){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTitle.setText(title);
                mDescription.setText(description);
                if(imageUrl!=null&&imageUrl.length()>0){
                    Picasso.with(MainActivity.this).load(imageUrl).into(mImage);
                }else{
                    mImage.setImageResource(android.R.color.transparent);
                }
                mAuthor.setText(author);
                mWebsite.setText(website);
                show(null);
            }
        });
    }

    private void show(final Integer errorMessage){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mScroll.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                if(errorMessage!=null){
                    Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
