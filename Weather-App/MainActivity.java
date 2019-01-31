package com.c323proj10.jillpena;

import com.c323proj10.jillpena.R;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity  {

    private final String apiKey= "7bfab2d91646137766a69251ab55afb1";
    private final int MY_PERMISSIONS_REQUEST = 1;
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    ProgressDialog progressDialog;
    String jsonData = "";
    TextView tempTextView;
    TextView weatherDetailsTextView;
    TextView dateTextView;
    ImageView currentWeather;
    private ArrayList<Weather> weatherList;
    RecyclerView rv;
    Location currLocation;
    Boolean switchUnits = false;
    RVAdapter adapter;
    private GestureDetectorCompat gestureDetectorCompat = null;
    ArrayList<Integer> imageList = new ArrayList<>();;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tempTextView = findViewById(R.id.textViewTemp);
        weatherDetailsTextView = findViewById(R.id.TextViewWeatherDetails);
        dateTextView = findViewById(R.id.textViewDate);
        weatherList = new ArrayList<>();
        currentWeather = findViewById(R.id.imageView);
        imageList.clear();
        imageList.add(0, R.mipmap.clear_day_round);
        imageList.add(1, R.mipmap.cloud_day_round);
        imageList.add(2, R.mipmap.rain_day_round);
        imageList.add(3, R.mipmap.snow_day_round);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        MyGestureDetector gestureDetector = new MyGestureDetector();
        gestureDetector.setActivity(this);
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureDetector);

        // ConstraintLayout constraintLayout = findViewById(R.layout.myLayoutMain);
        requestLocationPermission();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchUnits = !switchUnits;
                getWeatherFromLocation();
                if (switchUnits) {
                    Toast.makeText(getApplicationContext(), "Units changed to imperial", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getApplicationContext(), "Units changed to metric", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorCompat.onTouchEvent(event);
        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST){
            requestLocationPermission();
        }
    }

    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
                }
            } else {
                try {
                    Toast.makeText(this, "Getting Location...", Toast.LENGTH_LONG).show();
                    mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                    currLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    setLocationListener();
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60 * 60 * 3, 10, mLocationListener);
                    Toast.makeText(this, "Location Found.", Toast.LENGTH_LONG).show();
                    getWeatherFromLocation();
                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "Could Not Get Location.", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            try {
                Toast.makeText(this, "Getting Location...", Toast.LENGTH_LONG).show();
                mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                currLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                setLocationListener();
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60 * 60 * 3, 10, mLocationListener);
                Toast.makeText(this, "Location Found.", Toast.LENGTH_LONG).show();
                getWeatherFromLocation();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "Could Not Get Location.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setLocationListener(){
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.i("WEATHER", "New Location Found.");
                currLocation = location;
                getWeatherFromLocation();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
    }

    public void getWeatherFromLocation(){

        String apiCall = "";
        if (switchUnits) {
            apiCall = "https://api.openweathermap.org/data/2.5/forecast?lat="
                    + currLocation.getLatitude() +
                    "&lon=" + currLocation.getLongitude() +
                    "&units=metric" +
                    "&appid=" + apiKey;
        } else{
            apiCall = "https://api.openweathermap.org/data/2.5/forecast?lat="
                    + currLocation.getLatitude() +
                    "&lon=" + currLocation.getLongitude() +
                    "&units=imperial" +
                    "&appid=" + apiKey;
        }

        Log.i("WEATHER", "api call: " + apiCall);
        Log.i("WEATHER", "current location: " + currLocation.getLatitude() + ", " + currLocation.getLongitude());
        getWebResponse(apiCall);
        parseJSONData();
        initalizeList();
    }

    private void initalizeList() {
        rv = findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        adapter = new RVAdapter(weatherList);
        rv.setAdapter(adapter);
    }

    private void getWebResponse(String stringURL) {

            progressDialog = ProgressDialog.show(this, "Weather App", "Connecting, Please wait ...", true, true);
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                try {
                    jsonData = new DownloadWebpageTask().execute(stringURL).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "No network connection available!", Toast.LENGTH_SHORT).show();
            }
    }

    public void parseJSONData() {
        if (!jsonData.isEmpty()){
            weatherList.clear();
            try {
                JSONObject jsonRootObject = new JSONObject(jsonData);
                JSONArray jsonArrayList = jsonRootObject.getJSONArray("list");
                JSONObject jsonObject0 = jsonArrayList.getJSONObject(0);
                JSONObject mainObject0 = jsonObject0.getJSONObject("main");
                String currTemp = mainObject0.get("temp").toString();
                JSONArray WeatherArray0 = jsonObject0.getJSONArray("weather");
                JSONObject WeatherObject0 = WeatherArray0.getJSONObject(0);
                String description0 = WeatherObject0.get("description").toString().toUpperCase();
                String date0 = jsonObject0.get("dt_txt").toString();
                Log.i("WEATHER", "current weather data: " + "temp: " + currTemp + ", description: " + description0
                + ", date: " + date0);
                //Toast.makeText(this, "About to set textViews", Toast.LENGTH_SHORT).show();
                if (switchUnits) {
                    tempTextView.setText(currTemp + " °C");
                } else{
                    tempTextView.setText(currTemp + " °F");
                }

                if (description0.toLowerCase().contains("rain")){
                    currentWeather.setImageResource(imageList.get(2));
                    Log.i("WEATHER", "image changed to rain");
                }
                else if(description0.toLowerCase().contains("cloud")){
                    currentWeather.setImageResource(imageList.get(1));
                    Log.i("WEATHER", "image changed to clouds");
                }
                else if (description0.toLowerCase().contains("snow")){
                    currentWeather.setImageResource(imageList.get(3));
                    Log.i("WEATHER", "image changed to snow");

                }else if (description0.toLowerCase().contains("clear")){
                    currentWeather.setImageResource(imageList.get(0));
                    Log.i("WEATHER", "image changed to clear");
                }

                weatherDetailsTextView.setText(description0);
                SimpleDateFormat parser2 = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                //Date date0Date = parser2.parse(date0);
                String[] date0Array = date0.split(" ");
                Date newDate = new Date();
                String newDate0 = parser2.format(newDate);
                dateTextView.setText(newDate0);

                for(int i =0; i<jsonArrayList.length(); i++){
                    JSONObject jsonObject = jsonArrayList.getJSONObject(i);
                    JSONObject mainObject = jsonObject.getJSONObject("main");
                    String tempNow = mainObject.get("temp").toString();
                    JSONArray WeatherArray = jsonObject0.getJSONArray("weather");
                    JSONObject WeatherObject = WeatherArray.getJSONObject(0);
                    String description = WeatherObject.get("description").toString().toUpperCase();
                    String date = jsonObject.get("dt_txt").toString();
                   // Date dateDate = parser2.parse(date);
                    String[] dateArray = date.split(" ");
                    if (date.contains("12:00:00") /*&& !date0Array[0].equals(dateArray[0])*/) {
                        Log.i("WEATHER", "data: " + "temp: " + tempNow + ", description: " + description
                                + ", date: " + date);
                        String tempWithUnits = tempNow;
                        if (switchUnits) {
                            tempWithUnits = tempNow.concat(" °C");
                        } else {
                            tempWithUnits = tempNow.concat(" °F");
                        }
                        weatherList.add(new Weather(tempWithUnits, date, description));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Could not parse JSON DATA: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(this, "jsondata is empty", Toast.LENGTH_SHORT).show();
        }

    }


    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView weatherTextView;

            public MyViewHolder(View view) {
                super(view);
                weatherTextView = view.findViewById(R.id.textViewWeather);
            }
        }

        public RVAdapter(ArrayList<Weather> list) {
            weatherList = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.weather_item_layout,parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Weather curr = weatherList.get(position);
            holder.weatherTextView.setText(curr.getTemp() + " " + curr.getDetails() + " " + curr.getDate());
        }

        @Override
        public int getItemCount() {
            return weatherList.size();
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            jsonData = downloadFromURL(urls[0]);
            return jsonData;
        }

        private String downloadFromURL(String url) {

            InputStream is = null;
            StringBuffer result = new StringBuffer();
            URL myUrl = null;
            try {
                myUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET"); //not needed any more , GET is default
                conn.connect();
                int response = conn.getResponseCode();
                Log.i("WEATHER", "The response is: "+response);
                is = conn.getInputStream();
                progressDialog.dismiss();

                BufferedReader reader = new BufferedReader((new InputStreamReader(is, "UTF-8")));
                String line = "";
                while((line = reader.readLine()) != null){
                    result.append(line);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("WEATHER", "The result is: "+ result.toString());
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("WEATHER", "The result is: "+result);
        }
    }

   private class MyGestureDetector extends SimpleOnGestureListener {

       private MainActivity activity = null;
       private int MIN_SWIPE_DISTANCE_X = 100;
       private int MAX_SWIPE_DISTANCE_X = 1000;

       public MainActivity getActivity() {
           return activity;
       }

       public void setActivity(MainActivity activity) {
           this.activity = activity;
       }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            float deltaX = e1.getX() - e2.getX();
            float deltaXAbs = Math.abs(deltaX);
            String currentDate = dateTextView.getText().toString();
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            Date current = null;
            try {
                current = parser.parse(currentDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int size = weatherList.size();
            int i = size-1;
            int j = 0;

            if((deltaXAbs >= MIN_SWIPE_DISTANCE_X) && (deltaXAbs <= MAX_SWIPE_DISTANCE_X))
            {
                if(deltaX > 0)
                {
                   //Toast.makeText(getActivity(), "Swipe to left", Toast.LENGTH_SHORT).show();
                    while (j < size){
                        Weather w = weatherList.get(j);
                        Date newDate = null;
                        try {
                            newDate = parser.parse(w.getDate());
                            if (newDate.after(current)){
                                tempTextView.setText(w.getTemp());
                                dateTextView.setText(w.getDate());
                                if (w.getDetails().toLowerCase().contains("rain")){
                                    currentWeather.setImageResource(imageList.get(2));
                                    Log.i("WEATHER", "image changed to rain");
                                }
                                else if(w.getDetails().toLowerCase().contains("cloud")){
                                    currentWeather.setImageResource(imageList.get(1));
                                    Log.i("WEATHER", "image changed to clouds");
                                }
                                else if (w.getDetails().toLowerCase().contains("snow")){
                                    currentWeather.setImageResource(imageList.get(3));
                                    Log.i("WEATHER", "image changed to snow");

                                }else if (w.getDetails().toLowerCase().contains("clear")){
                                    currentWeather.setImageResource(imageList.get(0));
                                    Log.i("WEATHER", "image changed to clear");
                                }
                                weatherDetailsTextView.setText(w.getDetails());
                                break;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            break;
                        }
                        j++;
                    }

                }else
                {
                    //Toast.makeText(getActivity(), "Swipe to right", Toast.LENGTH_SHORT).show();
                    while (i > 0){
                        Weather w = weatherList.get(i);
                        Date newDate = null;
                        try {
                            newDate = parser.parse(w.getDate());
                            if (newDate.before(current)){
                                tempTextView.setText(w.getTemp());
                                dateTextView.setText(w.getDate());
                                if (w.getDetails().toLowerCase().contains("rain")){
                                    currentWeather.setImageResource(imageList.get(2));
                                    Log.i("WEATHER", "image changed to rain");
                                }
                                else if(w.getDetails().toLowerCase().contains("cloud")){
                                    currentWeather.setImageResource(imageList.get(1));
                                    Log.i("WEATHER", "image changed to clouds");
                                }
                                else if (w.getDetails().toLowerCase().contains("snow")){
                                    currentWeather.setImageResource(imageList.get(3));
                                    Log.i("WEATHER", "image changed to snow");

                                }else if (w.getDetails().toLowerCase().contains("clear")){
                                    currentWeather.setImageResource(imageList.get(0));
                                    Log.i("WEATHER", "image changed to clear");
                                }
                                weatherDetailsTextView.setText(w.getDetails());
                                break;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            break;
                        }
                        i--;
                    }
                }
            }

            return true;
        }
    }


}
