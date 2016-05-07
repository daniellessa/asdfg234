package br.com.dalecom.agendamobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
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

        protected TextView title, subtitle, price, oldPrice, cifrao, valueDiscount;
        protected RelativeLayout background_price, layoutOldPrice, layoutDiscount;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title_services);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle_service);
            price = (TextView) itemView.findViewById(R.id.price_service);
            oldPrice = (TextView) itemView.findViewById(R.id.old_price_service);
            cifrao = (TextView) itemView.findViewById(R.id.cifrao_old);
            valueDiscount = (TextView) itemView.findViewById(R.id.value_discount);
            background_price = (RelativeLayout) itemView.findViewById(R.id.layout_price);
            layoutOldPrice = (RelativeLayout) itemView.findViewById(R.id.layout_old_price);
            layoutDiscount = (RelativeLayout) itemView.findViewById(R.id.layout_discount);

        }
    }

    public ServiceAdapter(Context mContext, List list){
        this.mContext = mContext;
        this.mList = list;
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

        if(mList.get(position).getOldPrice() > 0){
            holder.oldPrice.setText(String.valueOf(FloatHelper.formatarFloat(mList.get(position).getOldPrice())));
            holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.cifrao.setPaintFlags(holder.cifrao.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.layoutOldPrice.setVisibility(View.VISIBLE);

            int value = Math.round(((mList.get(position).getOldPrice() - mList.get(position).getPrice()) * 100)/mList.get(position).getOldPrice());
            holder.valueDiscount.setText(String.valueOf(value));
            holder.layoutDiscount.setVisibility(View.VISIBLE);
        }

        if(mList.get(position).getHours() > 0)
            holder.subtitle.setText("Duração média de: " + mList.get(position).getHours() + " Hora(s) e " + mList.get(position).getMinutes() +" minuto(s)");
        else
            holder.subtitle.setText("Duração média de: " + mList.get(position).getMinutes() +" minuto(s)");

        holder.price.setText(FloatHelper.formatarFloat(mList.get(position).getPrice()));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


}
