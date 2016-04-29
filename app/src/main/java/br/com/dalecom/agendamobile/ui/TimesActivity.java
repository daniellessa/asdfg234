package br.com.dalecom.agendamobile.ui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.adapters.CalendarHorizontalAdapter;
import br.com.dalecom.agendamobile.adapters.ServiceAdapter;
import br.com.dalecom.agendamobile.adapters.TimesAdapter;
import br.com.dalecom.agendamobile.fragments.CustomDialogFragment;
import br.com.dalecom.agendamobile.fragments.DialogFragmentConfirmEvent;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.model.Day;
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
import br.com.dalecom.agendamobile.utils.S;
import br.com.dalecom.agendamobile.utils.ServicesParser;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TimesActivity extends AppCompatActivity {


    private ViewPager mViewPager;
    private TextView viewMonth, viewYear;
    private TextView dayPrevius, dayCurrent, dayNext, weekDayPrevius, weekDayCurrent, weekDayNext, monthCurrent;
    private RelativeLayout previusLayout, nextLayout, daysLayout;
    private ImageView imageProfessional, selectedHorizDay, nextMonth, previusMonth;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Calendar dateSelected;
    private RecyclerView mRecyclerView;
    //private RecyclerView.LayoutManager layoutManagerCalendarHoriz;
    //private RecyclerView mRecyclerViewCalendarHoriz;
    private RecyclerView.LayoutManager layoutManager;
    //private CalendarHorizontalAdapter adapterCalendarHoriz;
    private TimesAdapter adapter;
    private User userSelected;
    public ProgressDialog dialog;
    private List<Times> mList;
    private List<Day> mListCalendarHoriz;
    private  CalendarTimes calendarTimes;
    private List<Event> mEvents = new ArrayList<>();
    private Calendar startAt, endstAt;
    private ViewFlipper viewDays;

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


        dateSelected = eventManager.getDateSelected();
        userSelected = eventManager.getCurrentUserProfessional();
        populateEventsList();
        setCollapsingToolBar();
        //populateCalendarHorizontal();
        setFindByIds();
        setFragmentDays();

    }

    private void setCollapsingToolBar(){

        User userProf = eventManager.getCurrentUserProfessional();

        imageProfessional = (ImageView) findViewById(R.id.icon_professional);

        if(userProf.getLocalImageLocation() != null)
            imageProfessional.setImageURI(Uri.parse(userProf.getLocalImageLocation()));

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.textWhite));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbarLayout.setTitle(userProf.getName());
        collapsingToolbarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listarProperties();
            }
        });

    }

    private void setFindByIds(){

        //viewMonth = (TextView) findViewById(R.id.calendar_month);
        //viewYear = (TextView) findViewById(R.id.calendar_year);

        //viewMonth.setText(DateHelper.getMonth(dateSelected));
        //viewYear.setText(String.valueOf(dateSelected.get(Calendar.YEAR)));

        //previusMonth = (ImageView) findViewById(R.id.calendar_previus_month);
        //nextMonth = (ImageView) findViewById(R.id.calendar_next_month);


//
//        previusMonth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar currentDate = Calendar.getInstance();
//                dateSelected.add(Calendar.MONTH, -1);
//                viewMonth.setText(DateHelper.getMonth(dateSelected));
//                viewYear.setText(String.valueOf(dateSelected.get(Calendar.YEAR)));
//                //if(dateSelected.after(currentDate)){
//                updateCalendarHorizontal(dateSelected);
//                updateRecyclerView(dateSelected);
//                //}
//
//
//            }
//        });

//        nextMonth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar currentDate = Calendar.getInstance();
//                dateSelected.add(Calendar.MONTH, 1);
//                viewMonth.setText(DateHelper.getMonth(dateSelected));
//                viewYear.setText(String.valueOf(dateSelected.get(Calendar.YEAR)));
//                //if(dateSelected.after(currentDate)) {
//                    updateCalendarHorizontal(dateSelected);
//                    updateRecyclerView(dateSelected);
//               // }
//
//            }
//        });

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
                dateSelected.add(Calendar.DAY_OF_MONTH, -1);
                setCurrentDayFragment();
            }
        });

        nextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateSelected.add(Calendar.DAY_OF_MONTH, 1);
                setCurrentDayFragment();
            }
        });


//        daysLayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d(LogUtils.TAG, "OnTouchListener: "+event.getTouchMajor()+" - ");
//                return false;
//            }
//        });

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

//    private void populateCalendarHorizontal() {
//
//        mListCalendarHoriz = populateList(dateSelected);
//        //mListCalendarHoriz.get(0).setSelected(true);
//
//        mRecyclerViewCalendarHoriz = (RecyclerView) findViewById(R.id.recyclerView_calendar_horizontal);
//        layoutManagerCalendarHoriz = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        mRecyclerViewCalendarHoriz.setLayoutManager(layoutManagerCalendarHoriz);
//        adapterCalendarHoriz = new CalendarHorizontalAdapter(this,mListCalendarHoriz);
//        adapterCalendarHoriz.notifyDataSetChanged();
//        mRecyclerViewCalendarHoriz.setAdapter(adapterCalendarHoriz);
//
//
//        Calendar currentDate = Calendar.getInstance();
//        currentDate.add(Calendar.DAY_OF_MONTH, -1);
//        int position = currentDate.get(Calendar.DAY_OF_MONTH);
//        layoutManagerCalendarHoriz.scrollToPosition(position);
//
//        mRecyclerViewCalendarHoriz.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//                if(mListCalendarHoriz.get(position).getViewType() == S.TYPE_ITEM) {
//                    if (selectedHorizDay != null)
//                        selectedHorizDay.setBackground(null);
//
//                    selectedHorizDay = (ImageView) view.findViewById(R.id.background_day);
//                    selectedHorizDay.setBackground(getResources().getDrawable(R.drawable.background_item_calendar));
//
//                    dateSelected = DateHelper.copyDate(mListCalendarHoriz.get(position).getDate());
//                    eventManager.setDateSelected(dateSelected);
//                    updateRecyclerView(dateSelected);
//
//                    Toast.makeText(TimesActivity.this, DateHelper.toString(eventManager.getDateSelected()), Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(TimesActivity.this, "Data indisponível", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }));
//
//
//
//    }

//    private List<Day> populateList (Calendar date){
//
//        ArrayList<Day> listDay = new ArrayList<>();
//
//        int month = date.get(Calendar.MONTH) + 1;
//        int year = date.get(Calendar.YEAR);
//
//        Calendar startAt = DateHelper.copyDate(date);
//        startAt.set(Calendar.DAY_OF_MONTH, 1);
//        Calendar endsAt = DateHelper.copyDate(startAt);
//
//        if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)
//            endsAt.add(Calendar.DAY_OF_MONTH, 31);
//        else if(month == 4 || month == 6 || month == 9 || month == 11)
//            endsAt.add(Calendar.DAY_OF_MONTH, 30);
//        else if(month == 2 && !isBissexto(year))
//            endsAt.add(Calendar.DAY_OF_MONTH, 28);
//        else if(month == 2 && isBissexto(year))
//            endsAt.add(Calendar.DAY_OF_MONTH, 29);
//
//        while (startAt.before(endsAt)){
//            listDay.add(new Day(startAt));
//            startAt.add(Calendar.DAY_OF_MONTH, 1);
//        }
//
//        return listDay;
//    }
//
//    private boolean isBissexto(int ano){
//        boolean result = false;
//        if( ano % 400 == 0){
//            result = true;
//        }else if(ano%4 == 0 && ano%100!=0)
//            result = true;
//
//        return result;
//    }

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

                        Log.d(LogUtils.TAG,"Date selected: "+ time.getStartAt() + " " +time.getEndsAt());
                        if (calendarTimes.checkDisponible(TimesActivity.this, time)) {

                            startAt = DateHelper.copyDate(dateSelected);
                            startAt.set(Calendar.HOUR_OF_DAY, time.getStartAt().getHours());
                            startAt.set(Calendar.MINUTE, time.getStartAt().getMinutes());

                            endstAt = DateHelper.copyDate(startAt);
                            endstAt.add(Calendar.HOUR_OF_DAY, eventManager.getCurrentService().getHours());
                            endstAt.add(Calendar.MINUTE, eventManager.getCurrentService().getMinutes());

                            eventManager.setCurrentStartAt(DateHelper.convertDateToStringSql(startAt));
                            eventManager.setCurrentEndsAt(DateHelper.convertDateToStringSql(endstAt));
                            eventManager.finalizeEvent();

                            Log.d(LogUtils.TAG, "Start date: " + DateHelper.convertDateToStringSql(startAt));
                            Log.d(LogUtils.TAG, "Ends date: " + DateHelper.convertDateToStringSql(endstAt));

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
            //initDialog(startAt, endstAt);
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

                dialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();
            }
        });
    }

//    private void updateCalendarHorizontal(Calendar dateSelected){
//        List<Day> mDays = populateList(dateSelected);
//        adapterCalendarHoriz.swap(mDays);
//    }


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
            case R.id.change_professional:

                break;
            case android.R.id.home:
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }



}
