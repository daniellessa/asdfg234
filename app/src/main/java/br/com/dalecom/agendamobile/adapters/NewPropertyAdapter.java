package br.com.dalecom.agendamobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.ui.PropertyActivity;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.FileUtils;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.S;
import br.com.dalecom.agendamobile.wrappers.S3;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by daniellessa on 29/03/16.
 */
public class NewPropertyAdapter extends RecyclerView.Adapter<NewPropertyAdapter.VHItem> {


    private List<Property> mList;
    private Context mContext;

    @Inject
    public S3 s3;

    @Inject public FileUtils fileUtils;

    @Inject public RestClient restClient;

    @Inject public SharedPreference sharedPreference;

    @Inject public EventManager eventManager;

    public class VHItem extends RecyclerView.ViewHolder {

        protected ImageView imageView;
        protected TextView textViewName, textViewAddress;
        protected ImageView progress, iconFavorite;

        public VHItem (View view){
            super(view);

            imageView = (ImageView) view.findViewById(R.id.icon_property);
            textViewName = (TextView) view.findViewById(R.id.property_name);
            textViewAddress = (TextView) view.findViewById(R.id.property_address);
            progress = (ImageView) view.findViewById(R.id.back_searching);
            iconFavorite = (ImageView) view.findViewById(R.id.icon_favorite);

        }
    }

    public NewPropertyAdapter(Context context, List<Property> list){
        mList = list;
        mContext = context;
        ((AgendaMobileApplication) mContext.getApplicationContext()).getAppComponent().inject(this);
    }


    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_new_property, parent, false);
        return new VHItem(view);
    }

    @Override
    public void onBindViewHolder(final VHItem holder, final int position) {

        String address = mList.get(position).getStreet();
        address += ", "+ mList.get(position).getNumber();
        address += " - "+ mList.get(position).getCity();
        holder.textViewAddress.setText(address);

        holder.textViewName.setText(mList.get(position).getName());
        changeImageS3(position, holder.imageView);
        searchedAnimated(holder.progress);

        if(isFavorite(mList.get(position)))
            holder.iconFavorite.setImageResource(R.drawable.ic_favorite_red);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProperty(mList.get(position));
            }
        });


        holder.iconFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Property property = mList.get(position);

                if(!isFavorite(property)) {
                    saveProperty(property);
                    holder.iconFavorite.setImageResource(R.drawable.ic_favorite_red);
                    Toast.makeText(mContext, "Inserido em favoritos", Toast.LENGTH_SHORT).show();
                }else {
                    Property.findOne(property.getIdServer()).delete();
                    holder.iconFavorite.setImageResource(R.drawable.ic_favorite_white);
                    Toast.makeText(mContext, "Removido de favoritos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void searchedAnimated(final ImageView view) {

        Animation rotation = AnimationUtils.loadAnimation(mContext.getApplicationContext(), R.anim.rotation);
        rotation.setRepeatCount(Animation.INFINITE);
        view.startAnimation(rotation);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void swap(List<Property> properties){
        mList.clear();
        mList.addAll(properties);
        notifyDataSetChanged();
    }

    private void changeImageS3(final int position, final ImageView view){

        if(mList.get(position).getBucketPath() != null && mList.get(position).getPhoto_path() != null){

            if(mList.get(position).getLocalImageLocation() == null) {

                String namePath = fileUtils.getUniqueName();
                final File pictureFile = fileUtils.getOutputMediaFile(FileUtils.MEDIA_TYPE_IMAGE, namePath);
                s3.downloadProfileFile(pictureFile,mList.get(position).getBucketPath()+"/"+mList.get(position).getPhoto_path()).setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        Log.d(LogUtils.TAG, "State PropertyAdapter: " + state);
                        if (state == TransferState.COMPLETED) {
                            mList.get(position).setLocalImageLocationAndDeletePreviousIfExist(Uri.fromFile(pictureFile).toString());
                            view.setImageURI(Uri.parse(mList.get(position).getLocalImageLocation()));
                            notifyItemChanged(position);
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        Log.d(LogUtils.TAG, "Change PropertyAdapter: " + bytesCurrent*100/bytesTotal);
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Log.e(LogUtils.TAG, "Erro PropertyAdapter: " + ex);
                        view.setImageResource(R.drawable.property_default);
                    }
                });
            }else {
                view.setImageURI(Uri.parse(mList.get(position).getLocalImageLocation()));
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        return S.TYPE_ITEM;
    }

    private void goToProperty(Property property){
        sharedPreference.setCurrentProperty(property);
        Calendar currentDate = Calendar.getInstance();
        eventManager.startNewEvent(currentDate);
        eventManager.setCurrentProperty(property);

        Intent it = new Intent(mContext, PropertyActivity.class);
        mContext.startActivity(it);
    }

    public void saveProperty(Property property){
        if(property != null){

            Property oldProperty = Property.findOne(property.getIdServer());
            property.setNotification(true);

            if(oldProperty == null){
                property.save();
                notificationAdminProperty(property);
            }else{
                oldProperty.delete();
                property.save();
                notificationAdminProperty(property);
            }

        }

    }

    private void notificationAdminProperty(Property property){

        restClient.notifyNewAssociation(property.getIdServer(), new Callback<JsonObject>() {

            @Override
            public void success(JsonObject jsonObject, Response response) {
                Log.d(LogUtils.TAG, "notificationAdminProperty: SUCESS");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(LogUtils.TAG, "notificationAdminProperty: FAIL");
            }
        });
    }

    private boolean isFavorite(Property property){

        if(Property.findOne(property.getIdServer()) != null)
            return true;
        else
            return false;

    }
}
