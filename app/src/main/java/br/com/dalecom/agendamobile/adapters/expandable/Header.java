package br.com.dalecom.agendamobile.adapters.expandable;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

/**
 * Created by daniellessa on 05/04/16.
 */
public class Header implements ParentListItem {

    private int id;
    private String title;
    private List itens;

    public Header(String title, List list){
        itens = list;
        this.title = title;
    }

    public Header(){

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List getItens() {
        return itens;
    }

    public void setItens(List itens) {
        this.itens = itens;
    }

    @Override
    public List<?> getChildItemList() {
        return null;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
