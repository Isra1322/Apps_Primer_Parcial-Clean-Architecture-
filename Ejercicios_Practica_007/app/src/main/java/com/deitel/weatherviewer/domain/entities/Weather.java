package com.deitel.weatherviewer.domain.entities;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Weather {
    public final String dayOfWeek;
    public final String minTemp;
    public final String maxTemp;
    public final String humidity;
    public final String description;
    public final String iconURL;

    public Weather(long timeStamp, double minTemp, double maxTemp, double humidity, String description, String iconName) {
        // Formateador de números para redondear temperaturas a enteros
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);

        this.dayOfWeek = convertTimeStampToDay(timeStamp);
        this.minTemp = numberFormat.format(minTemp) + "\u00B0C";
        this.maxTemp = numberFormat.format(maxTemp) + "\u00B0C";
        
        // Formato para la humedad (porcentaje)
        this.humidity = NumberFormat.getPercentInstance().format(humidity / 100.0);
        this.description = description;
        this.iconURL = "https://openweathermap.org/img/w/" + iconName + ".png";
    }

    private static String convertTimeStampToDay(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp * 1000);
        TimeZone tz = TimeZone.getDefault();
        
        // Ajustar por la zona horaria del dispositivo
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));

        // Formato de fecha para obtener el nombre del día de la semana (ej. Lunes) en español
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE", new Locale("es", "ES"));
        
        // Poner en mayúscula la primera letra del día
        String dayName = dateFormatter.format(calendar.getTime());
        if (dayName != null && dayName.length() > 0) {
            dayName = dayName.substring(0, 1).toUpperCase() + dayName.substring(1);
        }
        return dayName;
    }
}
