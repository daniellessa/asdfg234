package br.com.dalecom.agendamobile.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
import br.com.dalecom.agendamobile.model.Professional;
import br.com.dalecom.agendamobile.model.Service;
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
    private Professional userProf;
    private List<Event> mEvents;
    List<Times> list;
    private Context mContext;
    private Service service;

    @Inject EventManager eventManager;

    @Inject RestClient restClient;



    public CalendarTimes(Context mContext, List<Event> listEvents) {
        mEvents = listEvents;
        this.mContext = mContext;
        ((AgendaMobileApplication) mContext.getApplicationContext()).getAppComponent().inject(this);
        populateVariables();
    }

    private void populateVariables(){
        userProf = eventManager.getCurrentProfessional();
        startAt = copyDate(userProf.getStartAt());
        endsAt = copyDate(userProf.getEndsAt());
        split = copyDate(userProf.getSplit());
        interval = userProf.getInterval();
        service = eventManager.getCurrentService();

    }


    public List<Times> construct(){


        list = new ArrayList<>();
        list.clear();
        Calendar auxStart = copyDate(startAt);
        Calendar auxEnd = copyDate(startAt);
        Calendar auxTarget = copyDate(startAt);
        auxTarget.add(Calendar.HOUR_OF_DAY, split.get(Calendar.HOUR_OF_DAY));
        auxTarget.add(Calendar.MINUTE, split.get(Calendar.MINUTE));
        int next = 1;


        while (startAt.before(endsAt)){

            boolean find = false;

            for (Event event: mEvents){

                Date eventStart = DateHelper.convertStringSqlInDate(event.getStartAt());

                if(DateHelper.hourToString(startAt).equals(DateHelper.hourToString(eventStart))){

                    if(DateHelper.hourToString(auxEnd).equals(DateHelper.hourToString(auxTarget))){
                        list.add(createFreeTime(auxStart, auxEnd));
                    }

                    list.add(populateTime(startAt, event.getEndsAt(), event.getUser().getName()));
                    Date end = DateHelper.convertStringSqlInDate(event.getEndsAt());
                    startAt.add(Calendar.HOUR_OF_DAY,end.getHours() - eventStart.getHours());
                    startAt.add(Calendar.MINUTE, (end.getMinutes() - eventStart.getMinutes()) + interval.get(Calendar.MINUTE));

                    auxStart = copyDate(startAt);
                    auxEnd = copyDate(startAt);
                    auxTarget = copyDate(startAt);
                    auxTarget.add(Calendar.HOUR_OF_DAY, split.get(Calendar.HOUR_OF_DAY));
                    auxTarget.add(Calendar.MINUTE, split.get(Calendar.MINUTE));
                    auxEnd.add(Calendar.MINUTE, next);
                    find = true;
                    break;
                }
            }

            if(!find){

                if(DateHelper.hourToString(auxEnd).equals(DateHelper.hourToString(auxTarget))){
                    list.add(createFreeTime(auxStart, auxEnd));

                    auxStart = copyDate(startAt);
                    auxEnd = copyDate(startAt);
                    auxTarget = copyDate(startAt);
                    auxTarget.add(Calendar.HOUR_OF_DAY, split.get(Calendar.HOUR_OF_DAY));
                    auxTarget.add(Calendar.MINUTE, split.get(Calendar.MINUTE));
                }
                auxEnd.add(Calendar.MINUTE, next);
            }

            startAt.add(Calendar.MINUTE, next);

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

    private Times createFreeTime(Calendar startAt, Calendar endsAt){

        Times time = new Times();
        time.setViewType(0);
        time.setStartAt(DateHelper.calendarToDate(startAt));
        time.setEndsAt(DateHelper.calendarToDate(endsAt));
        time.setUserName(mContext.getResources().getString(R.string.available));
        time.setFree(true);


        return time;
    }

    public boolean checkDisponible(Context context, Times selectedTime){

        if(selectedTime.isFree()){
            for (int i=0; i < list.size(); i++){
                if(list.get(i).getStartAt() == selectedTime.getStartAt()){
                    int duration = (service.getHours()*60) + service.getMinutes();
                    int splitMinutes = (split.get(Calendar.HOUR_OF_DAY)*60) + split.get(Calendar.MINUTE);
                    int checkNextIndex = (int) Math.round(((double)duration / splitMinutes)+0.5d);//duration/splitMinutes;

                    for (int x=0; x < checkNextIndex; x++){
                        try {
                            if(!list.get(i+x).isFree()){
                                Toast.makeText(context,"Tempo insuficente para este serviço",Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        }catch (IndexOutOfBoundsException e){
                            Toast.makeText(context,"Tempo insuficente para este serviço",Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }else{
            Toast.makeText(context,"Este horário não está disponível",Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    private Calendar copyDate(Calendar date){
        Calendar newDate = Calendar.getInstance();
        newDate.set(date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DAY_OF_MONTH),date.get(Calendar.HOUR_OF_DAY),date.get(Calendar.MINUTE),0);
        return newDate;
    }



}
