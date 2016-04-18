package br.com.dalecom.agendamobile.model;

import java.util.Calendar;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.utils.S;

/**
 * Created by daniellessa on 17/04/16.
 */
public class Day {

    private int id;
    private String day;
    private String weekDay;
    private Calendar date;
    private int viewType;


    public Day (Calendar date ){
        this.date = DateHelper.copyDate(date);
        this.day = initDay(this.date);
        this.weekDay = initWeekDay(this.date);

        Calendar currentDate = Calendar.getInstance();

        if(this.date.before(currentDate))
            this.viewType = S.TYPE_HEADER;
        else
            this.viewType = S.TYPE_ITEM;


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    private String initDay(Calendar date){
        String result = null;

        result = String.valueOf(date.get(Calendar.DAY_OF_MONTH));
        if (result.length() == 1){
            result = "0"+result;
        }
        return result;
    }

    private String initWeekDay(Calendar date){
        String result = null;

        switch (date.get(Calendar.DAY_OF_WEEK)){
            case 1:
                result = "DOM";
                break;
            case 2:
                result = "SEG";
                break;
            case 3:
                result = "TER";
                break;
            case 4:
                result = "QUA";
                break;
            case 5:
                result = "QUI";
                break;
            case 6:
                result = "SEX";
                break;
            case 7:
                result = "SÁB";
                break;
        }

        return result;
    }
}
