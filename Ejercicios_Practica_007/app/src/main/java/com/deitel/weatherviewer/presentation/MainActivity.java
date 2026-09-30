package com.deitel.weatherviewer.presentation;

import com.deitel.weatherviewer.R;

import com.deitel.weatherviewer.domain.entities.Weather;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    // Lista de objetos Weather representando el pronóstico
    private List<Weather> weatherList = new ArrayList<>();

    // ArrayAdapter para vincular objetos Weather al ListView
    private WeatherArrayAdapter weatherArrayAdapter;
    private ListView weatherListView;

    // Executor para operaciones de red en segundo plano (reemplaza AsyncTask)
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar); <-- ¡Aquí está la corrección!

        // Crear ArrayAdapter para asociar weatherList con weatherListView
        weatherListView = (ListView) findViewById(R.id.weatherListView);
        weatherArrayAdapter = new WeatherArrayAdapter(this, weatherList);
        weatherListView.setAdapter(weatherArrayAdapter);

        // Configurar FloatingActionButton para ocultar teclado e iniciar consulta web
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener texto de locationEditText y crear la URL para la petición
                EditText locationEditText = (EditText) findViewById(R.id.locationEditText);
                final URL url = createURL(locationEditText.getText().toString());

                // Ocultar teclado e iniciar descarga si la URL es válida
                if (url != null) {
                    dismissKeyboard(locationEditText);
                    fetchWeather(url);
                } else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout),
                            R.string.invalid_url, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow();
    }

    // Ocultar teclado programáticamente
    private void dismissKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private URL createURL(String city) {
        String apiKey = getString(R.string.api_key);
        String baseUrl = getString(R.string.web_service_url);

        try {
            // crear URL con la ciudad en unidades métricas (Celsius), en español y para 16 días
            String urlString = baseUrl + URLEncoder.encode(city, "UTF-8") +
                    "&units=metric&lang=es&cnt=16&APPID=" + apiKey;
            return new URL(urlString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // URL malformada
    }
    // Ejecutar la petición HTTP en hilo de fondo con ExecutorService (reemplaza AsyncTask)
    private void fetchWeather(final URL url) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final JSONObject result = doWeatherRequest(url);

                // Volver al hilo principal para actualizar la UI
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null) {
                            convertJSONtoArrayList(result);
                            weatherArrayAdapter.notifyDataSetChanged();
                            weatherListView.smoothScrollToPosition(0);
                        } else {
                            Snackbar.make(findViewById(R.id.coordinatorLayout),
                                    R.string.connect_error, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    // Realiza la petición HTTP y devuelve el JSON (se ejecuta en hilo de fondo)
    private JSONObject doWeatherRequest(URL url) {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            int response = connection.getResponseCode();

            if (response == HttpURLConnection.HTTP_OK) {
                StringBuilder builder = new StringBuilder();

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                }

                return new JSONObject(builder.toString());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return null;
    }

    // Extraer datos del clima del JSON y poblar weatherList (Adaptado para API Gratuita)
    private void convertJSONtoArrayList(JSONObject forecast) {
        weatherList.clear(); // limpiar lista antigua

        try {
            JSONArray list = forecast.getJSONArray("list");

            // Convertir cada elemento del arreglo JSON a un objeto Weather
            for (int i = 0; i < list.length(); ++i) {
                JSONObject day = list.getJSONObject(i);

                // En la API gratis, las temperaturas vienen dentro de "main"
                JSONObject mainData = day.getJSONObject("main");
                JSONObject weather = day.getJSONArray("weather").getJSONObject(0);

                weatherList.add(new Weather(
                        day.getLong("dt"),
                        mainData.getDouble("temp_min"),
                        mainData.getDouble("temp_max"),
                        mainData.getDouble("humidity"),
                        weather.getString("description"),
                        weather.getString("icon")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
