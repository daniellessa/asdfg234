package br.com.dalecom.agendamobile.adapters.expandable;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import br.com.dalecom.agendamobile.R;

/**
 * Created by daniellessa on 05/04/16.
 */
public class HeaderViewHolder extends ParentViewHolder {
    /**
     * Default constructor.
     *
     * @param itemView The {@link View} being hosted in this ViewHolder
     */

    private TextView titleView;

    public HeaderViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.text_header);
    }


    public void setTitleView(String text) {
        this.titleView.setText(text);
    }
}
