package br.com.dalecom.agendamobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.Times;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.CalendarTimes;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.EventParser;
import br.com.dalecom.agendamobile.utils.LogUtils;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by daniellessa on 24/03/16.
 */
public class TimesAdapter extends RecyclerView.Adapter<TimesAdapter.MyViewHolder> {

    private List<Times> mList;
    private Context mContext;
    private ImageLoader imageLoader;
    private Calendar dateSelected;

    @Inject
    RestClient restClient;

    @Inject
    EventManager eventManager;

    class MyViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView imagePerfil;
        protected TextView startAt, endsAt, userName;
        protected RelativeLayout background;

        public MyViewHolder(View itemView) {
            super(itemView);

            imagePerfil = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
            startAt = (TextView) itemView.findViewById(R.id.time_startAt);
            endsAt = (TextView) itemView.findViewById(R.id.time_endsAt);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            background = (RelativeLayout) itemView.findViewById(R.id.layout_background);

        }
    }

    public TimesAdapter(Context mContext, List list, Calendar date){
        this.mContext = mContext;
        ((AgendaMobileApplication) mContext.getApplicationContext()).getAppComponent().inject(this);
        this.mList = list;
        this.dateSelected = date;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_times, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {
        holder.startAt.setText(DateHelper.hourToString(mList.get(position).getStartAt()));
        holder.endsAt.setText(DateHelper.hourToString(mList.get(position).getEndsAt()));
        holder.userName.setText(mList.get(position).getUserName());

        if(mList.get(position).isFree()){
            //holder.userName.setTextColor(Color.parseColor("#009688"));
            holder.imagePerfil.setImageResource(R.drawable.user_default);
        }
        else{
            if(mList.get(position).getUserName() == "11")
                holder.userName.setText("Scarlett Johanson");
                holder.imagePerfil.setImageResource(R.drawable.scarlett);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void displayProfilePhotoByUri(String uriString, ImageView imageView) {
        imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .considerExifParams(true)
                .build();

        imageLoader.displayImage(uriString, imageView, options);
    }

    public void swap(List<Times> times){
        mList.clear();
        mList.addAll(times);
        notifyDataSetChanged();
    }





}
