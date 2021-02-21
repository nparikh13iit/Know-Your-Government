package com.example.knowyourgovernment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private String location;
    private ArrayList<Official> officialList;
    private static int MY_LOCATION_REQUEST_CODE_ID = 111;
    private LocationManager locationManager;
    private Criteria criteria;

    private TextView locationinfo;
    private RecyclerView recyclerview;
    private OfficialAdapter officialAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Linking Components
        locationinfo = findViewById(R.id.locationInfo);
        recyclerview = findViewById(R.id.recycler);
        officialList = new ArrayList<>();

        //Initializing the RecyclerView
        officialAdapter = new OfficialAdapter(officialList,this);
        recyclerview.setAdapter(officialAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        String postal_code;

        //location//////////////////////////////////////////////////////////////////////////////////
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();

        // use gps for location
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE_ID);
        } else {
            postal_code = setLocation();
            Log.d(TAG, "onCreate: "+postal_code);
            if (postal_code != ""){
                //run the runnable
                if (doNetCheck()) {
                    getRunnableData(postal_code);
                } else {
                    noNetworkDailog();
                    officialList.clear();
                    officialAdapter.notifyDataSetChanged();
                    locationinfo.setText("No Data for Location");
                }
            }
            else {
                noNetworkDailog();
                officialList.clear();
                officialAdapter.notifyDataSetChanged();
                locationinfo.setText("No Data for Location");
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull
            String[] permissions, @NonNull
                    int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_LOCATION_REQUEST_CODE_ID) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                String postal_code;
                postal_code = setLocation();
                Log.d(TAG, "onCreate: "+postal_code);
                if (postal_code != ""){
                    //run the runnable
                    if (doNetCheck()) {
                        getRunnableData(postal_code);
                    } else {
                        noNetworkDailog();
                        officialList.clear();
                        officialAdapter.notifyDataSetChanged();
                        locationinfo.setText("No Data for Location");
                    }
                }
                else {
                    // load the error activity
                    noNetworkDailog();
                    officialList.clear();
                    officialAdapter.notifyDataSetChanged();
                    locationinfo.setText("No Data for Location");
                }
                return;
            }
        }

    }

    @SuppressLint("MissingPermission")
    private String setLocation() {

        String bestProvider = locationManager.getBestProvider(criteria, true);

        Location currentLocation = null;
        if (bestProvider != null) {
            currentLocation = locationManager.getLastKnownLocation(bestProvider);
        }


        //Log.d(TAG, "setLocation: "+ bestProvider + " " + currentLocation.getLatitude() + " " +currentLocation.getLongitude());

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String postal_code = "";
        try {
            List<Address> addresses;
            double lat = currentLocation.getLatitude();
            double lon = currentLocation.getLongitude();
            Log.d(TAG, "setLocation: "+lat +" " + lon);
            addresses = geocoder.getFromLocation(lat, lon, 10);

            postal_code = addresses.get(0).getPostalCode();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        Log.d(TAG, "setLocation: "+postal_code+ "");
        return postal_code;
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.info:
                Intent i1 = new Intent(this,AboutActivity.class);
                startActivity(i1);
                break;
            case R.id.location:
                if (doNetCheck()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    final EditText et = new EditText(this);
                    et.setInputType(InputType.TYPE_CLASS_TEXT);
                    et.setGravity(Gravity.CENTER_HORIZONTAL);
                    builder.setView(et);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String loc = et.getText().toString().toUpperCase().trim();

                            if (loc != null) {
                                if (doNetCheck()) {
                                    getRunnableData(loc);
                                } else {
                                    noNetworkDailog();
                                    officialList.clear();
                                    officialAdapter.notifyDataSetChanged();
                                    locationinfo.setText("No Data for Location");
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "You have left the field blank", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(MainActivity.this, "Location Search cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setMessage("Enter a City, State or a Zip Code:");

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    noNetworkDailog();
                    officialList.clear();
                    officialAdapter.notifyDataSetChanged();
                    locationinfo.setText("No Data for Location");
                }
                break;
            default:
                Toast.makeText(this,"Option not valid", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        int official_position = recyclerview.getChildAdapterPosition(view);
        Official official = officialList.get(official_position);

        Intent i = new Intent(this,OfficialActivity.class);
        i.putExtra("official",official);
        i.putExtra("location",location);
        startActivity(i);
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    public void updateData(ArrayList<Official> officialArrayList,String l) {
        officialList.clear();
        officialList.addAll(officialArrayList);
        officialAdapter.notifyDataSetChanged();
        locationinfo.setText(l.toString());
        location = l;
    }

    public void downloadFailed() {
    }

    public void getRunnableData(String locationdata){
        OfficialDataRunnable officialDataRunnable = new OfficialDataRunnable(this,locationdata);
        new Thread(officialDataRunnable).start();
    }

    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void noNetworkDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Data cannot be accessed/loaded without an internet connection.");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}