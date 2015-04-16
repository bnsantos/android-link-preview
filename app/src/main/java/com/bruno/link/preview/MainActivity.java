package com.bruno.link.preview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    private TextView mTitle;
    private TextView mAuthor;
    private TextView mDescription;
    private TextView mImageUrl;

    private EditText mInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        preview();
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
        mInput = (EditText) findViewById(R.id.input);
        mTitle = (TextView) findViewById(R.id.linkTitle);
        mAuthor = (TextView) findViewById(R.id.linkAuthor);
        mDescription = (TextView) findViewById(R.id.linkDescription);
        mImageUrl = (TextView) findViewById(R.id.linkImageUrl);
    }

    private void preview(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(mInput.getText().toString()).userAgent("Mozilla").get();
                    StringBuffer description=new StringBuffer(), image= new StringBuffer(), author= new StringBuffer();
                    for(Element meta : doc.select("meta")) {
                        Log.i(TAG,"Name: " + meta.attr("name") + " - Content: " + meta.attr("content"));
                        if(meta.attr("name").contains("author")){
                            author.append(meta.attr("content"));
                        }else if(meta.attr("name").contains("description")){
                            Log.i(TAG, "Description: "+ meta.attr("name"));
                            description.append(meta.attr("content"));
                        }else if(meta.attr("name").contains("image")){
                            Log.i(TAG, "ImageURL: "+ meta.attr("name"));
                            image.append(meta.attr("content"));
                        }
                    }
                    update(doc.title(), author.toString(), description.toString().trim(), image.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void update(final String title, final String author, final String description, final String image){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTitle.setText(title);
                mAuthor.setText(author);
                mDescription.setText(description);
                mImageUrl.setText(image);
            }
        });
    }


}
