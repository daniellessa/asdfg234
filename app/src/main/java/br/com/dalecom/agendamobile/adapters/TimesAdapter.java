package br.com.dalecom.agendamobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.fragments.DialogFragmentConfirmEvent;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.Times;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.ui.EventActivity;
import br.com.dalecom.agendamobile.ui.TimesActivity;
import br.com.dalecom.agendamobile.utils.CalendarTimes;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.EventParser;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.S;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by daniellessa on 24/03/16.
 */
public class TimesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Times> mList;
    private List<Event> mListEvents;
    private Calendar startAt, endstAt;
    private Context mContext;
    private ImageLoader imageLoader;
    private Calendar dateSelected;

    @Inject
    RestClient restClient;

    @Inject
    EventManager eventManager;

    @Inject
    SharedPreference sharedPreference;

    class VHBusy extends RecyclerView.ViewHolder {

        protected CircleImageView imagePerfil;
        protected TextView startAt;
        protected Button btnCancel;

        public VHBusy(View itemView) {
            super(itemView);
            imagePerfil = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
            startAt = (TextView) itemView.findViewById(R.id.time_startAt);
            btnCancel = (Button) itemView.findViewById(R.id.btn_cancel_event);
        }
    }

    class VHBusyMy extends RecyclerView.ViewHolder {

        protected CircleImageView imagePerfil;
        protected TextView startAt;
        protected RelativeLayout btnCancel;

        public VHBusyMy(View itemView) {
            super(itemView);
            imagePerfil = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
            startAt = (TextView) itemView.findViewById(R.id.time_startAt);
            btnCancel = (RelativeLayout) itemView.findViewById(R.id.btn_cancel_event);
        }
    }

    class VHFree extends RecyclerView.ViewHolder {

        protected CircleImageView imagePerfil;
        protected TextView startAt;
        protected RelativeLayout background;
        protected Button btnCancel;


        public VHFree(View itemView) {
            super(itemView);
            imagePerfil = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
            startAt = (TextView) itemView.findViewById(R.id.time_startAt);
            btnCancel = (Button) itemView.findViewById(R.id.btn_cancel_event);
            background = (RelativeLayout) itemView.findViewById(R.id.background_times);
        }
    }

    class VHLunch extends RecyclerView.ViewHolder {

        protected CircleImageView imagePerfil;
        protected TextView startAt;
        protected RelativeLayout background;
        protected Button btnCancel;


        public VHLunch(View itemView) {
            super(itemView);
            imagePerfil = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
            startAt = (TextView) itemView.findViewById(R.id.time_startAt);
            btnCancel = (Button) itemView.findViewById(R.id.btn_cancel_event);
            background = (RelativeLayout) itemView.findViewById(R.id.background_times);
        }
    }

    class VHInvalidHour extends RecyclerView.ViewHolder {

        protected CircleImageView imagePerfil;
        protected TextView startAt;
        protected RelativeLayout background;
        protected Button btnCancel;


        public VHInvalidHour(View itemView) {
            super(itemView);
            imagePerfil = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
            startAt = (TextView) itemView.findViewById(R.id.time_startAt);
            btnCancel = (Button) itemView.findViewById(R.id.btn_cancel_event);
            background = (RelativeLayout) itemView.findViewById(R.id.background_times);
        }
    }

    class VHBloqued extends RecyclerView.ViewHolder {

        protected CircleImageView imagePerfil;
        protected TextView startAt;
        protected RelativeLayout background;
        protected Button btnCancel;


        public VHBloqued(View itemView) {
            super(itemView);
            imagePerfil = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
            startAt = (TextView) itemView.findViewById(R.id.time_startAt);
            btnCancel = (Button) itemView.findViewById(R.id.btn_cancel_event);
            background = (RelativeLayout) itemView.findViewById(R.id.background_times);
        }
    }

    public TimesAdapter(Context mContext, List list, List<Event> listEvents, Calendar date){
        this.mContext = mContext;
        ((AgendaMobileApplication) mContext.getApplicationContext()).getAppComponent().inject(this);
        this.mList = list;
        this.mListEvents = listEvents;
        this.dateSelected = date;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == S.TYPE_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_times, parent, false);
            return new VHBusy(view);
        }
        else if(viewType == S.TYPE_ITEM_MY){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_times_my_event, parent, false);
            return new VHBusyMy(view);
        }
        else if(viewType == S.TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_times, parent, false);
            return new VHFree(view);
        }
        else if(viewType == S.TYPE_LUNCH){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_times, parent, false);
            return new VHLunch(view);
        }
        else if(viewType == S.TYPE_INVALID_HOUR){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_times, parent, false);
            return new VHInvalidHour(view);
        }
        else if(viewType == S.TYPE_ITEM_BLOQUED){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_times, parent, false);
            return new VHBloqued(view);
        }


        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,final int position) {

        if(holder instanceof VHBusyMy){
            ((VHBusyMy) holder).startAt.setText(DateHelper.hourToString(mList.get(position).getStartAt()));

            if(sharedPreference.getCurrentUser().getLocalImageLocation().length() > 0){
                ((VHBusyMy) holder).imagePerfil.setImageURI(Uri.parse(sharedPreference.getCurrentUser().getLocalImageLocation()));
                Log.d(LogUtils.TAG, "Photo uri: "+ sharedPreference.getCurrentUser().getLocalImageLocation());
            }

            ((VHBusyMy) holder).btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToEventActivity(mList.get(position).getEvent());
                }
            });

        }
        else if(holder instanceof VHBusy){
            ((VHBusy) holder).startAt.setText(DateHelper.hourToString(mList.get(position).getStartAt()));
            ((VHBusy) holder).startAt.setTextColor(mContext.getResources().getColor(R.color.red_time));
        }
        else if(holder instanceof VHFree){

            ((VHFree) holder).startAt.setText(DateHelper.hourToString(mList.get(position).getStartAt()));
            ((VHFree) holder).background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Agendamento ok", Toast.LENGTH_LONG).show();
                    //createEvent(mList.get(position));
                }
            });
        }

        else if(holder instanceof VHLunch){
            ((VHLunch) holder).startAt.setText(DateHelper.hourToString(mList.get(position).getStartAt()));
            ((VHLunch) holder).startAt.setTextColor(mContext.getResources().getColor(R.color.red_time));
        }

        else if(holder instanceof VHInvalidHour){
            ((VHInvalidHour) holder).startAt.setText(DateHelper.hourToString(mList.get(position).getStartAt()));
            ((VHInvalidHour) holder).startAt.setTextColor(mContext.getResources().getColor(R.color.red_time));
        }

        else if(holder instanceof VHBloqued){
            ((VHBloqued) holder).startAt.setText(DateHelper.hourToString(mList.get(position).getStartAt()));
            ((VHBloqued) holder).startAt.setTextColor(mContext.getResources().getColor(R.color.red_time));
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getViewType();
    }


    public void swap(List<Times> times){
        mList.clear();
        mList.addAll(times);
        notifyDataSetChanged();
    }

    private void goToEventActivity(Event event){

        event.setUserProf(eventManager.getCurrentUserProfessional());
        eventManager.setCurrentEvent(event);
        Intent it = new Intent(mContext, EventActivity.class);
        mContext.startActivity(it);

    }

    private void createEvent(Times time){

        CalendarTimes calendarTimes = new CalendarTimes(mContext, mList);

        Log.d(LogUtils.TAG, "Date selected: " + time.getStartAt() + " " + time.getEndsAt());
        if (calendarTimes.checkDisponible(mContext, time)) {

            startAt = DateHelper.copyDate(dateSelected);
            startAt.set(Calendar.HOUR_OF_DAY, time.getStartAt().get(Calendar.HOUR_OF_DAY));
            startAt.set(Calendar.MINUTE, time.getStartAt().get(Calendar.MINUTE));

            endstAt = DateHelper.copyDate(startAt);
            endstAt.add(Calendar.HOUR_OF_DAY, eventManager.getCurrentService().getHours());
            endstAt.add(Calendar.MINUTE, eventManager.getCurrentService().getMinutes());

            eventManager.setCurrentStartAt(DateHelper.copyDate(startAt));
            eventManager.setCurrentEndsAt(DateHelper.copyDate(endstAt));
            eventManager.finalizeEvent();

            Log.d(LogUtils.TAG, "Start date: " + DateHelper.convertDateToStringSql(startAt));
            Log.d(LogUtils.TAG, "Ends date: " + DateHelper.convertDateToStringSql(endstAt));

            initConfirmDialog(startAt, endstAt);
        }
    }

    private void initConfirmDialog(Calendar startDate, Calendar endsDate){

//        String message = "Você confirma o agendamento deste serviço para o dia " + DateHelper.toString(startDate) + " das " + DateHelper.hourToString(startDate) + " às " + DateHelper.hourToString(endsDate) + "?" ;
//        FragmentTransaction ft = mContext.getSupportFragmentManager().beginTransaction();
//        DialogFragmentConfirmEvent cdf = new DialogFragmentConfirmEvent(this,message,callbackPostEvents);
//        cdf.show(ft, "dialog");
    }

    private Callback callbackPostEvents = new Callback<JsonObject>(){

        @Override
        public void success(JsonObject jsonObject, Response response) {
            Log.d(LogUtils.TAG,"Success postEvent: "+ response.getStatus());
//            updateRecyclerView(dateSelected);
//            initDialog(startAt, endstAt);

            restClient.notifyNewEvent(eventManager.getCurrentUserProfessional().getIdServer(), new Callback<JsonObject>() {

                @Override
                public void success(JsonObject jsonObject, Response response) {
                    Log.d(LogUtils.TAG, "notificationEvent: SUCESS");
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(LogUtils.TAG, "notificationEvent: FAIL: "+ error);
                }
            });
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LogUtils.TAG,"Erro postEvent: "+ error);
        }
    };

}
