package br.com.dalecom.agendamobile.ui;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.adapters.TimesAdapter;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.model.Professional;
import br.com.dalecom.agendamobile.model.Times;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.utils.CalendarTimes;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.ObjectTest;
import br.com.dalecom.agendamobile.utils.RecyclerItemClickListener;

public class TimesActivity extends AppCompatActivity {

    private Calendar dateSelected;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TimesAdapter adapter;
    private User userSelected;
    private Toolbar toolbar;
    private List<Times> mList;
    private int id;
    private TextView week_day, dateView;
    private ImageView previousDay, nextDay;

    @Inject public EventManager eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AgendaMobileApplication) getApplication()).getAppComponent().inject(this);

        setContentView(R.layout.activity_times);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateSelected = eventManager.getDateSelected();
        userSelected = eventManager.getProfessional();
        setRecyclerView();
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

    private void setRecyclerView(){

        mList = new ObjectTest().createCalendar(userSelected, this);

//        CalendarTimes calendarTimes = new CalendarTimes(this);
//        mList = calendarTimes.construct();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_times);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new TimesAdapter(this,mList,dateSelected);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mList.get(position);

                        Calendar startDate = Calendar.getInstance();
                        startDate.setTime(mList.get(position).getStartAt());

                        Calendar endsDate = Calendar.getInstance();
                        endsDate.setTime(mList.get(position).getEndsAt());

                        Log.d(LogUtils.TAG, "Date: " + DateHelper.toString(startDate));
                        Log.d(LogUtils.TAG, "StartAt: " + DateHelper.hourToString(startDate));
                        Log.d(LogUtils.TAG, "EndsAt: " + DateHelper.hourToString(endsDate));

                        Intent it = new Intent(TimesActivity.this, ServicesActivity.class);
                        it.putExtra("startDate", startDate);
                        it.putExtra("endsDate", endsDate);

                        startActivity(it);
                    }
                })
        );


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
