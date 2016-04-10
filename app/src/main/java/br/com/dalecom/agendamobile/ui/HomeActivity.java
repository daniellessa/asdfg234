package br.com.dalecom.agendamobile.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import java.util.Calendar;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.FileUtils;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;
import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MaterialCalendarView calendarView;
    private CircleImageView imageView, imagePropertyView;
    private TextView propertyName, userName, userEmail;
    private Calendar dateSelected = Calendar.getInstance();
    private User currentUser;
    private Property currentProperty;
    private NavigationView navigationView;


    @Inject public EventManager eventManager;
    @Inject public FileUtils fileUtils;
    @Inject public SharedPreference sharedPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AgendaMobileApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        populateFindByIds();
        setUserOnNavigation();
        setCalendar();
        setCurrentProperty();
    }

    private void setCalendar(){
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        calendarView.setWeekDayLabels(getResources().getStringArray(R.array.weeks_array));
        calendarView.setTitleMonths(getResources().getStringArray(R.array.months_array));
        calendarView.setSelectionColor(R.color.colorPrimary);


        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                date.copyTo(dateSelected);

                eventManager.startNewEvent(dateSelected);

                Intent it = new Intent(HomeActivity.this, ProfessionalsActivity.class);
                it.putExtra("dateSelected", dateSelected);
                startActivity(it);
            }
        });
    }

    private void populateFindByIds(){
        imageView = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.icon_perfil);
        userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name_user);
        userEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email_user);
        propertyName = (TextView) findViewById(R.id.name_property_home);
        imagePropertyView = (CircleImageView) findViewById(R.id.icon_property);
    }

    private void setUserOnNavigation(){
        currentUser = sharedPreference.getCurrentUser();

        userName.setText(currentUser.getName());
        userEmail.setText(currentUser.getEmail());
//        User userTemp = currentUser.getUserByServerId(currentUser.getIdServer());
//         if(userTemp.getLocalImageLocation() != null) {
//             imageView.setImageURI(Uri.parse(userTemp.getLocalImageLocation()));
//         }
    }

    private void setCurrentProperty(){
        currentProperty = sharedPreference.getCurrentProperty();

        if(currentProperty.getName().length() > 0){
            //imagePropertyView.setImageURI();
            propertyName.setText(currentProperty.getName());
        }
    }

    public void listarProperties (View view){
        Intent it = new Intent(this, ProperiesActivity.class);
        startActivity(it);
    }
    public void listarProperties (){
        Intent it = new Intent(this, ProperiesActivity.class);
        startActivity(it);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCurrentProperty();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent it;

        switch (id){
            case R.id.account:

                break;
            case R.id.mycalendar:

                break;
            case R.id.nav_slideshow:

                break;
            case R.id.settings:

                break;
            case R.id.history:

                break;
            case R.id.properties:
                listarProperties();
                break;
            case R.id.logout:
                sharedPreference.clearUserToken();
                it = new Intent(this, LoginActivity.class);
                startActivity(it);
                break;
        }
        

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
