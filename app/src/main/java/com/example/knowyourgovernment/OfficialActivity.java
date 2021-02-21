package com.example.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.awt.font.TextAttribute;

public class OfficialActivity extends AppCompatActivity {

    private static final String TAG = "OfficialActivity";
    private Official official;
    private String photoURL,facebookURL,twitterURL,youtubeURL,location;

    private TextView locationinfo, title, name, party,address,phone,email,website;
    private TextView addressTitle,phoneTitle,emailTitle,websiteTitle;
    private ImageButton photo, partyLogo, facebook, twitter, youtube;
    private ConstraintLayout cl,cl0;
    private ScrollView sl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        cl = findViewById(R.id.cl);
        cl0 = findViewById(R.id.cl0);
        sl = findViewById(R.id.sl);

        //Textfields
        locationinfo = findViewById(R.id.locationInfo);
        title = findViewById(R.id.title);
        name = findViewById(R.id.name);
        party = findViewById(R.id.party);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        website = findViewById(R.id.website);
        addressTitle = findViewById(R.id.addressTitle);
        phoneTitle = findViewById(R.id.phoneTitle);
        emailTitle = findViewById(R.id.emailTitle);
        websiteTitle = findViewById(R.id.websiteTitle);

        //Image Buttons
        photo = findViewById(R.id.photo);
        partyLogo = findViewById(R.id.partyLogo);
        facebook = findViewById(R.id.facebook);
        twitter = findViewById(R.id.twitter);
        youtube = findViewById(R.id.youtube);

        Intent data = getIntent();
        if (data.hasExtra("official"))
        {
            official = (Official) data.getSerializableExtra("official");
            if (official != null)
            {
                // Getting the URLS
                facebookURL = official.getFacebook();
                twitterURL = official.getTwitter();
                youtubeURL = official.getYoutube();
                photoURL = official.getPhotoURL();

                //Setting Textfields
                title.setText(official.getTitle());
                name.setText(official.getName());
                party.setText("("+official.getParty()+")");
                address.setText(official.getAddress());
                phone.setText(official.getPhone());
                email.setText(official.getEmail());
                website.setText(official.getUrl());

                //Linkifying the fields
                Linkify.addLinks(address, Linkify.ALL);
                Linkify.addLinks(phone, Linkify.ALL);
                Linkify.addLinks(email, Linkify.ALL);
                Linkify.addLinks(website, Linkify.ALL);

                //Setting the party logo and background
                if (official.getParty().contains("Democratic")){
                    partyLogo.setImageResource(R.drawable.dem_logo);
                    cl.setBackgroundColor(Color.parseColor("#0600FF"));
                    cl0.setBackgroundColor(Color.parseColor("#0600FF"));
                    sl.setBackgroundColor(Color.parseColor("#0600FF"));
                }
                else if (official.getParty().contains("Republican")){
                    partyLogo.setImageResource(R.drawable.rep_logo);
                    cl.setBackgroundColor(Color.parseColor("#FF0000"));
                    cl0.setBackgroundColor(Color.parseColor("#FF0000"));
                    sl.setBackgroundColor(Color.parseColor("#FF0000"));
                }
                else {
                    partyLogo.setVisibility(View.GONE);
                    cl.setBackgroundColor(Color.parseColor("#000000"));
                    cl0.setBackgroundColor(Color.parseColor("#000000"));
                    sl.setBackgroundColor(Color.parseColor("#000000"));
                }

                //Removing the blank fields:
                if (official.getAddress().equals("")) {
                    address.setVisibility(View.GONE);
                    addressTitle.setVisibility(View.GONE);
                }
                if (official.getPhone().equals("")) {
                    phone.setVisibility(View.GONE);
                    phoneTitle.setVisibility(View.GONE);
                }
                if (official.getEmail().equals("")) {
                    email.setVisibility(View.GONE);
                    emailTitle.setVisibility(View.GONE);
                }
                if (official.getUrl().equals("")) {
                    website.setVisibility(View.GONE);
                    websiteTitle.setVisibility(View.GONE);
                }

                if (official.getFacebook().equals("")){facebook.setVisibility(View.GONE);}
                if (official.getTwitter().equals("")){twitter.setVisibility(View.GONE);}
                if (official.getYoutube().equals("")){youtube.setVisibility(View.GONE);}

                if (photoURL!= "") {
                    Picasso.get().load(photoURL)
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(photo); // Use this if you don't want a callback
                }


            }
        }

        if (data.hasExtra("location"))
        {
            location = data.getStringExtra("location");
            if (location != null)
            {
                locationinfo.setText(location);
            }
        }

    }

    public void openFacebook(View v){
        String FACEBOOK_URL = "https://www.facebook.com/" + facebookURL;
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + facebookURL;
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void openTwitter(View v){
        Intent intent = null;
        String name = twitterURL;
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void openYoutube(View v){
        String name = youtubeURL;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    public void callURL(View view) {
        String url;
        if(official.getParty().contains("Democratic"))
        {
            url="https://democrats.org";
        }
        else
        {
            url="https://www.gop.com";
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void photoClick(View view) {
        if (photoURL!= ""){
            Intent i = new Intent(this,photoDetailActivity.class);
            i.putExtra("official",official);
            i.putExtra("location",location);
            startActivity(i);
        }

    }
}