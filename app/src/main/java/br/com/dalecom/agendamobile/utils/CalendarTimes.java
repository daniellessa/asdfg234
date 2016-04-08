package br.com.dalecom.agendamobile.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.Times;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by daniellessa on 03/04/16.
 */
public class CalendarTimes {

    private Calendar startAt, endsAt, split, interval;
    private User userProf;
    private List<Event> mEvents;
    private Context mContext;

    @Inject EventManager eventManager;

    @Inject RestClient restClient;



    public CalendarTimes(Context mContext, List<Event> listEvents) {
        mEvents = listEvents;
        this.mContext = mContext;
        ((AgendaMobileApplication) mContext.getApplicationContext()).getAppComponent().inject(this);
        populateVariables();
    }

    private void populateVariables(){
        userProf = eventManager.getUserProf();
        startAt = userProf.getProfessional().getStartAt();
        endsAt = userProf.getProfessional().getEndsAt();
        split = userProf.getProfessional().getSplit();
        interval = userProf.getProfessional().getInterval();

    }


    public List<Times> construct(){


        List<Times> list = new ArrayList<>();
        list.clear();
        int count = 0;

        while (startAt.before(endsAt)){


            count++;
            for (int i=0; i < mEvents.size(); i++){

                Date eventStart = DateHelper.convertStringSqlInDate(mEvents.get(i).getStartAt());
                if(startAt.get(Calendar.HOUR_OF_DAY) == eventStart.getHours() && startAt.get(Calendar.MINUTE) == eventStart.getMinutes()){

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

        Log.d(LogUtils.TAG,"Count: "+ count);
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
