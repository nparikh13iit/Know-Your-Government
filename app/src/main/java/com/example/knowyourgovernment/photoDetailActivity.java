package com.example.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class photoDetailActivity extends AppCompatActivity {

    private TextView locationinfo, title, name;
    private ImageButton photo, partyLogo;
    private ConstraintLayout cl;

    private Official official;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        //Textfields
        locationinfo = findViewById(R.id.locationInfo);
        title = findViewById(R.id.title);
        name = findViewById(R.id.name);
        cl = findViewById(R.id.cl);

        //Images
        photo = findViewById(R.id.photo);
        partyLogo = findViewById(R.id.partyLogo);

        Intent data = getIntent();
        if (data.hasExtra("official")) {
            official = (Official) data.getSerializableExtra("official");
            if (official != null) {
                title.setText(official.getTitle());
                name.setText(official.getName());

                if (official.getParty().contains("Democratic")) {
                    partyLogo.setImageResource(R.drawable.dem_logo);
                    cl.setBackgroundColor(Color.parseColor("#0600FF"));
                } else if (official.getParty().contains("Republican")) {
                    partyLogo.setImageResource(R.drawable.rep_logo);
                    cl.setBackgroundColor(Color.parseColor("#FF0000"));
                } else {
                    partyLogo.setVisibility(View.GONE);
                    cl.setBackgroundColor(Color.parseColor("#000000"));
                }

                if (official.getPhotoURL() != "") {
                    Picasso.get().load(official.getPhotoURL())
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(photo);
                }
            }
            if (data.hasExtra("location")) {
                String location = data.getStringExtra("location");
                if (location != null) {
                    locationinfo.setText(location);
                }
            }
        }
    }
}