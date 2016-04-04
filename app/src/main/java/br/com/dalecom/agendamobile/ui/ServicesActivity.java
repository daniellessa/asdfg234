package br.com.dalecom.agendamobile.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Calendar;
import java.util.List;

import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.adapters.ServiceAdapter;
import br.com.dalecom.agendamobile.fragments.CustomDialogFragment;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.model.Service;
import br.com.dalecom.agendamobile.utils.ObjectTest;
import br.com.dalecom.agendamobile.utils.RecyclerItemClickListener;

public class ServicesActivity extends AppCompatActivity {

    private Calendar dateSelected, startDate, endsDate ;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ServiceAdapter adapter;
    private Toolbar toolbar;
    private List<Service> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startDate = (Calendar) getIntent().getSerializableExtra("startDate");
        endsDate = (Calendar) getIntent().getSerializableExtra("endsDate");
        setRecyclerView();


    }

    private void setRecyclerView(){

        mList = new ObjectTest().populateService();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_services);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new ServiceAdapter(this,mList);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mList.get(position);

                        initDialog(startDate,endsDate);
                    }
                })
        );

    }

    private void initDialog(Calendar startDate, Calendar endsDate){

        String title = "Parabéns!";
        String message = "Agendamento realizado com sucesso para o dia " + DateHelper.toString(startDate) + " das " + DateHelper.hourToString(startDate) + " às " + DateHelper.hourToString(endsDate) ;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        CustomDialogFragment cdf = new CustomDialogFragment(title,message);
        cdf.show(ft, "dialog");
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
