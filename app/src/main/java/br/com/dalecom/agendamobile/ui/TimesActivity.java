package br.com.dalecom.agendamobile.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.adapters.TimesAdapter;
import br.com.dalecom.agendamobile.fragments.CustomDialogFragment;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.Professional;
import br.com.dalecom.agendamobile.model.Service;
import br.com.dalecom.agendamobile.model.Times;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.CalendarTimes;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.EventParser;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.ObjectTest;
import br.com.dalecom.agendamobile.utils.RecyclerItemClickListener;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TimesActivity extends AppCompatActivity {

    private Calendar dateSelected;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TimesAdapter adapter;
    private User userSelected;
    public ProgressDialog dialog;
    private Toolbar toolbar;
    private List<Times> mList;
    private int id;
    private  CalendarTimes calendarTimes;
    private TextView week_day, dateView;
    private ImageView previousDay, nextDay;
    private List<Event> mEvents = new ArrayList<>();
    private Calendar startAt, endstAt;

    @Inject
    public EventManager eventManager;

    @Inject
    public RestClient restClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AgendaMobileApplication) getApplication()).getAppComponent().inject(this);

        setContentView(R.layout.activity_times);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Service serv = eventManager.getCurrentService();

        if(serv.getHours() > 0)
            toolbar.setSubtitle(serv.getTitle() + " - " + serv.getHours() + ":" +serv.getMinutes());
        else
            toolbar.setSubtitle(serv.getTitle()+" - "+serv.getMinutes()+" Min");

        dateSelected = eventManager.getDateSelected();
        userSelected = eventManager.getCurrentUserProfessional();
        populateEventsList();
        setFindsByIds();

    }

    private void setFindsByIds(){
        week_day = (TextView) findViewById(R.id.week_day);
        dateView = (TextView) findViewById(R.id.date);
        previousDay = (ImageView) findViewById(R.id.button_back_day);
        nextDay = (ImageView) findViewById(R.id.button_next_day);

        week_day.setText(DateHelper.getWeekDay(dateSelected));
        dateView.setText(DateHelper.toString(dateSelected));

        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //--Day
            }
        });

        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //++day
            }
        });
    }

    private void populateEventsList(){
        restClient.getEvents(userSelected.getIdServer(), DateHelper.toStringSql(eventManager.getDateSelected()), callbackEvents);
        dialog = ProgressDialog.show(TimesActivity.this,"Aguarde","Carregando agenda...",false,true);
    }

    private Callback callbackEvents = new Callback<JsonArray>(){

        @Override
        public void success(JsonArray jsonArray, Response response) {
            EventParser eventParser = new EventParser(jsonArray);
            mEvents = eventParser.parseFullEvents();
            setRecyclerView();
            dialog.dismiss();
        }

        @Override
        public void failure(RetrofitError error) {
            mEvents = new ArrayList<>();
            setRecyclerView();
            dialog.dismiss();
        }
    };


    private void setRecyclerView(){

        calendarTimes = new CalendarTimes(this, mEvents);
        mList = calendarTimes.construct();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_times);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new TimesAdapter(this,mList,dateSelected);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Times time = mList.get(position);
                        if (calendarTimes.checkDisponible(TimesActivity.this, time)) {

                            startAt = Calendar.getInstance();
                            startAt.setTime(time.getStartAt());

                            endstAt = Calendar.getInstance();
                            endstAt.setTime(time.getStartAt());
                            endstAt.add(Calendar.HOUR_OF_DAY, eventManager.getCurrentService().getHours());
                            endstAt.add(Calendar.MINUTE, eventManager.getCurrentService().getMinutes());

                            eventManager.setCurrentStartAt(DateHelper.convertDateToStringSql(startAt));
                            eventManager.setCurrentEndsAt(DateHelper.convertDateToStringSql(endstAt));
                            eventManager.finalizeEvent();

                            restClient.postEvent(eventManager.getEvent(), callbackPostEvents);
                        }

                    }
                })
        );


    }

    private Callback callbackPostEvents = new Callback<JsonObject>(){

        @Override
        public void success(JsonObject jsonObject, Response response) {
            Log.d(LogUtils.TAG,"Success postEvent: "+ response.getStatus());
            initDialog(startAt, endstAt);
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LogUtils.TAG,"Erro postEvent: "+ error);
        }
    };

    private void initDialog(Calendar startDate, Calendar endsDate){

        String title = "Parabéns!";
        String message = "Agendamento realizado com sucesso para o dia " + DateHelper.toString(startDate) + " das " + DateHelper.hourToString(startDate) + " às " + DateHelper.hourToString(endsDate) ;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        CustomDialogFragment cdf = new CustomDialogFragment(title,message);
        cdf.show(ft, "dialog");
    }



    @Override
    protected void onResume() {
        super.onResume();
        //populateEventsList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_times, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings:

                break;
            case android.R.id.home:
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

}
