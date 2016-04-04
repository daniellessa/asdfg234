package br.com.dalecom.agendamobile.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.adapters.ProfessionalsAdapter;
import br.com.dalecom.agendamobile.model.Professional;
import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.ObjectTest;
import br.com.dalecom.agendamobile.utils.ProfessionalParser;
import br.com.dalecom.agendamobile.utils.RecyclerItemClickListener;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfessionalsActivity extends AppCompatActivity {

    private Calendar dateSelected;
    private RecyclerView mRecyclerView;
    private RelativeLayout layoutNull;
    private TextView textViewNull;
    public ProgressDialog dialog;
    private RecyclerView.LayoutManager layoutManager;
    private ProfessionalsAdapter adapter;
    private Toolbar toolbar;
    private Property currentProperty;
    private List<User> mList;

    @Inject public RestClient restClient;

    @Inject public SharedPreference sharedPreference;

    @Inject public EventManager eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AgendaMobileApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_professionals);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setSubtitle("por tipo de serviço");
        layoutNull = (RelativeLayout) findViewById(R.id.layout_null);
        textViewNull = (TextView) findViewById(R.id.text_null);


        dateSelected = (Calendar) getIntent().getSerializableExtra("dateSelected");
        currentProperty = sharedPreference.getCurrentProperty();

        restClient.getProfessionals(currentProperty.getIdServer(), callbackProfessionals);
        dialog = ProgressDialog.show(ProfessionalsActivity.this,"Aguarde","Procurando profissionais...",false,true);

        eventManager.startNewEvent(dateSelected);

    }

    private void setRecyclerView(){

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_professionals);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new ProfessionalsAdapter(this,mList);
        mRecyclerView.setAdapter(adapter);


        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {


                        eventManager.setProfessionalIntoEvent(mList.get(position));
                        Intent it = new Intent(ProfessionalsActivity.this, TimesActivity.class);
                        startActivity(it);
                    }
                })
        );



    }

    private Callback callbackProfessionals = new Callback<JsonArray>(){


        @Override
        public void success(JsonArray jsonArray, Response response) {

            if(jsonArray.size() < 1){
                textViewNull.setText("Ops!\nNenhum profissional cadastrado neste estabelecimento.");
                layoutNull.setVisibility(View.VISIBLE);
            }

            ProfessionalParser professionalParser = new ProfessionalParser(jsonArray);
            mList = professionalParser.parseFullProfessionas();
            Log.d(LogUtils.TAG, "Value list: "+ mList.size());
            setRecyclerView();
            dialog.dismiss();

        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LogUtils.TAG, "Result request professionals FAIL: ");
            textViewNull.setText("Algo de errado aconteceu!\nVerifique sua conexão com a internet ou tente mais tarde.");
            layoutNull.setVisibility(View.VISIBLE);
            dialog.dismiss();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_professional, menu);
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
