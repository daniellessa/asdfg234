package br.com.dalecom.agendamobile.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.helpers.FloatHelper;
import br.com.dalecom.agendamobile.model.Alert;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.model.Service;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.S;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by daniellessa on 24/03/16.
 */
public class AlertAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Alert> mList;
    private Context mContext;
    private ImageLoader imageLoader;
    private RecyclerView mRecyclerView;

    public interface OnItemLongClickListener {
        public boolean onItemLongClicked(int position);
    }

    @Inject
    EventManager eventManager;

    class VHBusy extends RecyclerView.ViewHolder {

        protected CircleImageView imageProfile;
        protected TextView alertTitle, alertMessage, alertDate, alertHour;
        public View v;


        public VHBusy(View itemView) {
            super(itemView);
            imageProfile = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
            alertTitle = (TextView) itemView.findViewById(R.id.title_alert);
            alertMessage = (TextView) itemView.findViewById(R.id.message_alert);
            alertDate = (TextView) itemView.findViewById(R.id.date_alert);
            alertHour = (TextView) itemView.findViewById(R.id.time_alert);

            this.v = itemView;
        }
    }

    class VHFree extends RecyclerView.ViewHolder {

        protected CircleImageView imageProfile;
        protected ImageView deleteAlert;
        protected TextView alertTitle, alertMessage, alertDate, alertHour;
        public View v;


        public VHFree(View itemView) {
            super(itemView);
            imageProfile = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
            deleteAlert = (ImageView) itemView.findViewById(R.id.icon_delete_alert);
            alertTitle = (TextView) itemView.findViewById(R.id.title_alert);
            alertMessage = (TextView) itemView.findViewById(R.id.message_alert);
            alertDate = (TextView) itemView.findViewById(R.id.date_alert);
            alertHour = (TextView) itemView.findViewById(R.id.time_alert);

            this.v = itemView;
        }
    }

    public AlertAdapter(Context mContext, List list, RecyclerView recyclerView){
        this.mContext = mContext;
        ((AgendaMobileApplication) mContext.getApplicationContext()).getAppComponent().inject(this);
        this.mList = list;
        this.mRecyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == S.TYPE_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_alert, parent, false);
            return new VHBusy(view);
        }
        else if(viewType == S.TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_alert, parent, false);
            return new VHFree(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {

        if(holder instanceof VHBusy){
            //((VHBusy) holder).startAt.setText(DateHelper.hourToString(mList.get(position).getStartAt()));
            //((VHBusy) holder).startAt.setTextColor(mContext.getResources().getColor(R.color.red_time));
            //((VHBusy) holder).background.setVisibility(View.GONE);
        }
        else if(holder instanceof VHFree){

            ((VHFree) holder).alertTitle.setText(mList.get(position).getTitle());
            ((VHFree) holder).alertMessage.setText(mList.get(position).getMessage());
            ((VHFree) holder).alertDate.setText(mList.get(position).getDate());
            ((VHFree) holder).alertHour.setText(mList.get(position).getHour());

            switch (mList.get(position).getType()){
                case S.TYPE_NEW_ASSOCIATION:
                    ((VHFree) holder).imageProfile.setImageResource(R.drawable.person_flat);
                    break;
                case S.TYPE_NEW_EVENT:
                    ((VHFree) holder).imageProfile.setImageResource(R.drawable.calendar_flat);
                    break;
                case S.TYPE_NEW_PROMOTION:
                    ((VHFree) holder).imageProfile.setImageResource(R.drawable.gift_flat);
                    break;
            }

            ((VHFree) holder).deleteAlert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Alert alert = Alert.findOne(mList.get(position).getIdServer());

                    mList.remove(position);
                    mRecyclerView.removeViewAt(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mList.size());
                    alert.delete();

                }
            });

        }

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {

//        if(mList.get(position).getViewType() == S.TYPE_HEADER)
//            return S.TYPE_HEADER;
        return S.TYPE_ITEM;
    }

    private void displayProfilePhotoByUri(String uriString, ImageView imageView) {
        imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .considerExifParams(true)
                .build();

        imageLoader.displayImage(uriString, imageView, options);
    }

    public void swap(List<Alert> alerts){
        mList.clear();
        mList.addAll(alerts);
        notifyDataSetChanged();
    }

}
