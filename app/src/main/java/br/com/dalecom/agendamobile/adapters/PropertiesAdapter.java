package br.com.dalecom.agendamobile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.model.Property;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by daniellessa on 29/03/16.
 */
public class PropertiesAdapter extends RecyclerView.Adapter<PropertiesAdapter.VHItem> {


    private List<Property> mList;
    private Context mContext;

    public class VHItem extends RecyclerView.ViewHolder {

        protected CircleImageView imageView;
        protected TextView textViewName, textViewInfo;

        public VHItem (View view){
            super(view);

            imageView = (CircleImageView) view.findViewById(R.id.icon_property);
            textViewName = (TextView) view.findViewById(R.id.name_professional);
            textViewInfo  = (TextView) view.findViewById(R.id.professional_type);

        }
    }

    public PropertiesAdapter (Context context, List<Property> list){
        mList = list;
        mContext = context;
    }


    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_properties, parent, false);
        return new VHItem(view);
    }

    @Override
    public void onBindViewHolder(VHItem holder, int position) {

        if(holder instanceof VHItem){
            //((VHItem) holder).imageView.setImageResource();
            holder.textViewName.setText(mList.get(position).getName());
            holder.textViewInfo.setText(mList.get(position).getInfo());
        }


    }


    @Override
    public int getItemCount() {
        return mList.size();
    }
}
