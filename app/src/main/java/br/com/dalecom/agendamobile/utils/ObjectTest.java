package br.com.dalecom.agendamobile.utils;

import android.content.Context;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.Professional;
import br.com.dalecom.agendamobile.model.Service;
import br.com.dalecom.agendamobile.model.Times;
import br.com.dalecom.agendamobile.model.User;

/**
 * Created by daniellessa on 25/03/16.
 */
public class ObjectTest {

    public static final long HOUR = 3600*1000;
    public static final long MINUTE = 60*1000;

    public List<User> getAllProfessionals(){

        List<User> mList = new ArrayList<>();

        User user0 = new User();
        Professional p0 = new Professional();
        user0.setProfessional(p0);
        user0.getProfessional().setCategory(0);
        user0.setName("");
        mList.add(user0);

        User user1 = new User();
        Professional p1 = new Professional();
        user1.setProfessional(p1);
        user1.getProfessional().setCategory(2);
        user1.setName("Scarlett Johanson");
        mList.add(user1);

        User user2 = new User();
        Professional p2 = new Professional();
        user2.setProfessional(p2);
        user2.getProfessional().setCategory(5);
        user2.setName("Fernanda Cavalcante");
        mList.add(user2);

        User user3 = new User();
        Professional p3 = new Professional();
        user3.setProfessional(p3);
        user3.getProfessional().setCategory(4);
        user3.setName("Paulo Pinheiros");
        mList.add(user3);

        return mList;
    }



    public List<Times> createCalendar(User p, Context context){

        List<Event> mEvents = populateEvents();
        ArrayList<Times> mArray = new ArrayList<>();


        Calendar start = p.getProfessional().getStartAt();
        Calendar ends = p.getProfessional().getEndsAt();
        Calendar split = p.getProfessional().getSplit();
        boolean created = false;

            for (;start.before(ends);) {

                for (int i=0; i < mEvents.size(); i++) {

                    Date eventDate = DateHelper.convertStringSqlInDate(mEvents.get(i).getStartAt());

                    if (start.get(Calendar.HOUR_OF_DAY) == eventDate.getHours() && start.get(Calendar.MINUTE) == eventDate.getMinutes()) {

                        Times time = new Times();
                        time.setStartAt(eventDate);
                        time.setEndsAt(eventDate);
                        time.setUserName(mEvents.get(i).getUser().getName());
                        time.setFree(false);
                        mArray.add(time);
                        created = true;
                        break;
                    }
                }

                if(!created){
                    Times time = new Times();
                    Date startAt = new Date(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH), start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE));
                    time.setStartAt(startAt);
                    Date end = new Date(startAt.getTime() + split.get(Calendar.HOUR_OF_DAY) * HOUR);
                    end = new Date(end.getTime() + split.get(Calendar.MINUTE) * MINUTE);
                    time.setEndsAt(end);
                    time.setUserName(context.getResources().getString(R.string.available));
                    time.setFree(true);
                    mArray.add(time);
                }
                    created = false;
                    start.add(Calendar.HOUR_OF_DAY, split.get(Calendar.HOUR_OF_DAY));
                    start.add(Calendar.MINUTE, split.get(Calendar.MINUTE));
            }

        return mArray;
    }


    private User createUser(){
        User p = new User();
        Professional pp = new Professional();
        p.setProfessional(pp);
        p.setId(1);
        p.setName("Daniel Lessa");

        Calendar pcS = Calendar.getInstance();
        pcS.set(Calendar.HOUR_OF_DAY, 8);
        pcS.set(Calendar.MINUTE, 0);

        p.getProfessional().setStartAt(pcS);

        Calendar pcE = Calendar.getInstance();
        pcE.set(Calendar.HOUR_OF_DAY, 15);
        pcE.set(Calendar.MINUTE, 0);
        p.getProfessional().setEndsAt(pcE);

        Calendar pcSp = Calendar.getInstance();
        pcSp.set(Calendar.HOUR_OF_DAY, 0);
        pcSp.set(Calendar.MINUTE, 30);
        p.getProfessional().setSplit(pcSp);

        Calendar pcI = Calendar.getInstance();
        pcI.set(Calendar.HOUR_OF_DAY, 0);
        pcI.set(Calendar.MINUTE, 15);
        p.getProfessional().setInterval(pcI);

        return p;
    }

    private List<Event> populateEvents(){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<Event> mEvents = new ArrayList<>();

        Event a = new Event();
        User aa = new User();
        a.setUser(aa);
        a.getUser().setName("Scalertt Johanson");
        Date acS = new Date();

        try {
            acS = format.parse("2016-02-26 08:30");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        a.setStartAt(DateHelper.convertDateToStringSql(acS));

        Date end = new Date();
        try {
            end = format.parse("2016-02-26 09:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        a.setEndsAt(DateHelper.convertDateToStringSql(end));
        a.setFinalized(false);

        mEvents.add(a);

        Event b = new Event();
        User bb = new User();
        b.setUser(bb);
        b.getUser().setName("Scalertt Johanson");
        Date bcS = new Date();

        try {
            bcS = format.parse("2016-02-26 14:30");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        b.setStartAt(DateHelper.convertDateToStringSql(bcS));

        Date endb = new Date();
        try {
            endb = format.parse("2016-02-26 15:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        b.setEndsAt(DateHelper.convertDateToStringSql(endb));
        b.setFinalized(false);

        mEvents.add(b);

        Event c = new Event();
        User cc = new User();
        c.setUser(cc);
        c.getUser().setName("Scalertt Johanson");
        Date ccS = new Date();

        try {
            ccS = format.parse("2016-02-26 12:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setStartAt(DateHelper.convertDateToStringSql(ccS));

        Date endc = new Date();
        try {
            endc = format.parse("2016-02-26 12:30");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setEndsAt(DateHelper.convertDateToStringSql(endc));
        c.setFinalized(false);

        mEvents.add(c);

        return mEvents;
    }

    public ArrayList<Service> populateService(){
        ArrayList<Service> list = new ArrayList<>();

        Service service = new Service();
        service.setTitle("Corte Masculino");
        service.setMinutes(45);
        service.setPrice(new Float(25.00));
        list.add(service);

        Service service1 = new Service();
        service1.setTitle("Corte Feminino");
        service1.setMinutes(1);
        service1.setPrice(new Float(45.50));
        list.add(service1);

        Service service2 = new Service();
        service2.setTitle("Progressiva");
        service2.setMinutes(2);
        service2.setPrice(new Float(100.00));
        list.add(service2);

        Service service3 = new Service();
        service3.setTitle("Calterização");
        service3.setMinutes(50);
        service3.setPrice(new Float(60.00));
        list.add(service3);

        return list;
    }

}

