package com.bruno.link.preview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    private TextView mTitle;
    private TextView mAuthor;
    private TextView mDescription;
    private ImageView mImage;
    private TextView mType;
    private Button mPreview;

    private EditText mInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview();
            }
        });
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
        mImage = (ImageView) findViewById(R.id.linkImage);
        mType = (TextView) findViewById(R.id.linkType);
        mPreview = (Button) findViewById(R.id.previewBtn);
    }

    private void preview(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(mInput.getText().toString()).userAgent("Mozilla").get();
                    StringBuffer description=new StringBuffer(), image= new StringBuffer(), author= new StringBuffer(), title = new StringBuffer(), type = new StringBuffer();
                    for(Element meta : doc.select("meta")) {

                        String name = meta.attr("name");
                        String property = meta.attr("property");
                        String content = meta.attr("content");
                        Log.i(TAG,"Name: " + name + "- Property: " + property + " - Content: " + content);
                        if(property!=null){
                            if(property.equals(Constants.OpenGraphProtocol.OG_TITLE)) {
                                title.append(content);
                            }else if(property.equals(Constants.OpenGraphProtocol.OG_DESCRIPTION)) {
                                description.append(content);
                            }else if(property.equals(Constants.OpenGraphProtocol.OG_TYPE)) {
                                type.append(content);
                            }else if(property.equals(Constants.OpenGraphProtocol.OG_IMAGE)) {
                                image.append(meta.attr(("content")));
                            }
                        }
                    }
                    update(title.toString(), author.toString(), description.toString().trim(), image.toString(), type.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void update(final String title, final String author, final String description, final String image, final String type){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTitle.setText(title);
                mAuthor.setText(author);
                mDescription.setText(description);
                mType.setText(type);
                if(image!=null&&image.length()>0){
                    Picasso.with(MainActivity.this).load(image).fit().into(mImage);
                }
            }
        });
    }


}
