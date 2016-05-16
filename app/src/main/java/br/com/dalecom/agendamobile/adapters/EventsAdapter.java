package br.com.dalecom.agendamobile.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.model.Event;
import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.model.Service;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.FileUtils;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.S;
import br.com.dalecom.agendamobile.wrappers.S3;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by daniellessa on 24/03/16.
 */
public class EventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Event> mList;
    private Context mContext;
    private ImageLoader imageLoader;
    private RecyclerView mRecyclerView;

    @Inject
    RestClient restClient;

    @Inject
    EventManager eventManager;

    @Inject
    S3 s3;

    @Inject FileUtils fileUtils;

    class VHBusy extends RecyclerView.ViewHolder {

        protected CircleImageView iconProfessional;
        protected TextView eventName, eventDate, eventProperty, eventStatus;

        public VHBusy(View itemView) {
            super(itemView);
            iconProfessional = (CircleImageView) itemView.findViewById(R.id.icon_professional);
            eventName = (TextView) itemView.findViewById(R.id.name_event);
            eventDate = (TextView) itemView.findViewById(R.id.text_day_month_year);
            eventProperty = (TextView) itemView.findViewById(R.id.property_name);
            eventStatus = (TextView) itemView.findViewById(R.id.event_status);
        }
    }

    class VHFree extends RecyclerView.ViewHolder {

        protected CircleImageView iconProfessional;
        protected TextView professionalName, eventName, eventDate, eventHour, eventDayWeek, eventProperty, eventStatus;
        protected ImageView progress;
        protected RelativeLayout statusBarLayout;


        public VHFree(View itemView) {
            super(itemView);
            professionalName = (TextView) itemView.findViewById(R.id.professional_name);
            iconProfessional = (CircleImageView) itemView.findViewById(R.id.icon_professional);
            eventName = (TextView) itemView.findViewById(R.id.name_event);
            eventDayWeek = (TextView) itemView.findViewById(R.id.text_day_week);
            eventDate = (TextView) itemView.findViewById(R.id.text_day_month_year);
            eventHour = (TextView) itemView.findViewById(R.id.text_hour);
            eventProperty = (TextView) itemView.findViewById(R.id.property_name);
            eventStatus = (TextView) itemView.findViewById(R.id.event_status);
            progress = (ImageView) itemView.findViewById(R.id.back_searching);
            statusBarLayout = (RelativeLayout) itemView.findViewById(R.id.layout_status);

        }
    }

    public EventsAdapter(Context mContext, List list, RecyclerView recyclerView){
        this.mContext = mContext;
        ((AgendaMobileApplication) mContext.getApplicationContext()).getAppComponent().inject(this);
        this.mList = list;
        this.mRecyclerView = recyclerView;
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
            User professional = mList.get(position).getUserProf();
            Service service = mList.get(position).getService();
            Property property = mList.get(position).getProperty();
            Calendar date = DateHelper.copyDate(mList.get(position).getStartAt());

            searchedAnimated(((VHFree) holder).progress);
            ((VHFree) holder).professionalName.setText(professional.getName());
            ((VHFree) holder).eventName.setText(service.getTitle());
            ((VHFree) holder).eventDayWeek.setText(DateHelper.getWeekDay(date));
            ((VHFree) holder).eventDate.setText(DateHelper.toStringFull(date));
            ((VHFree) holder).eventHour.setText(DateHelper.hourToString(date));
            ((VHFree) holder).eventProperty.setText(property.getName());
            getImageProfessionalByBucket(professional, ((VHFree) holder).iconProfessional, position);
            //((VHFree) holder).iconProfessional.setImageResource(R.drawable.scarlett);


            switch (mList.get(position).getStatus()){
                case "pending":
                    ((VHFree) holder).eventStatus.setText("Agendado");
                    break;
                case "serving":
                    ((VHFree) holder).eventStatus.setText("Atendendo");
                    ((VHFree) holder).statusBarLayout.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
                    break;
                case "finished":
                    ((VHFree) holder).eventStatus.setText("Atendido");
                    ((VHFree) holder).statusBarLayout.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
                    break;
                case "canceled":
                    ((VHFree) holder).eventStatus.setText("Cancelado");
                    ((VHFree) holder).statusBarLayout.setBackgroundColor(mContext.getResources().getColor(R.color.orange));
                    break;
                case "missed":
                    ((VHFree) holder).eventStatus.setText("Não compareceu");
                    ((VHFree) holder).statusBarLayout.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                    break;
            }

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

    private void getImageProfessionalByBucket(final User professional, final CircleImageView imagePerfil,final int position){

        if(professional.getBucketPath() != null && professional.getPhotoPath() != null){

            if(professional.getLocalImageLocation() == null) {

                String namePath = fileUtils.getUniqueName();
                final File pictueFile = fileUtils.getOutputMediaFile(FileUtils.MEDIA_TYPE_IMAGE, namePath);
                s3.downloadProfileFile(pictueFile, professional.getBucketPath() + professional.getPhotoPath()).setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        Log.d(LogUtils.TAG, "State ProfessionalAdapter: " + state);
                        if (state == TransferState.COMPLETED) {
                            professional.setLocalImageLocationAndDeletePreviousIfExist(Uri.fromFile(pictueFile).toString());
                            imagePerfil.setImageURI(Uri.parse(professional.getLocalImageLocation()));
                            notifyItemChanged(position);
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Log.d(LogUtils.TAG, "Erro ProfessionalAdapter: " + ex);
                    }
                });
            }else {
                imagePerfil.setImageURI(Uri.parse(professional.getLocalImageLocation()));
            }
        }

        if(professional.getLocalImageLocation() != null ){
            imagePerfil.setImageURI(Uri.parse(professional.getLocalImageLocation()));
        }
    }

    public void searchedAnimated(final ImageView view) {

        Animation rotation = AnimationUtils.loadAnimation(mContext.getApplicationContext(), R.anim.rotation);
        rotation.setRepeatCount(Animation.INFINITE);
        view.startAnimation(rotation);
    }





}
