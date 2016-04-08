package br.com.dalecom.agendamobile.adapters.expandable;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

import br.com.dalecom.agendamobile.R;

/**
 * Created by daniellessa on 05/04/16.
 */
public class ProfissionalViewHolder extends ChildViewHolder {
    /**
     * Default constructor.
     *
     * @param itemView The {@link View} being hosted in this ViewHolder
     */

    private TextView nameView;

    public ProfissionalViewHolder(View itemView) {
        super(itemView);

        nameView = (TextView) itemView.findViewById(R.id.name_professional);
    }

    public void setNameView(String text) {
        this.nameView.setText(text);
    }
}
