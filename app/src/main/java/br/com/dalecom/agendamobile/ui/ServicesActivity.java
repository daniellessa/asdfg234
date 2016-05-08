package br.com.dalecom.agendamobile.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.JsonArray;

import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.adapters.ServiceAdapter;
import br.com.dalecom.agendamobile.model.Service;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.RecyclerItemClickListener;
import br.com.dalecom.agendamobile.utils.ServicesParser;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ServicesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ServiceAdapter adapter;
    private Toolbar toolbar;
    private List<Service> mList;
    private ProgressDialog dialog;

    @Inject
    EventManager eventManager;

    @Inject
    RestClient restClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AgendaMobileApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_services);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setSubtitle("realizados pelo profissional");

        restClient.getServiceForProperty(eventManager.getCurrentProperty().getIdServer(), callbackServices);
        dialog = ProgressDialog.show(ServicesActivity.this, "Aguarde", "Carregando Serviços...", false, true);


    }

    private Callback callbackServices = new Callback<JsonArray>(){

        @Override
        public void success(JsonArray jsonArray, Response response) {

            ServicesParser servicesParser = new ServicesParser(jsonArray);
            mList = servicesParser.parseFullServices();
            setRecyclerView();
            dialog.dismiss();
        }

        @Override
        public void failure(RetrofitError error) {
            //configurar view para list vazia
            dialog.dismiss();
        }
    };

    private void setRecyclerView(){

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_services);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new ServiceAdapter(this,mList);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        eventManager.setServiceIntoEvent(mList.get(position));
                        initAgenda();
                    }
                })
        );

    }

    private void initAgenda(){
        Intent it = new Intent(ServicesActivity.this, TimesActivity.class);
        startActivity(it);
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
