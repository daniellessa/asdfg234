package br.com.dalecom.agendamobile.adapters;

import android.content.Context;
import android.graphics.Color;
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

import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.helpers.DateHelper;
import br.com.dalecom.agendamobile.helpers.FloatHelper;
import br.com.dalecom.agendamobile.model.Service;
import br.com.dalecom.agendamobile.model.Times;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by daniellessa on 24/03/16.
 */
public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {

    private List<Service> mList;
    private Context mContext;

    class MyViewHolder extends RecyclerView.ViewHolder {

        protected TextView title, subtitle, price;
        protected RelativeLayout background_price;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title_service);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle_service);
            price = (TextView) itemView.findViewById(R.id.price_service);
            background_price = (RelativeLayout) itemView.findViewById(R.id.layout_price);

        }
    }

    public ServiceAdapter(Context mContext, List list){
        this.mContext = mContext;
        this.mList = list;;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_services, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {

        holder.title.setText(mList.get(position).getTitle());

        if(mList.get(position).getDuration() < 5)
        holder.subtitle.setText("Duração média de: " + mList.get(position).getDuration() + " Hora(s)");
        else
            holder.subtitle.setText("Duração média de: " + mList.get(position).getDuration() + " minutos");

        holder.price.setText(FloatHelper.formatarFloat(mList.get(position).getPrice()));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }




}
