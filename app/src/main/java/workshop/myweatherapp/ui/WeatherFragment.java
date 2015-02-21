package workshop.myweatherapp.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import workshop.myweatherapp.R;
import workshop.myweatherapp.utils.NetworkUtils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends BaseFragment {
    private static final String ARG_LAT = "lat";
    private static final String ARG_LONGI = "longi";
    View progressBar;
    TextView cityName, temp, desc, pressure, humidity, wind, cloud;
    ImageView weatherIcon;
    private double lat;
    private double longi;
    private ViewGroup mContainer;

    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param lat   Latitude.
     * @param longi Longitude.
     * @return A new instance of fragment WeatherFragment.
     */
    public static WeatherFragment newInstance(double lat, double longi) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LAT, lat);
        args.putDouble(ARG_LONGI, longi);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lat = getArguments().getDouble(ARG_LAT);
            longi = getArguments().getDouble(ARG_LONGI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        progressBar = view.findViewById(R.id.pbWeather);
        mContainer = (ViewGroup) view.findViewById(R.id.llContainer);
        cityName = (TextView) view.findViewById(R.id.tvCityName);
        temp = (TextView) view.findViewById(R.id.tvTemp);
        desc = (TextView) view.findViewById(R.id.tvWeatherDesc);
        pressure = (TextView) view.findViewById(R.id.tvPressure);
        humidity = (TextView) view.findViewById(R.id.tvHumidity);
        wind = (TextView) view.findViewById(R.id.tvWind);
        cloud = (TextView) view.findViewById(R.id.tvCloud);
        weatherIcon = (ImageView) view.findViewById(R.id.ivWeatherIcon);

        new RetrieveWeather().execute(lat, longi);

        return view;
    }


    public class RetrieveWeather extends AsyncTask<Double, Void, JSONObject> {

        Bitmap mBitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mContainer.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Double... params) {
            try {
                JSONObject json_reponse = new JSONObject(NetworkUtils.GET("http://api.openweathermap.org/data/2.5/weather?lat=" + params[0] + "&lon=" + params[1]));
                URL urlConnection = new URL("http://openweathermap.org/img/w/" + json_reponse.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png");
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                mBitmap = BitmapFactory.decodeStream(input);
                return json_reponse;
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(JSONObject json_response) {
            super.onPostExecute(json_response);
            mContainer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            try {
                DecimalFormat df = new DecimalFormat("#.0");
                cityName.setText(json_response.getString("name"));
                temp.setText(df.format(json_response.getJSONObject("main").getDouble("temp") - 272.15) + "°");
                desc.setText(json_response.getJSONArray("weather").getJSONObject(0).getString("description"));
                pressure.setText(json_response.getJSONObject("main").getString("pressure") + " mbar");
                humidity.setText(json_response.getJSONObject("main").getString("humidity") + " %");
                wind.setText(json_response.getJSONObject("wind").getString("speed") + " mph");
                cloud.setText(json_response.getJSONObject("clouds").getString("all") + " %");
                weatherIcon.setImageBitmap(mBitmap);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
