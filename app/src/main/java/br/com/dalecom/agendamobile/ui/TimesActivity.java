package br.com.dalecom.agendamobile.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.adapters.TimesAdapter;
import br.com.dalecom.agendamobile.fragments.CustomDialogFragment;
import br.com.dalecom.agendamobile.fragments.DialogFragmentConfirmEvent;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.helpers.Helper;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.Times;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.CalendarTimes;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.EventParser;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.RecyclerItemClickListener;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TimesActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, DialogInterface.OnCancelListener{

    private TextView dayPrevius, dayCurrent, dayNext, weekDayPrevius, weekDayCurrent, weekDayNext, monthCurrent, professionalName, professionType;
    private RelativeLayout previusLayout, nextLayout, daysLayout;
    private ImageView imageProfessional;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Calendar dateSelected;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TimesAdapter adapter;
    private User userSelected;
    private RelativeLayout layoutProgressBar;
    private List<Times> mList;
    private  CalendarTimes calendarTimes;
    private List<Event> mEvents = new ArrayList<>();
    private Calendar startAt, endstAt;
    private FloatingActionButton calendarPicker;
    private float initPosition;
    private float finalPosition;

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

        setFindByIds();
        dateSelected = eventManager.getDateSelected();
        userSelected = eventManager.getCurrentUserProfessional();
        populateEventsList();
        setCollapsingToolBar();
        setFragmentDays();


    }

    private void setFindByIds(){
        layoutProgressBar = (RelativeLayout) findViewById(R.id.layout_progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_times);
        calendarPicker = (FloatingActionButton) findViewById(R.id.fab);
        professionalName = (TextView) findViewById(R.id.name_professional);
        professionType = (TextView) findViewById(R.id.professional_type);


        professionalName.setText(Helper.getFirstName(eventManager.getCurrentUserProfessional().getName()));
        professionType.setText(eventManager.getCurrentProfessional().getProfessionName());

        calendarPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDatePicker();
            }
        });

    }


    private void setCollapsingToolBar(){

        User userProf = eventManager.getCurrentUserProfessional();

        imageProfessional = (ImageView) findViewById(R.id.icon_professional);

        if(userProf.getLocalImageLocation() != null)
            imageProfessional.setImageURI(Uri.parse(userProf.getLocalImageLocation()));

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @TargetApi(Build.VERSION_CODES.M)
    private void setFragmentDays(){

        monthCurrent = (TextView) findViewById(R.id.month);
        dayPrevius = (TextView) findViewById(R.id.number_day_previus);
        dayCurrent = (TextView) findViewById(R.id.number_day);
        dayNext = (TextView) findViewById(R.id.number_day_next);
        weekDayPrevius = (TextView) findViewById(R.id.week_day_previus);
        weekDayCurrent = (TextView) findViewById(R.id.week_day);
        weekDayNext = (TextView) findViewById(R.id.week_day_next);
        previusLayout = (RelativeLayout) findViewById(R.id.layout_previus);
        nextLayout = (RelativeLayout) findViewById(R.id.layout_next);
        daysLayout = (RelativeLayout) findViewById(R.id.layout_fragment_day);
        setCurrentDayFragment();

        previusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previusDay();
            }
        });

        nextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextDay();
            }
        });


        daysLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = MotionEventCompat.getActionMasked(event);

                switch (action) {
                    case (MotionEvent.ACTION_DOWN):
                        initPosition = event.getX();
                        return true;
                    case (MotionEvent.ACTION_MOVE):
                        return true;
                    case (MotionEvent.ACTION_UP):
                        finalPosition = event.getX();
                        if (initPosition < finalPosition) {
                            previusDay();
                        } else {
                           nextDay();
                        }
                        return true;
                    case (MotionEvent.ACTION_CANCEL):
                        Log.d(LogUtils.TAG, "Action was CANCEL");
                        return true;
                }

                return false;
            }
        });
    }

    private void previusDay(){

        Calendar dateAux = DateHelper.copyDate(dateSelected);
        dateAux.add(Calendar.DAY_OF_MONTH, -1);
        Calendar currentDate = Calendar.getInstance();

//        if(!dateAux.before(currentDate)) {

            dateSelected.add(Calendar.DAY_OF_MONTH, -1);
            while (!DateHelper.isWorkDay(dateSelected, eventManager.getCurrentUserProfessional())) {
                dateSelected.add(Calendar.DAY_OF_MONTH, -1);
            }
            setCurrentDayFragment();
//        }
    }

    private void nextDay(){

        dateSelected.add(Calendar.DAY_OF_MONTH, 1);
        while (!DateHelper.isWorkDay(dateSelected, eventManager.getCurrentUserProfessional())) {
            dateSelected.add(Calendar.DAY_OF_MONTH, 1);
        }
        setCurrentDayFragment();
    }



    private void setCurrentDayFragment(){

        Calendar previusDate = DateHelper.copyDate(eventManager.getDateSelected());
        previusDate.add(Calendar.DAY_OF_MONTH, -1);

        Calendar date = DateHelper.copyDate(eventManager.getDateSelected());

        Calendar nextDate = DateHelper.copyDate(eventManager.getDateSelected());
        nextDate.add(Calendar.DAY_OF_MONTH, +1);

        weekDayPrevius.setText(DateHelper.getWeekDay(previusDate));
        weekDayCurrent.setText(DateHelper.getWeekDay(date));
        weekDayNext.setText(DateHelper.getWeekDay(nextDate));

        dayPrevius.setText(DateHelper.getDay(previusDate));
        dayCurrent.setText(DateHelper.getDay(date));
        dayNext.setText(DateHelper.getDay(nextDate));

        monthCurrent.setText(DateHelper.getMonth(date));

        updateRecyclerView(dateSelected);
    }


    private void populateEventsList(){
        mEvents.clear();
        restClient.getEvents(eventManager.getCurrentUserProfessional().getProfessional().getIdServer(), DateHelper.toStringSql(eventManager.getDateSelected()), callbackEvents);
        layoutProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private Callback callbackEvents = new Callback<JsonArray>(){

        @Override
        public void success(JsonArray jsonArray, Response response) {
            EventParser eventParser = new EventParser(jsonArray);
            mEvents = eventParser.parseFullEvents();
            setRecyclerView();
            layoutProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void failure(RetrofitError error) {
            mEvents = new ArrayList<>();
            setRecyclerView();
            layoutProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    };

    private void setRecyclerView(){


        calendarTimes = new CalendarTimes(this, mEvents, dateSelected);
        mList = calendarTimes.construct();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_times);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new TimesAdapter(this,mList,mEvents,dateSelected);
        mRecyclerView.invalidate();
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);

//        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//
//                        Times time = mList.get(position);
//
//                        Log.d(LogUtils.TAG, "Date selected: " + time.getStartAt() + " " + time.getEndsAt());
//                        if (calendarTimes.checkDisponible(TimesActivity.this, time)) {
//
//                            startAt = DateHelper.copyDate(dateSelected);
//                            startAt.set(Calendar.HOUR_OF_DAY, time.getStartAt().get(Calendar.HOUR_OF_DAY));
//                            startAt.set(Calendar.MINUTE, time.getStartAt().get(Calendar.MINUTE));
//
//                            endstAt = DateHelper.copyDate(startAt);
//                            endstAt.add(Calendar.HOUR_OF_DAY, eventManager.getCurrentService().getHours());
//                            endstAt.add(Calendar.MINUTE, eventManager.getCurrentService().getMinutes());
//
//                            eventManager.setCurrentStartAt(DateHelper.copyDate(startAt));
//                            eventManager.setCurrentEndsAt(DateHelper.copyDate(endstAt));
//                            eventManager.finalizeEvent();
//
//                            Log.d(LogUtils.TAG, "Start date: " + DateHelper.convertDateToStringSql(startAt));
//                            Log.d(LogUtils.TAG, "Ends date: " + DateHelper.convertDateToStringSql(endstAt));
//
//                            initConfirmDialog(startAt, endstAt);
//                        }
//                    }
//                })
//        );
    }

    private Callback callbackPostEvents = new Callback<JsonObject>(){

        @Override
        public void success(JsonObject jsonObject, Response response) {
            Log.d(LogUtils.TAG,"Success postEvent: "+ response.getStatus());
            updateRecyclerView(dateSelected);
            initDialog(startAt, endstAt);

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

        layoutProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        restClient.getEvents(eventManager.getCurrentUserProfessional().getProfessional().getIdServer(), DateHelper.toStringSql(dateSelected), new Callback<JsonArray>() {


            @Override
            public void success(JsonArray jsonArray, Response response) {

                EventParser eventParser = new EventParser(jsonArray);
                List<Event> mEvents = eventParser.parseFullEvents();
                CalendarTimes calendarTimes = new CalendarTimes(TimesActivity.this, mEvents, dateSelected);
                List<Times> times = calendarTimes.construct();

                if(times != null)
                    if (adapter != null)
                        adapter.swap(times);

                layoutProgressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(RetrofitError error) {
                layoutProgressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
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
            case R.id.home_acticity:
                Intent it = new Intent(this, HomeActivity.class);
                startActivity(it);

                break;
            case android.R.id.home:
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void initDatePicker(){
        Calendar cDefault = DateHelper.copyDate(dateSelected);

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.MONTH, 3);

        List<Calendar> daysList = new LinkedList<>();
        Calendar[] daysArray;
        Calendar cAux = Calendar.getInstance();

        while (cAux.getTimeInMillis() <= maxDate.getTimeInMillis()){
            if(DateHelper.isWorkDay(cAux, eventManager.getCurrentUserProfessional())){
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(cAux.getTimeInMillis());

                daysList.add(c);
            }
            cAux.setTimeInMillis(cAux.getTimeInMillis() + (24 * 60 * 60 * 1000));
        }

        daysArray = new Calendar[daysList.size()];
        for (int i = daysArray.length - 1; i >=  0; i --){
            daysArray[i] = daysList.get(i);
        }

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                this,
                cDefault.get(Calendar.YEAR),
                cDefault.get(Calendar.MONTH),
                cDefault.get(Calendar.YEAR)
        );

        datePickerDialog.setMinDate(minDate);
        datePickerDialog.setSelectableDays(daysArray);

        datePickerDialog.initialize(
            this, cDefault.get(Calendar.YEAR),
            cDefault.get(Calendar.MONTH),
            cDefault.get(Calendar.YEAR)
        );

        datePickerDialog.show(getFragmentManager(), "DatePickerDialog");



    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        dateSelected.set(Calendar.YEAR, year);
        dateSelected.set(Calendar.MONTH, monthOfYear);
        dateSelected.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setCurrentDayFragment();
    }
}
