package br.com.dalecom.agendamobile.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.dalecom.agendamobile.model.User;

/**
 * Created by daniellessa on 24/03/16.
 */
public class DateHelper {

    private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void makeIgualsDate(Calendar dateForChange, Calendar dateSelected){
        dateForChange.set(Calendar.DAY_OF_MONTH, dateSelected.get(Calendar.DAY_OF_MONTH));
        dateForChange.set(Calendar.MONTH, dateSelected.get(Calendar.MONTH));
        dateForChange.set(Calendar.YEAR, dateSelected.get(Calendar.YEAR));
    }


    public static String convertDateToStringSql(Calendar date){
        String year = String.valueOf(date.get(Calendar.YEAR));
        String month = String.valueOf(date.get(Calendar.MONTH)+1);
        String day = String.valueOf(date.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(date.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(date.get(Calendar.MINUTE));
        String seconds = "00";

        if(year.length() == 1){
            year= "0"+year;
        }
        if(month.length() == 1){
            month= "0"+month;
        }
        if(day.length() == 1){
            day= "0"+day;
        }
        if(hour.length() == 1){
            hour= "0"+hour;
        }
        if(minute.length() == 1){
            minute= "0"+minute;
        }
        return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+seconds;
    }

    public static String convertDateToStringSql(Date date){
        String year = String.valueOf(date.getYear());
        String month = String.valueOf(date.getMonth()+1);
        String day = String.valueOf(date.getDay());
        String hour = String.valueOf(date.getHours());
        String minute = String.valueOf(date.getMinutes());
        String seconds = "00";

        if(year.length() == 1){
            year= "0"+year;
        }
        if(month.length() == 1){
            month= "0"+month;
        }
        if(day.length() == 1){
            day= "0"+day;
        }
        if(hour.length() == 1){
            hour= "0"+hour;
        }
        if(minute.length() == 1){
            minute= "0"+minute;
        }
        return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+seconds;
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

    public static Calendar convertStringSqlInCalendar(String dateSql){
        Calendar date = Calendar.getInstance();
        date.setTime(convertStringSqlInDate(dateSql));
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

    public static String toString(String dateString){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar date = Calendar.getInstance();
        try {
            date.setTime(format.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    public static String toStringFull(Calendar date){
        String result = null;
        String day = String.valueOf(date.get(Calendar.DAY_OF_MONTH));
        String month = String.valueOf(date.get(Calendar.MONTH)+1);
        String year = String.valueOf(date.get(Calendar.YEAR));
        String hour = String.valueOf(date.get(Calendar.HOUR_OF_DAY));
        String min = String.valueOf(date.get(Calendar.MINUTE));

        if(day.length() == 1){
            day = "0"+day;
        }
        if(month.length() == 1){
            month = "0"+month;
        }
        if(hour.length() == 1){
            hour = "0"+hour;
        }
        if(min.length() == 1){
            min = "0"+min;
        }


        result = day+" de "+getMonth(date)+" de "+year;

        return result;
    }

    public static String getDay(Calendar date){
        String day = String.valueOf(date.get(Calendar.DAY_OF_MONTH));
        if(day.length() == 1){
            day = "0"+day;
        }
        return day;
    }

    public static String toStringSql(Calendar date){
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
        result = year+"-"+month+"-"+day;

        return result;
    }

    public static String toStringSql(Date date){
        String result = null;
        String day = String.valueOf(date.getDay());
        String month = String.valueOf(date.getMonth()+1);
        String year = String.valueOf(date.getYear());

        if(day.length() == 1){
            day = "0"+day;
        }
        if(month.length() == 1){
            month = "0"+month;
        }
        result = year+"-"+month+"-"+day;

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

        r = hour+":"+minute;

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

        r = hour+":"+minute;

        return r;
    }

    public static String hourToString(String date){
        String r = null;
        String[] pasts = date.split(":");
        String hour = pasts[0];
        String minute = pasts[1];

        if(hour.length() == 1){
            hour = "0"+hour;
        }

        if(minute.length() == 1){
            minute = "0"+minute;
        }

        r = hour+":"+minute;

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

    public static String getMonth(Calendar date){
        String result = null;

        switch(date.get(Calendar.MONTH) + 1){
            case 1:
                result = "Janeiro";
                break;
            case 2:
                result = "Fevereiro";
                break;
            case 3:
                result = "Março";
                break;
            case 4:
                result = "Abril";
                break;
            case 5:
                result = "Maio";
                break;
            case 6:
                result = "Junho";
                break;
            case 7:
                result = "Julho";
                break;
            case 8:
                result = "Agosto";
                break;
            case 9:
                result = "Setembro";
                break;
            case 10:
                result = "Outubro";
                break;
            case 11:
                result = "Novembro";
                break;
            case 12:
                result = "Dezembro";
                break;
        }

        return result;
    }

    public static Calendar copyDate(Calendar date){
        Calendar newDate = Calendar.getInstance();
        newDate.set(date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DAY_OF_MONTH),date.get(Calendar.HOUR_OF_DAY),date.get(Calendar.MINUTE),0);
        return newDate;
    }

    public static boolean isWorkDay(Calendar day, User user){

        switch (day.get(Calendar.DAY_OF_WEEK)){
            case 1:
                if(user.getProfessional().isWorkSunday())
                    return true;
                else
                    return false;
            case 2:
                if(user.getProfessional().isWorkMonday())
                    return true;
                else
                    return false;
            case 3:
                if(user.getProfessional().isWorkTuesday())
                    return true;
                else
                    return false;
            case 4:
                if(user.getProfessional().isWorkWednesday())
                    return true;
                else
                    return false;
            case 5:
                if(user.getProfessional().isWorkThursday())
                    return true;
                else
                    return false;
            case 6:
                if(user.getProfessional().isWorkFriday())
                    return true;
                else
                    return false;
            case 7:
                if(user.getProfessional().isWorkSaturday())
                    return true;
                else
                    return false;

        }

        return false;
    }
}
