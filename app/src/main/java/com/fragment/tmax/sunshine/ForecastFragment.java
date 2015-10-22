package com.fragment.tmax.sunshine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tmax on 05/01/2015.
 */
public  class ForecastFragment extends android.support.v4.app.Fragment {
    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu event(step 11)
        setHasOptionsMenu(true);
    }

    @Override
    // (step 12)
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecasfragment, menu);
    }

    @Override
    //(step 13)
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle action bar action click here. The action bar will automatically
        // handle clicks on up/home button,
        // so long as you specify a parent activity in androidManifest.xml
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchWeatherTAsk fetchWeatherTAsk = new FetchWeatherTAsk();
            fetchWeatherTAsk.execute("Lagos"); //You need at least one parameter, or fetchWeatherTask will return null.
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // a fake array list of string data(Step 1)
        String[] arrayList = {"Today-sunny- 88/63", "Tomorrow -foggy-70/46",
                "Wednesday-cloudy-72/63", "Thursday-rainy-54/51",
                "Friday-foggy-70/46", "Saturday-sunny-76/76"};
        // We turn the strings data above to an array list (Step 2)
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(arrayList));
        // here we will create an adapter to populate the listViews (Step 3)
        ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(
                // we pass in the context(Fragment of our parent activity(Step 4))
                getActivity(),
                //ID of the List Item Layout(Step 5)
                R.layout.list_item_forcast,
                //ID of the Text View to populate (Step 6)
                R.id.list_item_forecast_text,
                //Forecast Data (pass in initial arrays od string list.)(Step 7)
                weekForecast

        );

        // find the list views in the fragment rootView in our fragment place holder(Step 8)
        ListView listOfWeathers;
        listOfWeathers = (ListView) rootView.findViewById(R.id.list_view_forecast);
        // Set the find list view to and adapter(step 9) test our app.
        listOfWeathers.setAdapter(mForecastAdapter);


        return rootView;
    }


    // // Connect app to weather and introduce running in background(asyntask) (step 10)


    public class FetchWeatherTAsk extends AsyncTask<String, Void, Void> {
        private final String LOG_TAG = FetchWeatherTAsk.class.getSimpleName();


        @Override
        protected Void doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=Lagos&mode=json&units=metric&cnt=7&appid=bd82977b86bf27fb59a04b61b657fb6f");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Forcast JSON String: " + forecastJsonStr); //This goes after forecastJsonStr = buffer.toString();.
            } catch (IOException e) {
//                Log.d("ERROR", "Error.");
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return null;
        }
    }
}







