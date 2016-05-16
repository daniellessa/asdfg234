package br.com.dalecom.agendamobile.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.helpers.FloatHelper;
import br.com.dalecom.agendamobile.helpers.Helper;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.LogUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EventActivity extends AppCompatActivity implements OnMapReadyCallback{


    private TextView professionalName, serviceName, servicePrice;
    private TextView serviceWeekDay, serviceLongDay, serviceHour;
    private TextView propertyStreet, propertyNumber, propertyCompl, propertyCity;
    private ImageView imageProfessional;
    private Button btnCancel;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Event event;
    private float lat, lng;

    @Inject
    public EventManager eventManager;

    @Inject
    public RestClient restClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AgendaMobileApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        event = eventManager.getCurrentEvent();
        lat = event.getProperty().getLat();
        lng = event.getProperty().getLng();
        setFindByIds();
        setCollapsingToolBar();
        setMap();
    }

    private void setFindByIds(){

        professionalName = (TextView) findViewById(R.id.name_professional);
        serviceName = (TextView) findViewById(R.id.service_name);
        servicePrice = (TextView) findViewById(R.id.service_price);
        serviceWeekDay = (TextView) findViewById(R.id.text_week);
        serviceLongDay = (TextView) findViewById(R.id.text_day);
        serviceHour = (TextView) findViewById(R.id.text_hour);
        propertyStreet = (TextView) findViewById(R.id.property_street);
        propertyNumber = (TextView) findViewById(R.id.property_number);
        propertyCompl = (TextView) findViewById(R.id.property_compl);
        propertyCity = (TextView) findViewById(R.id.property_city);
        btnCancel = (Button) findViewById(R.id.button_cancel_event);

        professionalName.setText(Helper.getFirstName(event.getUserProf().getName()));
        //professionType.setText(professional.getProfessionName());
        serviceName.setText(event.getService().getTitle());
        servicePrice.setText("R$ " + FloatHelper.formatarFloat(event.getService().getPrice()));
        serviceWeekDay.setText(DateHelper.getWeekDay(event.getStartAt()));
        serviceLongDay.setText(DateHelper.toStringFull(event.getStartAt()));
        serviceHour.setText(DateHelper.hourToString(event.getStartAt()));
        propertyStreet.setText(event.getProperty().getStreet());
        propertyNumber.setText(String.valueOf(event.getProperty().getNumber()));
        propertyCompl.setText(event.getProperty().getInfo());
        propertyCity.setText(" - " + event.getProperty().getCity());


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEvent(v);
            }
        });


    }

    private void cancelEvent(final View view){

        restClient.cancelEvent(event, new Callback<JsonObject>(){

            @Override
            public void success(JsonObject jsonObject, Response response) {
                Snackbar.make(view, "Agendamento cancelado com sucesso", Snackbar.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Snackbar.make(view, "Ops! Algo de errado aconteceu. Tente Novamente", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void setMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setCollapsingToolBar(){

        User userProf = eventManager.getCurrentEvent().getUserProf();

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(LogUtils.TAG, "Map entrou");
        if (map != null) {
            map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)))
                    .setTitle(event.getProperty().getName());
              //      .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.logo_icalendar_primary));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12.0f));
        }
    }
}
