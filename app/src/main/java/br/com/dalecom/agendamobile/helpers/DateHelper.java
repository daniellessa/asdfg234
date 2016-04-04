package br.com.dalecom.agendamobile.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by daniellessa on 24/03/16.
 */
public class DateHelper {

    private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String convertDateToStringSql(Calendar date){
        return date.get(Calendar.YEAR)+"-"+(date.get(Calendar.MONTH)+1)+"-"+date.get(Calendar.DAY_OF_MONTH)+" "+date.get(Calendar.HOUR_OF_DAY)+":"+date.get(Calendar.MINUTE)+":"+date.get(Calendar.SECOND);
    }

    public static String convertDateToStringSql(Date date){
        return date.getYear()+"-"+(date.getMonth()+1)+"-"+date.getDay()+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    }

    public static Date convertStringSqlInDate(String dateSql){

        Date date = null;
        try {
            date = format.parse(dateSql);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String toString(Calendar date){
        String result = null;
        String day = String.valueOf(date.get(Calendar.DAY_OF_MONTH));
        String month = String.valueOf(date.get(Calendar.MONTH)+1);
        String year = String.valueOf(date.get(Calendar.YEAR));

        if(day.length() == 1){
            day = "0"+day;
        }
        if(month.length() == 1){
            month = "0"+month;
        }
        result = day+"/"+month+"/"+year;

        return result;
    }

    public static String hourToString(Calendar date){
        String r = null;
        String hour, minute;
        hour = String.valueOf(date.get(Calendar.HOUR_OF_DAY));
        minute = String.valueOf(date.get(Calendar.MINUTE));

        if(hour.length() == 1){
            hour = "0"+hour;
        }

        if(minute.length() == 1){
            minute = "0"+minute;
        }

        r = hour+":"+minute+" hs";

        return r;
    }

    public static String hourToString(Date date){
        String r = null;
        String hour, minute;
        hour = String.valueOf(date.getHours());
        minute = String.valueOf(date.getMinutes());

        if(hour.length() == 1){
            hour = "0"+hour;
        }

        if(minute.length() == 1){
            minute = "0"+minute;
        }

        r = hour+":"+minute+" hs";

        return r;
    }

    public static Date calendarToDate(Calendar date){
        Date newDate = null;
        try {
            newDate = format.parse(date.get(Calendar.YEAR)
                    +"-"+(date.get(Calendar.MONTH)+1)
                    +"-"+date.get(Calendar.DAY_OF_MONTH)
                    +" "+date.get(Calendar.HOUR_OF_DAY)
                    +":"+date.get(Calendar.MINUTE)
                    +":"+date.get(Calendar.SECOND));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String getWeekDay(Calendar date){
        String result = null;

        switch(date.get(Calendar.DAY_OF_WEEK)){
            case 1:
                result = "Domingo";
                break;
            case 2:
                result = "Segunda-feira";
                break;
            case 3:
                result = "Terça-feira";
                break;
            case 4:
                result = "Quarta-feira";
                break;
            case 5:
                result = "Quinta-feira";
                break;
            case 6:
                result = "Sexta-feira";
                break;
            case 7:
                result = "Sábado";
                break;
        }

        return result;
    }
}
