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
import br.com.dalecom.agendamobile.utils.S;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by daniellessa on 24/03/16.
 */
public class TimesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Times> mList;
    private Context mContext;
    private ImageLoader imageLoader;
    private Calendar dateSelected;

    @Inject
    RestClient restClient;

    @Inject
    EventManager eventManager;

    class VHBusy extends RecyclerView.ViewHolder {

        protected CircleImageView imagePerfil;
        protected TextView startAt, endsAt, userName;
        protected RelativeLayout background;

        public VHBusy(View itemView) {
            super(itemView);
            startAt = (TextView) itemView.findViewById(R.id.time_startAt);
            background = (RelativeLayout) itemView.findViewById(R.id.background_times);
        }
    }

    class VHFree extends RecyclerView.ViewHolder {

        protected CircleImageView imagePerfil;
        protected TextView startAt, endsAt, userName;
        protected RelativeLayout background;


        public VHFree(View itemView) {
            super(itemView);
            startAt = (TextView) itemView.findViewById(R.id.time_startAt);
        }
    }

    public TimesAdapter(Context mContext, List list, Calendar date){
        this.mContext = mContext;
        ((AgendaMobileApplication) mContext.getApplicationContext()).getAppComponent().inject(this);
        this.mList = list;
        this.dateSelected = date;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == S.TYPE_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_times, parent, false);
            return new VHBusy(view);
        }
        else if(viewType == S.TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_times, parent, false);
            return new VHFree(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {

        if(holder instanceof VHBusy){
            ((VHBusy) holder).startAt.setText(DateHelper.hourToString(mList.get(position).getStartAt()));
            ((VHBusy) holder).startAt.setTextColor(mContext.getResources().getColor(R.color.red_time));
            //((VHBusy) holder).background.setVisibility(View.GONE);
        }
        else if(holder instanceof VHFree){
            ((VHFree) holder).startAt.setText(DateHelper.hourToString(mList.get(position).getStartAt()));
            //((VHFree) holder).startAt.setTextColor(mContext.getResources().getColor(R.color.green_time));

        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(mList.get(position).getViewType() == S.TYPE_HEADER)
            return S.TYPE_HEADER;
        return S.TYPE_ITEM;
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
