package com.my1application.androidweatherappv2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.my1application.androidweatherappv2.Adapter.WeatherForecastAdapter;
import com.my1application.androidweatherappv2.Common.Common;
import com.my1application.androidweatherappv2.Model.WeatherForecastResult;
import com.my1application.androidweatherappv2.Retrofit.IOpenWeatherMap;
import com.my1application.androidweatherappv2.Retrofit.RetrofitClient;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class ForecastFragment extends Fragment {


    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    TextView txt_city_name, txt_geo_coord;
    RecyclerView recycler_forecast;


    static ForecastFragment instance;

    private RecyclerView.Adapter adapter;

    public static ForecastFragment getInstance() {
        if (instance == null)
            instance = new ForecastFragment();
        return instance;
    }

    public ForecastFragment() {
      compositeDisposable = new CompositeDisposable();
      Retrofit retrofit = RetrofitClient.getInstance();
      mService = retrofit.create(IOpenWeatherMap.class);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_forecast, container, false);

        txt_city_name =  (TextView) itemView.findViewById(R.id.txt_city_name);
        txt_geo_coord = (TextView) itemView.findViewById(R.id.txt_geo_coord);

        recycler_forecast =  (RecyclerView) itemView.findViewById(R.id.recycler_forecast);
        recycler_forecast.setHasFixedSize(true);
        recycler_forecast.setAdapter(adapter);
        recycler_forecast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        getForecastWeatherInformation();
        return itemView;
    }

    //ctrl+o

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    private void getForecastWeatherInformation() {
        compositeDisposable.add(mService.getForecastWeatherByLatlng(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecastResult>() {
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                         displayForecastWeather(weatherForecastResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.d("ERROR",""+ throwable.getMessage());
                    }
                })
                );

    }

    private void displayForecastWeather(WeatherForecastResult weatherForecastResult) {
            txt_city_name.setText(new StringBuilder(weatherForecastResult.city.name));
            txt_geo_coord.setText(new StringBuilder(weatherForecastResult.city.coord.toString()));

            WeatherForecastAdapter adapter = new WeatherForecastAdapter( );
            recycler_forecast.setAdapter(adapter);

        }
    }


