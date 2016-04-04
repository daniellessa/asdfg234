package br.com.dalecom.agendamobile.utils;

import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.dalecom.agendamobile.ui.HomeActivity;


/**
 * Created by guilhermeduartemattos on 11/27/15.
 */
public class FlowUtils {


    public static void destroyExam(final Context context) {

        Intent homeIntent = new Intent(context, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(homeIntent);
    }


    public static String transformDateFormat(String date, String inputFormat, String outputFormat) {

        SimpleDateFormat format = new SimpleDateFormat(inputFormat);
        Date newDate = null;
        try {
            newDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }

        format = new SimpleDateFormat(outputFormat);
        String formattedDate = format.format(newDate);
        return formattedDate;
    }

    public static String transformDateFormatFromServer(String date, String outputFormat) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date newDate = null;
        try {
            newDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }

        format = new SimpleDateFormat(outputFormat);
        String formattedDate = format.format(newDate);
        return formattedDate;
    }
}
