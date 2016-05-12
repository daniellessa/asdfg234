package br.com.dalecom.agendamobile.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import br.com.dalecom.agendamobile.CalendarView.CalendarView;
import br.com.dalecom.agendamobile.CalendarView.MonthLoader;
import br.com.dalecom.agendamobile.CalendarView.ViewEvent;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.di.components.AppComponent;

/**
 * Created by daniellessa on 11/05/16.
 */
public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setMinTime(8);
        calendarView.setMaxTime(18);
        MonthLoader.MonthChangeListener monthChangeListener = new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends ViewEvent> onMonthChange(int newYear, int newMonth) {
                List<ViewEvent> events = new ArrayList<>();

                return events;
            }
        };
        calendarView.setMonthChangeListener(monthChangeListener);





    }
}
