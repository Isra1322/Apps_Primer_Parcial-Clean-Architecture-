package com.deitel.weatherviewer.presentation;

import com.deitel.weatherviewer.R;

import com.deitel.weatherviewer.domain.entities.Weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherArrayAdapter extends ArrayAdapter<Weather> {

    // Clase para reutilizar vistas mientras los elementos de la lista se desplazan en pantalla
    private static class ViewHolder {
        ImageView conditionImageView;
        TextView dayTextView;
        TextView lowTextView;
        TextView hiTextView;
        TextView humidityTextView;
    }

    // Almacena los Bitmaps ya descargados para reutilización
    private final Map<String, Bitmap> bitmaps = new HashMap<>();

    // ExecutorService compartido para descargar imágenes (reemplaza AsyncTask)
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public WeatherArrayAdapter(Context context, List<Weather> forecast) {
        super(context, -1, forecast);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener objeto Weather para la posición especificada
        Weather day = getItem(position);

        ViewHolder viewHolder; // Objeto que referencia las vistas del elemento de la lista

        // Verificar si hay un ViewHolder reutilizable desde un ítem de lista desplazado; si no, crear uno
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);

            viewHolder.conditionImageView = (ImageView) convertView.findViewById(R.id.conditionImageView);
            viewHolder.dayTextView = (TextView) convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView = (TextView) convertView.findViewById(R.id.lowTextView);
            viewHolder.hiTextView = (TextView) convertView.findViewById(R.id.hiTextView);
            viewHolder.humidityTextView = (TextView) convertView.findViewById(R.id.humidityTextView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Si el ícono de condición del clima ya se descargó, usarlo; sino, descargarlo en otro hilo
        if (bitmaps.containsKey(day.iconURL)) {
            viewHolder.conditionImageView.setImageBitmap(bitmaps.get(day.iconURL));
        } else {
            // Guardar referencia al ImageView para actualizar en UI thread
            final ImageView targetImageView = viewHolder.conditionImageView;
            final String iconUrl = day.iconURL;
            targetImageView.setImageBitmap(null); // limpiar imagen anterior

            loadImageInBackground(iconUrl, targetImageView);
        }

        // Obtener otros datos del objeto Weather y colocarlos en las vistas correspondientes
        Context context = getContext();
        viewHolder.dayTextView.setText(context.getString(R.string.day_description, day.dayOfWeek, day.description));
        viewHolder.lowTextView.setText(context.getString(R.string.low_temp, day.minTemp));
        viewHolder.hiTextView.setText(context.getString(R.string.high_temp, day.maxTemp));
        viewHolder.humidityTextView.setText(context.getString(R.string.humidity, day.humidity));

        return convertView; // Retornar elemento de lista completado
    }

    // Cargar imagen en hilo de fondo y actualizar ImageView en el hilo principal
    // (reemplaza la clase interna LoadImageTask extends AsyncTask)
    private void loadImageInBackground(final String urlString, final ImageView imageView) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                HttpURLConnection connection = null;

                try {
                    URL url = new URL(urlString);
                    connection = (HttpURLConnection) url.openConnection();

                    try (InputStream inputStream = connection.getInputStream()) {
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        if (bitmap != null) {
                            bitmaps.put(urlString, bitmap); // almacenar en caché
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null)
                        connection.disconnect();
                }

                // Actualizar la UI en el hilo principal
                final Bitmap finalBitmap = bitmap;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (finalBitmap != null) {
                            imageView.setImageBitmap(finalBitmap);
                        }
                    }
                });
            }
        });
    }
}
