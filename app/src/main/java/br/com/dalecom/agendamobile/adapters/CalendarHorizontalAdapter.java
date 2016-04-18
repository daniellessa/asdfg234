package br.com.dalecom.agendamobile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.helpers.FloatHelper;
import br.com.dalecom.agendamobile.model.Day;
import br.com.dalecom.agendamobile.model.Service;
import br.com.dalecom.agendamobile.utils.S;

/**
 * Created by daniellessa on 24/03/16.
 */
public class CalendarHorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Day> mList;
    private Context mContext;
    private Calendar currentDate = Calendar.getInstance();

    class VHHeader extends RecyclerView.ViewHolder {

        protected TextView weekDay, numberDay;
        protected ImageView background;

        public VHHeader(View itemView) {
            super(itemView);

            weekDay = (TextView) itemView.findViewById(R.id.week_day);
            numberDay = (TextView) itemView.findViewById(R.id.number_day);
            background = (ImageView) itemView.findViewById(R.id.background_day);

        }
    }

    class VHItem extends RecyclerView.ViewHolder {

        protected TextView weekDay, numberDay;
        protected ImageView background;

        public VHItem(View itemView) {
            super(itemView);

            weekDay = (TextView) itemView.findViewById(R.id.week_day);
            numberDay = (TextView) itemView.findViewById(R.id.number_day);
            background = (ImageView) itemView.findViewById(R.id.background_day);

        }
    }

    public CalendarHorizontalAdapter(Context mContext, List<Day> list ){
        this.mContext = mContext;
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == S.TYPE_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_calendar_horizontal, parent, false);
            return new VHHeader(view);
        }
        else if(viewType == S.TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_calendar_horizontal, parent, false);
            return new VHItem(view);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {

        if(holder instanceof VHHeader){
            ((VHHeader) holder).weekDay.setText(mList.get(position).getWeekDay());
            ((VHHeader) holder).numberDay.setText(mList.get(position).getDay());

            ((VHHeader) holder).weekDay.setTextColor(mContext.getResources().getColor(R.color.red_time));
            ((VHHeader) holder).numberDay.setTextColor(mContext.getResources().getColor(R.color.red_time));
        }
        else if(holder instanceof VHItem){
            ((VHItem) holder).weekDay.setText(mList.get(position).getWeekDay());
            ((VHItem) holder).numberDay.setText(mList.get(position).getDay());
        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void swap(List<Day> days){
        mList.clear();
        mList.addAll(days);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        if(mList.get(position).getViewType() == S.TYPE_HEADER)
            return S.TYPE_HEADER;
        return S.TYPE_ITEM;
    }





}
