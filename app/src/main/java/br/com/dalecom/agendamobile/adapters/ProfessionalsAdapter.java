package br.com.dalecom.agendamobile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.Collection;
import java.util.List;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.model.User;
import br.com.dalecom.agendamobile.utils.S;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by daniellessa on 24/03/16.
 */
public class ProfessionalsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<User> mList;
    private Context mContext;
    private ImageLoader imageLoader;

    class VHItem extends RecyclerView.ViewHolder {

        protected TextView professionalName;
        protected TextView professionalTime;
        protected CircleImageView imagePerfil;


        public VHItem(View itemView) {
            super(itemView);

            professionalName = (TextView) itemView.findViewById(R.id.name_professional);
            professionalTime = (TextView) itemView.findViewById(R.id.professional_type);
            imagePerfil = (CircleImageView) itemView.findViewById(R.id.icon_perfil);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof VHItem)
        {
            ((VHItem) holder).imagePerfil.setImageResource(R.drawable.scarlett);
            ((VHItem) holder).professionalName.setText(mList.get(position).getName());
            ((VHItem) holder).professionalTime.setText(mList.get(position).getProfessional().getProfessionName());
        }
        else if(holder instanceof VHHeader)
        {
            ((VHHeader) holder).headerText.setText(mList.get(position).getProfessional().getProfessionName());
        }
    }

    @Override
    public int getItemViewType(int position) {

        if(mList.get(position).getProfessional().getViewType() == 0)
            return S.TYPE_HEADER;
        return S.TYPE_ITEM;
    }

    private boolean isPositionHeader(int position)
    {
        if(position == 0)
            return true;

        if(mList.get(position).getProfessional().getCategory() % 2 != 0)
        return true;

        return false;
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

    public void addAll(Collection<User> collection){
        if(collection != null){
            mList.addAll(collection);
            notifyDataSetChanged();
        }
    }


}
