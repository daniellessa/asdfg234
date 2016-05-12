package br.com.dalecom.agendamobile.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
import br.com.dalecom.agendamobile.wrappers.SharedPreference;
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

    @Inject
    SharedPreference sharedPreference;

    class VHBusy extends RecyclerView.ViewHolder {

        protected CircleImageView imagePerfil;
        protected TextView startAt;
        protected Button btnCancel;

        public VHBusy(View itemView) {
            super(itemView);
            imagePerfil = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
            startAt = (TextView) itemView.findViewById(R.id.time_startAt);
            btnCancel = (Button) itemView.findViewById(R.id.btn_cancel_event);
        }
    }

    class VHBusyMy extends RecyclerView.ViewHolder {

        protected CircleImageView imagePerfil;
        protected TextView startAt;
        protected Button btnCancel;

        public VHBusyMy(View itemView) {
            super(itemView);
            imagePerfil = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
            startAt = (TextView) itemView.findViewById(R.id.time_startAt);
            btnCancel = (Button) itemView.findViewById(R.id.btn_cancel_event);
        }
    }

    class VHFree extends RecyclerView.ViewHolder {

        protected CircleImageView imagePerfil;
        protected TextView startAt;
        protected RelativeLayout background;
        protected Button btnCancel;


        public VHFree(View itemView) {
            super(itemView);
            imagePerfil = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
            startAt = (TextView) itemView.findViewById(R.id.time_startAt);
            btnCancel = (Button) itemView.findViewById(R.id.btn_cancel_event);
        }
    }

    class VHLunch extends RecyclerView.ViewHolder {

        protected CircleImageView imagePerfil;
        protected TextView startAt;
        protected RelativeLayout background;
        protected Button btnCancel;


        public VHLunch(View itemView) {
            super(itemView);
            imagePerfil = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
            startAt = (TextView) itemView.findViewById(R.id.time_startAt);
            btnCancel = (Button) itemView.findViewById(R.id.btn_cancel_event);
        }
    }

    class VHInvalidHour extends RecyclerView.ViewHolder {

        protected CircleImageView imagePerfil;
        protected TextView startAt;
        protected RelativeLayout background;
        protected Button btnCancel;


        public VHInvalidHour(View itemView) {
            super(itemView);
            imagePerfil = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
            startAt = (TextView) itemView.findViewById(R.id.time_startAt);
            btnCancel = (Button) itemView.findViewById(R.id.btn_cancel_event);
        }
    }

    class VHBloqued extends RecyclerView.ViewHolder {

        protected CircleImageView imagePerfil;
        protected TextView startAt;
        protected RelativeLayout background;
        protected Button btnCancel;


        public VHBloqued(View itemView) {
            super(itemView);
            imagePerfil = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
            startAt = (TextView) itemView.findViewById(R.id.time_startAt);
            btnCancel = (Button) itemView.findViewById(R.id.btn_cancel_event);
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
        else if(viewType == S.TYPE_ITEM_MY){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_times, parent, false);
            return new VHBusyMy(view);
        }
        else if(viewType == S.TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_times, parent, false);
            return new VHFree(view);
        }
        else if(viewType == S.TYPE_LUNCH){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_times, parent, false);
            return new VHLunch(view);
        }
        else if(viewType == S.TYPE_INVALID_HOUR){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_times, parent, false);
            return new VHInvalidHour(view);
        }
        else if(viewType == S.TYPE_ITEM_BLOQUED){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_times, parent, false);
            return new VHBloqued(view);
        }


        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,final int position) {

        if(holder instanceof VHBusyMy){
            ((VHBusyMy) holder).startAt.setText(DateHelper.hourToString(mList.get(position).getStartAt()));
            ((VHBusyMy) holder).startAt.setTextColor(mContext.getResources().getColor(R.color.red_time));
            ((VHBusyMy) holder).imagePerfil.setVisibility(View.VISIBLE);
            ((VHBusyMy) holder).btnCancel.setVisibility(View.VISIBLE);

            if(sharedPreference.getCurrentUser().getLocalImageLocation().length() > 0){
                ((VHBusyMy) holder).imagePerfil.setImageURI(Uri.parse(sharedPreference.getCurrentUser().getLocalImageLocation()));
                Log.d(LogUtils.TAG, "Photo uri: "+ sharedPreference.getCurrentUser().getLocalImageLocation());
            }

        }
        else if(holder instanceof VHBusy){
            ((VHBusy) holder).startAt.setText(DateHelper.hourToString(mList.get(position).getStartAt()));
            ((VHBusy) holder).startAt.setTextColor(mContext.getResources().getColor(R.color.red_time));
        }
        else if(holder instanceof VHFree){
            ((VHFree) holder).startAt.setText(DateHelper.hourToString(mList.get(position).getStartAt()));
        }

        else if(holder instanceof VHLunch){
            ((VHLunch) holder).startAt.setText(DateHelper.hourToString(mList.get(position).getStartAt()));
            ((VHLunch) holder).startAt.setTextColor(mContext.getResources().getColor(R.color.red_time));
        }

        else if(holder instanceof VHInvalidHour){
            ((VHInvalidHour) holder).startAt.setText(DateHelper.hourToString(mList.get(position).getStartAt()));
            ((VHInvalidHour) holder).startAt.setTextColor(mContext.getResources().getColor(R.color.red_time));
        }

        else if(holder instanceof VHBloqued){
            ((VHBloqued) holder).startAt.setText(DateHelper.hourToString(mList.get(position).getStartAt()));
            ((VHBloqued) holder).startAt.setTextColor(mContext.getResources().getColor(R.color.red_time));
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getViewType();
    }


    public void swap(List<Times> times){
        mList.clear();
        mList.addAll(times);
        notifyDataSetChanged();
    }

//    private class DownloadImageGoogleOrFacebook extends AsyncTask<Void, Void, Void> {
//
//        private Bitmap image;
//
//        public DownloadImageGoogleOrFacebook(Bitmap image){
//            this.image = image;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            try {
//                InputStream is = new URL(sharedPreference.getCurrentUser().getPhotoPath()).openStream();
//                image = BitmapFactory.decodeStream(is);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//    }





}
