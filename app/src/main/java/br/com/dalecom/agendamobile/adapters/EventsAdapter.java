package br.com.dalecom.agendamobile.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.Professional;
import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.model.Service;
import br.com.dalecom.agendamobile.model.Times;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.S;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by daniellessa on 24/03/16.
 */
public class EventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Event> mList;
    private Context mContext;
    private ImageLoader imageLoader;

    @Inject
    RestClient restClient;

    @Inject
    EventManager eventManager;

    class VHBusy extends RecyclerView.ViewHolder {

        protected ImageView imageProperty, imageStatus;
        protected TextView eventName, eventDate, eventProperty;

        public VHBusy(View itemView) {
            super(itemView);
            imageProperty = (ImageView) itemView.findViewById(R.id.logo_property);
            imageStatus = (ImageView) itemView.findViewById(R.id.image_status);
            eventName = (TextView) itemView.findViewById(R.id.name_event);
            eventDate = (TextView) itemView.findViewById(R.id.event_date);
            eventProperty = (TextView) itemView.findViewById(R.id.event_property);
        }
    }

    class VHFree extends RecyclerView.ViewHolder {

        protected ImageView imageProperty, imageStatus;
        protected TextView eventName, eventDate, eventProperty;


        public VHFree(View itemView) {
            super(itemView);
            imageProperty = (ImageView) itemView.findViewById(R.id.logo_property);
            imageStatus = (ImageView) itemView.findViewById(R.id.image_status);
            eventName = (TextView) itemView.findViewById(R.id.name_event);
            eventDate = (TextView) itemView.findViewById(R.id.event_date);
            eventProperty = (TextView) itemView.findViewById(R.id.event_property);
        }
    }

    public EventsAdapter(Context mContext, List list){
        this.mContext = mContext;
        ((AgendaMobileApplication) mContext.getApplicationContext()).getAppComponent().inject(this);
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == S.TYPE_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_events, parent, false);
            return new VHBusy(view);
        }
        else if(viewType == S.TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_events, parent, false);
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
            Professional professional = mList.get(position).getProfessinal();
            Property property = mList.get(position).getProperty();
            Service service = mList.get(position).getService();


            //((VHFree) holder).eventName.setText(service.getTitle());
            ((VHFree) holder).eventDate.setText(mList.get(position).getStartAt());
            ((VHFree) holder).imageProperty.setImageURI(Uri.parse(property.getLocalImageLocation()));
            ((VHFree) holder).eventProperty.setText(property.getName());




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

    public void swap(List<Event> events){
        mList.clear();
        mList.addAll(events);
        notifyDataSetChanged();
    }





}
