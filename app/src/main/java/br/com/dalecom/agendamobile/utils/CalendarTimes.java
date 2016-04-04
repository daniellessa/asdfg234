package br.com.dalecom.agendamobile.utils;

import android.content.Context;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.Times;

/**
 * Created by daniellessa on 03/04/16.
 */
public class CalendarTimes {

    Calendar startAt, endsAt, split, interval;
    List<Event> mEvents = new ArrayList<>();
    Context mContext;



    public CalendarTimes(Context mContext) {
        this.startAt = startAt;
        this.endsAt = endsAt;
        this.split = split;
        this.mContext = mContext;
        ((AgendaMobileApplication) mContext).getAppComponent().inject(this);
    }

    public List<Times> construct(){

        List<Times> list = new ArrayList<>();
        list.clear();


        while (startAt.before(endsAt)){
            String start = DateHelper.convertDateToStringSql(startAt);
            for (int i=0; i < mEvents.size(); i++){

                Date eventStart = DateHelper.convertStringSqlInDate(mEvents.get(i).getStartAt());
                if(start == eventStart.toString()){
                    //popular item
                    list.add(populateTime(startAt, mEvents.get(i).getEndsAt(), mEvents.get(i).getUser().getName()));
                    Date end = DateHelper.convertStringSqlInDate(mEvents.get(i).getEndsAt());
                    startAt.add(Calendar.HOUR_OF_DAY,end.getHours() - eventStart.getHours());
                    startAt.add(Calendar.MINUTE,(end.getMinutes() - eventStart.getMinutes()) + interval.get(Calendar.MINUTE));
                }else{

                    list.add(createFreeTime(startAt));
                    startAt.add(Calendar.HOUR_OF_DAY, split.get(Calendar.HOUR));
                    startAt.add(Calendar.MINUTE, split.get(Calendar.MINUTE));
                }
            }

        }
      return list;
    }

    private Times populateTime(Calendar startAt, String ends, String name){
        Times time = new Times();
        time.setViewType(1);
        time.setStartAt(DateHelper.calendarToDate(startAt));
        time.setEndsAt(DateHelper.convertStringSqlInDate(ends));
        time.setUserName(name);
        time.setFree(false);

        return time;
    }

    private Times createFreeTime(Calendar startAt){

        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, startAt.get(Calendar.HOUR_OF_DAY) + split.get(Calendar.HOUR));
        end.set(Calendar.MINUTE, startAt.get(Calendar.MINUTE) + split.get(Calendar.MINUTE));

        Times time = new Times();
        time.setViewType(0);
        time.setStartAt(DateHelper.calendarToDate(startAt));
        time.setEndsAt(DateHelper.calendarToDate(end));
        time.setUserName(mContext.getResources().getString(R.string.available));
        time.setFree(true);


        return time;
    }



}
