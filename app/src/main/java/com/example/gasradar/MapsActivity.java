package com.example.gasradar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.location.Location;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.gms.common.api.Status;

import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//////////////////////////////////


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15;

    //widget
    private ImageButton gps_button;
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    ImageButton SearchButton;

    //PlacesInfo
    private String TAG = MapsActivity.class.getSimpleName();
    private ListView listview;

    ArrayList<HashMap<String, String>> resultList = new ArrayList<>();;
    ArrayList<HashMap<String, String>> gasInfoList = new ArrayList<>();;
    String next_page_token;
    int repeat = 0;

    Toolbar toolbar;
    MenuItem Logout;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    public void onBackPressed() {
    }

    private void initMap() {
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this, permissions,
                        LOCATION_PERMISSION_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }
    TextView GasTitle;
    TextView GasAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SearchButton = findViewById(R.id.SearchButton);

        toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gps_button = findViewById(R.id.gps_button);

        getLocationPermission();

        listview = findViewById(R.id.list_view);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), GasInformation.class);
                intent.putExtra("name", ((HashMap<String, String>) listview.getAdapter().getItem((int)id)).get("name"));
                intent.putExtra("vicinity", ((HashMap<String, String>) listview.getAdapter().getItem((int)id)).get("vicinity"));
                intent.putExtra("rating", ((HashMap<String, String>) listview.getAdapter().getItem((int)id)).get("rating"));
                intent.putExtra("phonenumber", ((HashMap<String, String>) listview.getAdapter().getItem((int)id)).get("phonenumber"));
                intent.putExtra("regular", ((HashMap<String, String>) listview.getAdapter().getItem((int)id)).get("regular"));
                intent.putExtra("midgrade", ((HashMap<String, String>) listview.getAdapter().getItem((int)id)).get("midgrade"));
                intent.putExtra("premium", ((HashMap<String, String>) listview.getAdapter().getItem((int)id)).get("premium"));
                intent.putExtra("diesel", ((HashMap<String, String>) listview.getAdapter().getItem((int)id)).get("diesel"));
                intent.putExtra("logo", ((HashMap<String, String>) listview.getAdapter().getItem((int)id)).get("logo"));

                startActivity(intent);
            }
        });


        String url1 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=33.753746,-84.386330&radius=20000&type=gas_station&key=AIzaSyA0zCWP8KYsLyrfnxBKNli4KyLMTNcHP6I";
        new GetResults().execute(url1);
        new ParseHub().execute();
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyA0zCWP8KYsLyrfnxBKNli4KyLMTNcHP6I");
            HideSoftKeyboard();

        }
        PlacesClient placesClient = Places.createClient(this);

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                RectangularBounds bounds = RectangularBounds.newInstance(
                        new LatLng(33.620087, -84.501501),
                        new LatLng(33.921462, -84.269517));

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .setCountry("US")
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setHint("Gas Station")
                        .setLocationRestriction(bounds)
                        .build(MapsActivity.this);
                MapsActivity.this.startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        Logout = findViewById(R.id.logout_item);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.logout_item){
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(MapsActivity.this, "Log Out was successful", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(MapsActivity.this, "Log Out was unsuccessful", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(data);

                //Intent intent = new Intent(this,MapsActivity.class);
                //intent.putExtra("lat",place.getLatLng().latitude);
                //String latitude = intent.putExtra("lng",place.getLatLng().longitude);


                MarkerOptions options = new MarkerOptions()
                        .position(place.getLatLng())
                        .title(place.getName())
                        .snippet(place.getAddress())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                final Marker markername = mMap.addMarker(options);

                mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
                    @Override
                    public void onInfoWindowClose(Marker marker) {
                        markername.remove();

                    }
                });

               // Toast.makeText(MapsActivity.this, "Place: " + place.getName() + ", " + place.getAddress(), Toast.LENGTH_SHORT).show();

                moveCamera(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), DEFAULT_ZOOM);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(MapsActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void init() {

       /* mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId==EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction()==KeyEvent.ACTION_DOWN
                        || keyEvent.getAction()==KeyEvent.KEYCODE_ENTER){

                    //Execute method for searching
                   // geoLocate();
                }
                return false;
            }
        });*/

        gps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });
        HideSoftKeyboard();
    }

    private void getDeviceLocation() {
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                Task location = mfusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            //this is the current location
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);


                        } else {
                            Toast.makeText(MapsActivity.this, "Unable to get Current Location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        HideSoftKeyboard();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();

           // if(mMap != null) {
             //   MarkerOptions marker = new MarkerOptions(...);
               // mMap.addMarker(marker);
          //  }
            /*Log.e(TAG, "Testing the ArrayList:");

                LatLng gaslatlng = new LatLng(latitude,longitude);

                Log.e(TAG, "Latitude results: " + latitude);
                Log.e(TAG, "Longitude results: " + longitude);
                Log.e(TAG, "Gas name results: " + nameofGas);
                Log.e(TAG, "Latitude and Longitude results: " + gaslatlng);

                Log.e(TAG, "Testing the Markers:");

                MarkerOptions options = new MarkerOptions()
                        .position(gaslatlng)
                        .title(nameofGas);

                if (resultList!=null){
                    Log.e(TAG, resultList.toString());
                    mMap.addMarker(options);
                }
                else {
                    Log.e(TAG, "ResultList is NULL");

                }*/


            }
    }


    private void HideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    Boolean hastoken = false;

    private class GetResults extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          //  Toast.makeText(MapsActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(String... urls) {
            for (String url : urls) {
                HttpHandler sh = new HttpHandler();

                String jsonStr = sh.makeServiceCall(url);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // Getting JSON Array node
                        if (jsonObj.has("next_page_token")) {
                            next_page_token = jsonObj.getString("next_page_token");
                            Log.e(TAG, "The next token is: " + next_page_token);
                            hastoken = true;
                        }
                        else {
                            hastoken = false;
                            Log.e(TAG, "No more token");
                        }

                        JSONArray results = jsonObj.getJSONArray("results");

                        // looping through All results
                        for (int i = 0; i < results.length(); i++) {
                            //All result instances
                            JSONObject result = results.getJSONObject(i);

                            JSONObject geometry = result.getJSONObject("geometry");
                            JSONObject location = geometry.getJSONObject("location");

                            String latitude = location.getString("lat");
                            String longitude = location.getString("lng");

                            String icon = result.getString("icon");
                            String name = result.getString("name");

                          /*  JSONArray types = result.getJSONArray("types");
                            for (int j = 0; j < types.length(); j++) {
                                JSONObject type = types.getJSONObject(j);
                                //String gas_station = result.getString("gas_station");
                            }*/

                            String vicinity = result.getString("vicinity");


                            // tmp hash map for single result
                            HashMap<String, String> the_result = new HashMap<>();

                            // adding each child node to HashMap key => value
                            the_result.put("latitude", latitude);
                            the_result.put("longitude", longitude);
                            the_result.put("icon", icon);
                            the_result.put("name", name);
                            the_result.put("vicinity", vicinity);

                            // adding result to result list
                            resultList.add(the_result);
                        }

                        Log.e(TAG, "Results are done loading");


                        // marker(resultList);
                    } catch (final JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                } else {
                    Log.e(TAG, "Couldn't get json from server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Couldn't get json from server. Check LogCat for possible errors!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            return null;

        }
            private Double latitude;
           private Double longitude;
           private String nameofGas;
           private String googlevicinity;
           private String parsehubvicinity;
           private String regularprice;
           private String midgradeprice;
           private String premiumprice;
           private String dieselprice;

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

                /*ListAdapter adapter = new SimpleAdapter(MapsActivity.this, resultList,
                        R.layout.list_item, new String[]{"name", "vicinity"},
                        new int[]{R.id.GasStationName, R.id.GasStationVicinity});
                listview.setAdapter(adapter);*/


            Log.e(TAG, "Testing the Markers, check the map");
            for (Map<String, String> i : resultList) {
                for (Map<String, String> j : gasInfoList) {
                    latitude = Double.parseDouble(i.get("latitude"));
                    longitude = Double.parseDouble(i.get("longitude"));
                    googlevicinity = i.get("vicinity");
                    parsehubvicinity = j.get("vicinity");

                    Double rlat = Math.round(latitude * 10000000.0) / 10000000.0;
                    Double rlng = Math.round(longitude * 10000000.0) / 10000000.0;
                    LatLng gaslatlng = new LatLng(rlat, rlng);

                    nameofGas = i.get("name");

                    Log.e(TAG, "Latitude results: " + latitude);
                    Log.e(TAG, "Longitude results: " + longitude);
                    Log.e(TAG, "Gas name results: " + nameofGas);
                    Log.e(TAG, "Latitude and Longitude results: " + gaslatlng);

                    if (googlevicinity.startsWith(parsehubvicinity.substring(0, 4))) {
                        regularprice = j.get("regular");
                        midgradeprice = j.get("midgrade");
                        premiumprice = j.get("premium");
                        dieselprice = j.get("diesel");
                        Log.e(TAG, "It's a match from: " + googlevicinity + " to " + parsehubvicinity);
                    } else {
                        Log.e(TAG, "Not a match");
                    }

                    MarkerOptions options = new MarkerOptions()
                            .position(gaslatlng)
                            .title(nameofGas)
                            .snippet(googlevicinity + "\n" + "Regular: " + regularprice + "\n" + "Midgrade: " + midgradeprice
                                    + "\n" + "Premium: " + premiumprice + "\n" + "Diesel: " + dieselprice)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                    mMap.addMarker(options);

                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker arg0) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            Context context = getApplicationContext();
                            LinearLayout info = new LinearLayout(context);
                            info.setOrientation(LinearLayout.VERTICAL);

                            TextView title = new TextView(context);
                            title.setTextColor(Color.BLACK);
                            title.setGravity(Gravity.CENTER);
                            title.setTypeface(null, Typeface.BOLD);
                            title.setText(marker.getTitle());

                            TextView snippet = new TextView(context);
                            snippet.setTextColor(Color.GRAY);
                            snippet.setText(marker.getSnippet());

                            info.addView(title);
                            info.addView(snippet);

                            return info;
                        }
                    });
                }
            }
            Log.e(TAG, "Testing if it has a next token:");

                if (hastoken == true){
                    String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=33.753746,-84.386330&radius=20000&type=gas_station&key=AIzaSyA0zCWP8KYsLyrfnxBKNli4KyLMTNcHP6I+&pagetoken=" + next_page_token;
                    new GetResults().execute(url);
                    repeat += 1;
                    Log.e(TAG, "It repeated:" +repeat + "times");
                    hastoken = false;

                }
                else {
                    Log.e(TAG, "No token given");
                    hastoken = false;

                }
        }
    }


    private class ParseHub extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  Toast.makeText(MapsActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String Parseurl = "https://www.parsehub.com/api/v2/projects/tcGPyMvntt2T/last_ready_run/data?api_key=t_-RR9TQ17be";
            //String Parseurl2 = "https://www.parsehub.com/api/v2/projects/tRZ1faNkC0Bn/last_ready_run/data?api_key=t_-RR9TQ17be";

            String jsonStr = sh.makeServiceCall(Parseurl);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray results = jsonObj.getJSONArray("gasname");

                    // looping through All results
                    for (int i = 0; i < results.length(); i++) {
                        //All result instances
                        JSONObject result = results.getJSONObject(i);
                        String name = result.getString("name");
                        String vicinity = result.getString("address").replace("\n",", ");
                        String rating;
                        String logo;
                        String regular; String midgrade; String premium; String diesel; String phonenumber;

                        if (result.has("regular")) {
                            regular = result.getString("regular");}
                        else{ regular = "N/A"; }

                        if (result.has("midgrade")) {
                            midgrade = result.getString("midgrade"); }
                        else{ midgrade = "N/A"; }

                        if (result.has("premium")) {
                            premium = result.getString("premium");}
                        else{ premium = "N/A"; }

                        if (result.has("diesel")) {
                            diesel = result.getString("diesel"); }
                        else{ diesel = "N/A"; }

                        if (result.has("phonenumber")) {
                            phonenumber = result.getString("phonenumber"); }
                        else{ phonenumber = "N/A"; }

                        if(result.has("rating")){
                            rating = result.getString("rating"); }
                        else{ rating = "No rating available"; }

                        if(result.has("logo")){
                            logo =  result.getString("logo"); }
                        else{
                            Log.e(TAG, "Gas Station" + name + "did not provide logo.");
                            continue;
                        }


                        Log.e(TAG, "PH Name " + name);
                        Log.e(TAG, "PH Vicinity " + vicinity);
                        Log.e(TAG, "PH Regular " + regular);
                        Log.e(TAG, "PH Midgrade " + midgrade);
                        Log.e(TAG, "PH Premium " + premium);
                        Log.e(TAG, "PH Diesel " + diesel);

                        // tmp hash map for single result
                        HashMap<String, String> gasinfo_result = new HashMap<>();

                        // adding each child node to HashMap key => value
                        gasinfo_result.put("name", name);
                        gasinfo_result.put("vicinity", vicinity);
                        gasinfo_result.put("regular", regular);
                        gasinfo_result.put("midgrade", midgrade);
                        gasinfo_result.put("premium", premium);
                        gasinfo_result.put("diesel", diesel);
                        gasinfo_result.put("phonenumber", phonenumber);
                        gasinfo_result.put("rating", rating);
                        gasinfo_result.put("logo",logo);

                        // adding result to result list
                        gasInfoList.add(gasinfo_result);
                    }

                    Log.e(TAG, "Results are done loading from ParseHub");


                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                       /*     Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();*/
                        }
                    });
                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            ListAdapter adapter = new SimpleAdapter(MapsActivity.this, gasInfoList,
                    R.layout.list_item, new String[]{"name", "vicinity","regular"},
                    new int[]{R.id.GasStationName, R.id.GasStationVicinity,R.id.GasRegular});
            listview.setAdapter(adapter);

        //Making a comparison of addresses
           /* for (Map<String, String> i : resultList) {
                for (Map<String, String> j : gasInfoList) {
                    String googleaddress = i.get("vicinity");
                    String parsehubaddress = j.get("vicinity");

                    if (googleaddress.startsWith(parsehubaddress.substring(0,4))) {

                        Log.e(TAG, "It's a match from: " + googleaddress + "to " + parsehubaddress);
                        Log.e(TAG, "Gas prices are: " + j.get("regular") + " " + j.get("midgrade") + "" + j.get("premium") + " " + j.get("diesel"));
                    } else {
                        Log.e(TAG, "Not a match");
                    }
                }
            }*/

        }
    }


}
