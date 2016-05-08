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
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.utils.FileUtils;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.utils.S;
import br.com.dalecom.agendamobile.wrappers.S3;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by daniellessa on 24/03/16.
 */
public class ProfessionalsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<User> mList;
    private Context mContext;
    private ImageLoader imageLoader;

    @Inject public S3 s3;

    @Inject public FileUtils fileUtils;

    class VHItem extends RecyclerView.ViewHolder {

        protected TextView professionalName;
        protected TextView professionalTime;
        protected CircleImageView imagePerfil;
        protected ImageView progress;


        public VHItem(View itemView) {
            super(itemView);

            professionalName = (TextView) itemView.findViewById(R.id.name_professional);
            professionalTime = (TextView) itemView.findViewById(R.id.professional_type);
            imagePerfil = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
            progress = (ImageView) itemView.findViewById(R.id.back_searching);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {

        protected TextView headerText;

        public VHHeader(View itemView) {
            super(itemView);
            headerText = (TextView) itemView.findViewById(R.id.text_header);
        }
    }

    public ProfessionalsAdapter(Context mContext, List list ){
        this.mContext = mContext;
        ((AgendaMobileApplication) mContext.getApplicationContext()).getAppComponent().inject(this);
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == S.TYPE_HEADER)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_professional, parent, false);
            return new VHHeader(view);
        }
        else if(viewType == S.TYPE_ITEM)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_profissionals, parent, false);
            return new VHItem(view);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof VHItem)
        {
            ((VHItem) holder).professionalName.setText(mList.get(position).getName());
            ((VHItem) holder).professionalTime.setText(mList.get(position).getProfessional().getProfessionName());
            searchedAnimated(((VHItem) holder).progress);

            if(mList.get(position).getBucketPath() != null && mList.get(position).getPhotoPath() != null){

                if(mList.get(position).getLocalImageLocation() == null) {

                    String namePath = fileUtils.getUniqueName();
                    final File pictueFile = fileUtils.getOutputMediaFile(FileUtils.MEDIA_TYPE_IMAGE, namePath);
                    s3.downloadProfileFile(pictueFile, mList.get(position).getBucketPath() + mList.get(position).getPhotoPath()).setTransferListener(new TransferListener() {
                        @Override
                        public void onStateChanged(int id, TransferState state) {
                            Log.d(LogUtils.TAG, "State ProfessionalAdapter: " + state);
                            if (state == TransferState.COMPLETED) {
                                mList.get(position).setLocalImageLocationAndDeletePreviousIfExist(Uri.fromFile(pictueFile).toString());
                                ((VHItem) holder).imagePerfil.setImageURI(Uri.parse(mList.get(position).getLocalImageLocation()));
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
                    ((VHItem) holder).imagePerfil.setImageURI(Uri.parse(mList.get(position).getLocalImageLocation()));
                }
            }

            if(mList.get(position).getLocalImageLocation() != null ){
                ((VHItem) holder).imagePerfil.setImageURI(Uri.parse(mList.get(position).getLocalImageLocation()));
            }


        }
        else if(holder instanceof VHHeader)
        {
            ((VHHeader) holder).headerText.setText(mList.get(position).getProfessional().getProfessionName());
        }
    }

    public void searchedAnimated(final ImageView view) {

        Animation rotation = AnimationUtils.loadAnimation(mContext.getApplicationContext(), R.anim.rotation);
        rotation.setRepeatCount(Animation.INFINITE);
        view.startAnimation(rotation);
    }

    @Override
    public int getItemViewType(int position) {

        if(mList.get(position).getProfessional().getViewType() == 0)
            return S.TYPE_HEADER;
        return S.TYPE_ITEM;
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

    public void add(User object){
        mList.add(object);
        notifyDataSetChanged();
    }

    public void remove(User object){
        mList.remove(object);
        notifyDataSetChanged();
    }

    public void addAll(List<User> collection){
        if(collection != null){
            mList.addAll(collection);
            notifyDataSetChanged();
        }
    }


}
