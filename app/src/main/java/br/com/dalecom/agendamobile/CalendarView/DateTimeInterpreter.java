package br.com.dalecom.agendamobile.CalendarView;

import java.util.Calendar;

/**
 * Created by daniellessa on 11/05/16.
 */
public interface DateTimeInterpreter {
    String interpretDate(Calendar date);
    String interpretTime(int hour);
    String interpretTime(int hour, int minutes);
}
