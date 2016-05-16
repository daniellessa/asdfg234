package br.com.dalecom.agendamobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import br.com.dalecom.agendamobile.AgendaMobileApplication;
import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.model.Property;
import br.com.dalecom.agendamobile.model.Times;
import br.com.dalecom.agendamobile.service.rest.RestClient;
import br.com.dalecom.agendamobile.ui.PropertyActivity;
import br.com.dalecom.agendamobile.utils.EventManager;
import br.com.dalecom.agendamobile.utils.LogUtils;
import br.com.dalecom.agendamobile.wrappers.SharedPreference;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by daniellessa on 29/03/16.
 */
public class PropertiesAdapter extends RecyclerView.Adapter<PropertiesAdapter.VHItem> {


    private List<Property> mList;
    private Context mContext;
    private RecyclerView mRecyclerView;

    @Inject
    public RestClient restClient;
    @Inject public SharedPreference sharedPreference;
    @Inject public EventManager eventManager;

    public class VHItem extends RecyclerView.ViewHolder {

        protected ImageView imageView, unfavoriteView;
        protected TextView textViewName;

        public VHItem (View view){
            super(view);

            imageView = (ImageView) view.findViewById(R.id.icon_property);
            textViewName = (TextView) view.findViewById(R.id.name_professional);
            unfavoriteView = (ImageView) view.findViewById(R.id.icon_unfavorite);

        }
    }

    public PropertiesAdapter (Context context, List<Property> list, RecyclerView recyclerView){
        mContext = context;
        ((AgendaMobileApplication) mContext.getApplicationContext()).getAppComponent().inject(this);
        mList = list;
        mRecyclerView = recyclerView;
    }


    @Override
    public VHItem onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_properties, parent, false);
        return new VHItem(view);
    }

    @Override
    public void onBindViewHolder(VHItem holder, final int position) {

        if(holder instanceof VHItem){
            //holder.textViewName.setText(mList.get(position).getName());

            if(mList.get(position).getLocalImageLocation() != null){
                holder.imageView.setImageURI(Uri.parse(mList.get(position).getLocalImageLocation()));
            }

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToProperty(mList.get(position));
                }
            });

            holder.unfavoriteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Property property = Property.findOne(mList.get(position).getIdServer());
                    if(isFavorite(mList.get(position))){
                        mList.remove(position);
                        mRecyclerView.removeViewAt(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mList.size());
                        property.delete();
                        Toast.makeText(mContext, "Removido de favoritos", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }


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

    private boolean isFavorite(Property property){

        if(Property.findOne(property.getIdServer()) != null)
            return true;
        else
            return false;

    }

    private void goToProperty(Property property){
        sharedPreference.setCurrentProperty(property);
        Calendar currentDate = Calendar.getInstance();
        eventManager.startNewEvent(currentDate);
        eventManager.setCurrentProperty(property);

        Intent it = new Intent(mContext, PropertyActivity.class);
        mContext.startActivity(it);
    }
}
