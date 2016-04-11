package br.com.dalecom.agendamobile.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
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
import br.com.dalecom.agendamobile.fragments.DialogFragmentConfirmEvent;
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
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TimesActivity extends AppCompatActivity {


    private TextView week_day, dateView;
    private ImageView previousDay, nextDay;
    private CircleImageView imageProfessional;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Calendar dateSelected;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TimesAdapter adapter;
    private User userSelected;
    public ProgressDialog dialog;
    private List<Times> mList;
    private int id;
    private  CalendarTimes calendarTimes;
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
        setImageProfessional();
        setSwipeRefreshLayout();

    }

    private void setFindsByIds(){
        week_day = (TextView) findViewById(R.id.week_day);
        dateView = (TextView) findViewById(R.id.date);
        previousDay = (ImageView) findViewById(R.id.button_back_day);
        nextDay = (ImageView) findViewById(R.id.button_next_day);
        imageProfessional = (CircleImageView) findViewById(R.id.icon_perfil_professional);

        setDateHeader();


        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateSelected.add(Calendar.DAY_OF_MONTH, -1);
                eventManager.setDateSelected(dateSelected);
                updateRecyclerView(dateSelected);
                setDateHeader();
            }
        });

        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateSelected.add(Calendar.DAY_OF_MONTH, 1);
                eventManager.setDateSelected(dateSelected);
                updateRecyclerView(dateSelected);
                setDateHeader();
            }
        });
    }

    private void setImageProfessional(){
        User userProf = eventManager.getCurrentUserProfessional();
        if(userProf.getLocalImageLocation() != null)
            imageProfessional.setImageURI(Uri.parse(userProf.getLocalImageLocation()));
    }

    private void setDateHeader(){
        week_day.setText(DateHelper.getWeekDay(dateSelected));
        dateView.setText(DateHelper.toString(dateSelected));
    }

    private void populateEventsList(){
        mEvents.clear();
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
        mRecyclerView.invalidate();
        adapter.notifyDataSetChanged();
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

                            initConfirmDialog(startAt, endstAt);
                        }
                    }
                })
        );




    }

    private Callback callbackPostEvents = new Callback<JsonObject>(){

        @Override
        public void success(JsonObject jsonObject, Response response) {
            Log.d(LogUtils.TAG,"Success postEvent: "+ response.getStatus());
            updateRecyclerView(dateSelected);
            initDialog(startAt, endstAt);
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LogUtils.TAG,"Erro postEvent: "+ error);
        }
    };


    private void initConfirmDialog(Calendar startDate, Calendar endsDate){

        String message = "Você confirma o agendamento deste serviço para o dia " + DateHelper.toString(startDate) + " das " + DateHelper.hourToString(startDate) + " às " + DateHelper.hourToString(endsDate) + "?" ;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DialogFragmentConfirmEvent cdf = new DialogFragmentConfirmEvent(this,message,callbackPostEvents);
        cdf.show(ft, "dialog");
    }


    private void initDialog(Calendar startDate, Calendar endsDate){

        String title = "Parabéns!";
        String message = "Agendamento realizado com sucesso para o dia " + DateHelper.toString(startDate) + " das " + DateHelper.hourToString(startDate) + " às " + DateHelper.hourToString(endsDate) ;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        CustomDialogFragment cdf = new CustomDialogFragment(title,message);
        cdf.show(ft, "dialog");
    }

    private void updateRecyclerView(final Calendar dateSelected){

        dialog = ProgressDialog.show(TimesActivity.this,"Aguarde","Carregando agenda...",false,true);
        restClient.getEvents(userSelected.getIdServer(), DateHelper.toStringSql(dateSelected), new Callback<JsonArray>() {


            @Override
            public void success(JsonArray jsonArray, Response response) {

                EventParser eventParser = new EventParser(jsonArray);
                List<Event> mEvents = eventParser.parseFullEvents();
                CalendarTimes calendarTimes = new CalendarTimes(TimesActivity.this, mEvents);
                List<Times> times = calendarTimes.construct();
                adapter.swap(times);

                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                dialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();
            }
        });
    }

    private void setSwipeRefreshLayout(){
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {
                refreshItems();
            }

            void refreshItems() {
                updateRecyclerView(dateSelected);
            }

        });


    }



    @Override
    protected void onResume() {
        super.onResume();
        //updateRecyclerView(dateSelected);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
