package br.com.dalecom.agendamobile.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.adapters.PropertiesAdapter;
import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.FileUtils;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.RecyclerItemClickListener;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;
import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CircleImageView imageView;
    private TextView userName;
    private Calendar dateSelected = Calendar.getInstance();
    private User currentUser;
    private Property currentProperty;
    private NavigationView navigationView;
    private List<Property> mList;
    private PropertiesAdapter adapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;


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
        setRecyclerView();
        setMenuOptions();
    }

    private void setRecyclerView(){

        mList = getallProperties();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_properties);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new PropertiesAdapter(this,mList);
        mRecyclerView.setAdapter(adapter);


        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d(LogUtils.TAG, "Property: " + mList.get(position).getName());
                        sharedPreference.setCurrentProperty(mList.get(position));
                        Log.d(LogUtils.TAG, "sharedPreference Property: " + sharedPreference.getCurrentProperty().getName());

                        Calendar currentDate = Calendar.getInstance();
                        eventManager.startNewEvent(currentDate);
                        eventManager.setCurrentProperty(mList.get(position));

                        //Intent it = new Intent(HomeActivity.this, ProfessionalsActivity.class);
                        Intent it = new Intent(HomeActivity.this, PropertyActivity.class);
                        startActivity(it);
                    }
                })
        );

    }

    public List<Property> getallProperties(){
        return new Select()
                .from(Property.class)
                .orderBy("name ASC")
                .execute();
    }


    private void populateFindByIds(){
        imageView = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.icon_perfil);
        userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name_user);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(HomeActivity.this, UpDateImageActivity.class);
                startActivity(it);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent it = new Intent(HomeActivity.this, NewPropertyActivity.class);
                startActivity(it);

            }
        });

    }

    public void setMenuOptions(){
        RelativeLayout alerts = (RelativeLayout) navigationView.getHeaderView(0).findViewById(R.id.alerts);
        alerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent it = new Intent(HomeActivity.this,.class);
//                startActivity(it);
            }
        });
        RelativeLayout myAppointments = (RelativeLayout) navigationView.getHeaderView(0).findViewById(R.id.myAppointments);
        myAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to
            }
        });
        RelativeLayout settings = (RelativeLayout) navigationView.getHeaderView(0).findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to settings
            }
        });
        RelativeLayout logout = (RelativeLayout) navigationView.getHeaderView(0).findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreference.clearUserToken();
                Intent it = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(it);
            }
        });

    }

    private void setUserOnNavigation(){
        currentUser = sharedPreference.getCurrentUser();

        try {
            userName.setText(currentUser.getName());

            if(currentUser.getLocalImageLocation().length() > 0){
                imageView.setImageURI(Uri.parse(currentUser.getLocalImageLocation()));
                Log.d(LogUtils.TAG, "Path image CurrentUser: " + currentUser.getLocalImageLocation());
            }

            if(currentUser.getBucketPath().length() == 0 && currentUser.getPhotoPath().length() > 0){
                new DownloadImageGoogle().execute();
            }
        }catch (NullPointerException ex){
            Intent it = new Intent(this,LoginActivity.class);
            startActivity(it);
        }


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
        adapter.swap(getallProperties());
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
            case R.id.mycalendar:
                it = new Intent(this,CalendarProfessionalActivity.class);
                startActivity(it);
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

                break;
        }
        

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class DownloadImageGoogle extends AsyncTask<Void, Void, Void> {

        private Bitmap image;
        @Override
        protected Void doInBackground(Void... params) {

            try {
                InputStream is = new URL(currentUser.getPhotoPath()).openStream();
                image = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (image != null)
                imageView.setImageBitmap(image);
        }
    }
}
