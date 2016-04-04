package br.com.dalecom.agendamobile.model;

import android.net.Uri;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;

import br.com.dalecom.agendamobile.utils.LogUtils;

/**
 * Created by daniellessa on 24/03/16.
 */
@Table(name="Professional")
public class Professional extends Model{

    @Column(name = "Property")
    private int properties;

    @Column(name = "Category")
    private int category;

    @Column(name = "startAt")
    private Calendar startAt;

    @Column(name = "endsAt")
    private Calendar endsAt;

    @Column(name = "startLaunchAt")
    private Calendar startLunchAt;

    @Column(name = "endsLaunchAt")
    private Calendar endsLunchAt;

    @Column(name = "split")
    private Calendar split;

    @Column(name = "interval")
    private Calendar interval;

    @Column(name = "workSunday")
    private boolean workSunday;

    @Column(name = "workMonday")
    private boolean workMonday;

    @Column(name = "workTuesday")
    private boolean workTuesday;

    @Column(name = "workWednesday")
    private boolean workWednesday;

    @Column(name = "workThursday")
    private boolean workThursday;

    @Column(name = "workFriday")
    private boolean workFriday;

    @Column(name = "workSaturday")
    private boolean workSaturday;

    protected int viewType;

    protected String professionName;




    public int getProperties() {
        return properties;
    }

    public void setProperties(int properties) {
        this.properties = properties;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public Calendar getStartAt() {
        return startAt;
    }

    public void setStartAt(Calendar startAt) {
        this.startAt = startAt;
    }

    public Calendar getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Calendar endsAt) {
        this.endsAt = endsAt;
    }

    public Calendar getStartLaunchAt() {
        return startLunchAt;
    }

    public void setStartLaunchAt(Calendar startLaunchAt) {
        this.startLunchAt = startLaunchAt;
    }

    public Calendar getEndsLaunchAt() {
        return endsLunchAt;
    }

    public void setEndsLaunchAt(Calendar endsLaunchAt) {
        this.endsLunchAt = endsLaunchAt;
    }

    public Calendar getSplit() {
        return split;
    }

    public void setSplit(Calendar split) {
        this.split = split;
    }

    public Calendar getInterval() {
        return interval;
    }

    public void setInterval(Calendar interval) {
        this.interval = interval;
    }

    public boolean isWorkSunday() {
        return workSunday;
    }

    public void setWorkSunday(boolean workSunday) {
        this.workSunday = workSunday;
    }

    public boolean isWorkMonday() {
        return workMonday;
    }

    public void setWorkMonday(boolean workMonday) {
        this.workMonday = workMonday;
    }

    public boolean isWorkTuesday() {
        return workTuesday;
    }

    public void setWorkTuesday(boolean workTuesday) {
        this.workTuesday = workTuesday;
    }

    public boolean isWorkWednesday() {
        return workWednesday;
    }

    public void setWorkWednesday(boolean workWednesday) {
        this.workWednesday = workWednesday;
    }

    public boolean isWorkThursday() {
        return workThursday;
    }

    public void setWorkThursday(boolean workThursday) {
        this.workThursday = workThursday;
    }

    public boolean isWorkFriday() {
        return workFriday;
    }

    public void setWorkFriday(boolean workFriday) {
        this.workFriday = workFriday;
    }

    public boolean isWorkSaturday() {
        return workSaturday;
    }

    public void setWorkSaturday(boolean workSaturday) {
        this.workSaturday = workSaturday;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getProfessionName() {
        return professionName;
    }

    public void setProfessionName(String professionName) {
        this.professionName = professionName;
    }
}
