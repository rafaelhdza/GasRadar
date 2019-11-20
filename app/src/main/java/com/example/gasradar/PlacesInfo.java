package com.example.gasradar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PlacesInfo extends AppCompatActivity {

    private String TAG = PlacesInfo.class.getSimpleName();
    private ListView listview;

    ArrayList<HashMap<String, String>> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        resultList = new ArrayList<>();
        listview = findViewById(R.id.list_view);

        new GetResults().execute();
    }

    private class GetResults extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(PlacesInfo.this,"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response

            //https://maps.googleapis.com/maps/api/place/nearbysearch/json?
            // location=33.753746,-84.386330
            // &radius=20000
            // &type=gas_station
            // &keyword=
            // key=AIzaSyA0zCWP8KYsLyrfnxBKNli4KyLMTNcHP6I

            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=33.753746,-84.386330&radius=20000&type=gas_station&key=AIzaSyA0zCWP8KYsLyrfnxBKNli4KyLMTNcHP6I";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
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

                        /*JSONArray types = result.getJSONArray("types");
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

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(PlacesInfo.this, resultList,
                    R.layout.list_item, new String[]{ "name","vicinity"},
                    new int[]{R.id.GasStationName, R.id.GasStationVicinity});
            listview.setAdapter(adapter);
        }
    }
}
