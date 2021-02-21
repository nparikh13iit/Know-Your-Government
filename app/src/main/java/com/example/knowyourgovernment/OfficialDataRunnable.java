package com.example.knowyourgovernment;

import android.net.Uri;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LongDef;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class OfficialDataRunnable implements Runnable {

    private MainActivity mainActivity;
    private String postal_code;
    private static final String TAG = "OfficialDataRunnable";
    private String API_KEY = "AIzaSyDVxV82GKwBL1ilo5O2dCin43TOmJE9w6s";
    private final String OFFICIAL_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key="+ API_KEY+"&address=";

    public OfficialDataRunnable(MainActivity mainActivity, String postal_code) {
        this.mainActivity = mainActivity;
        this.postal_code = postal_code;
    }

    @Override
    public void run() {
        String final_URL = OFFICIAL_URL+postal_code;

        Uri dataUri = Uri.parse(final_URL);
        String urlToUse = dataUri.toString();
        //Log.d(TAG, "run: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                handleResults(null);
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            //Log.d(TAG, "run: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            handleResults(null);
            return;
        }

        handleResults(sb.toString());

    }

    private void handleResults(String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.downloadFailed();
                }
            });
            return;
        }

        final ArrayList<Official> officialArrayList = parseJSON(s);
        final String location = parseJSONLocation(s);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (officialArrayList != null)

                mainActivity.updateData(officialArrayList,location);
            }
        });
    }

    private String parseJSONLocation(String s){

        String location = "";
        try {
            JSONObject jsonObject = new JSONObject(s);

            JSONObject normalizedInput = new JSONObject(jsonObject.getString("normalizedInput"));
            String city = normalizedInput.getString("city");
            String state = normalizedInput.getString("state");
            String zip = normalizedInput.getString("zip");
            if (!city.equals("")){
                location = city+", "+state+" "+zip;
            }
            if (city.equals("")){
                location = state+" "+zip;
            }

            return location;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            //e.printStackTrace();
        }
        return null;
    }




    private ArrayList<Official> parseJSON(String s) {
        //Log.d(TAG, "parseJSON: "+s);
        ArrayList<Official> tempList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray offices = new JSONArray(jsonObject.getString("offices"));

            for (int i = 0; i < offices.length();i++)
            {
                try{
                    JSONObject office = (JSONObject) offices.get(i);
                    JSONArray officialIndices = office.getJSONArray("officialIndices");

                    for (int j = 0; j< officialIndices.length();j++)
                    {
                        try{
                            int position = Integer.parseInt(officialIndices.get(j).toString());
                            JSONArray officials = new JSONArray(jsonObject.getString("officials"));
                            JSONObject officialInfo = officials.getJSONObject(position);

                            Official official = new Official();

                            //title
                            String title="";
                            try{
                                title=office.getString("name");
                            }catch (JSONException e) {
                                //e.printStackTrace();
                            }
                            official.setTitle(title);

                            //name:
                            String name="";
                            try{
                                name=officialInfo.getString("name");
                            }catch (JSONException e) {
                                //e.printStackTrace();
                            }
                            official.setName(name);

                            //party:
                            String party="";
                            try{
                                party=officialInfo.getString("party");
                            }catch (JSONException e) {
                                //e.printStackTrace();
                            }
                            official.setParty(party);

                            //Address:
                            JSONArray addressLines = new JSONArray(officialInfo.getString("address"));
                            JSONObject addressLine = new JSONObject(addressLines.get(0).toString());

                            String line1 = "";
                            try{
                                line1 = addressLine.getString("line1");
                            } catch (Exception e){
                                //.printStackTrace();
                            }

                            String line2 = "";
                            try{
                                line2 = addressLine.getString("line2");
                            } catch (Exception e){
                                //e.printStackTrace();
                            }

                            String city1 = "";
                            try{
                                city1 = addressLine.getString("city");
                            } catch (Exception e){
                                //e.printStackTrace();
                            }

                            String state1 = "";
                            try{
                                state1 = addressLine.getString("state");
                            } catch (Exception e){
                                //e.printStackTrace();
                            }

                            String zip1 = "";
                            try{
                                zip1 = addressLine.getString("zip");
                            } catch (Exception e){
                                //e.printStackTrace();
                            }

                            String address = line1 + " " + line2 + city1 + ", " + state1 + " " + zip1;
                            official.setAddress(address);

                            //Phone number:
                            String phone ="";
                            try{
                                phone=officialInfo.getJSONArray("phones").getString(0);
                            }catch (JSONException e) {
                                //e.printStackTrace();
                            }
                            official.setPhone(phone);

                            //URL:
                            String URL="";
                            try{
                                URL = officialInfo.getJSONArray("urls").getString(0);
                            } catch (Exception e){
                                //e.printStackTrace();
                            }
                            official.setUrl(URL);

                            //email:
                            String email="";
                            try{
                                email = officialInfo.getJSONArray("emails").getString(0);
                            } catch (Exception e){
                               // e.printStackTrace();
                            }
                            official.setEmail(email);

                            //Phone Url:
                            String photoURL="";
                            try{
                                photoURL = officialInfo.getString("photoUrl");
                            } catch (Exception e){
                                //e.printStackTrace();
                            }
                            official.setPhotoURL(photoURL);

                            try {

                                JSONArray channels = new JSONArray(officialInfo.getString("channels"));
                                for (int k = 0; k < channels.length(); k++) {

                                    try {
                                        JSONObject channel = (JSONObject) channels.get(k);

                                        String facebook = "", twitter = "", youtube = "";

                                        if (channel.getString("type").equals("Facebook")) {
                                            facebook = channel.getString("id");
                                            official.setFacebook(facebook);
                                        }
                                        if (channel.getString("type").equals("Twitter")) {
                                            twitter = channel.getString("id");
                                            official.setTwitter(twitter);
                                        }
                                        if (channel.getString("type").equals("YouTube")) {
                                            youtube = channel.getString("id");
                                            official.setYoutube(youtube);
                                        }
                                    } catch (Exception e){
                                        //e.printStackTrace();
                                    }
                                }
                            } catch (Exception e){
                                //e.printStackTrace();
                            }

                            Log.d(TAG, "parseJSON: "+official.getTitle()+ " "+official.getName()+ " "+official.getFacebook()+ " "+official.getTwitter()+ " "+official.getYoutube());


                            tempList.add(official);
                        } catch (Exception e) {
                            //Log.d(TAG, "parseJSON: " + e.getMessage());
                            //e.printStackTrace();
                        }

                    }
                } catch (Exception e) {
                    //Log.d(TAG, "parseJSON: " + e.getMessage());
                    //e.printStackTrace();
                }
            }
            return tempList;
        } catch (Exception e) {
            //Log.d(TAG, "parseJSON: " + e.getMessage());
            //e.printStackTrace();
        }
        return null;
    }
}
