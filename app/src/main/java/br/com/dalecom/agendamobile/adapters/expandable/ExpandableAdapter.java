package br.com.dalecom.agendamobile.adapters.expandable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

import br.com.dalecom.agendamobile.R;
import br.com.dalecom.agendamobile.model.User;

/**
 * Created by daniellessa on 05/04/16.
 */
public class ExpandableAdapter extends ExpandableRecyclerAdapter<HeaderViewHolder,ProfissionalViewHolder> {

    /**
     * Primary constructor. Sets up {@link #mParentItemList} and {@link #mItemList}.
     * <p/>
     * Changes to {@link #mParentItemList} should be made through add/remove methods in
     * {@link ExpandableRecyclerAdapter}
     *
     * @param parentItemList List of all {@link ParentListItem} objects to be
     *                       displayed in the RecyclerView that this
     *                       adapter is linked to
     */

    LayoutInflater layoutInflater;

    public ExpandableAdapter(Context context, @NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public HeaderViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View recipeView = layoutInflater.inflate(R.layout.header_professional, parentViewGroup, false);
        return new HeaderViewHolder(recipeView);
    }

    @Override
    public ProfissionalViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View view = layoutInflater.inflate(R.layout.adapter_profissionals, childViewGroup, false);
        return new ProfissionalViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(HeaderViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        Header header = (Header) parentListItem;
        parentViewHolder.setTitleView(header.getTitle());

    }

    @Override
    public void onBindChildViewHolder(ProfissionalViewHolder childViewHolder, int position, Object childListItem) {
        User user = (User) childListItem;
        childViewHolder.setNameView(user.getName());
    }
}
