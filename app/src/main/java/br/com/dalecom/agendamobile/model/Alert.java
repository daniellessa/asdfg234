package br.com.dalecom.agendamobile.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Calendar;
import java.util.List;

/**
 * Created by daniellessa on 04/05/16.
 */
@Table(name = "Alert")
public class Alert extends Model {

    @Column(name = "IdServer")
    protected int idServer;

    @Column(name = "Title")
    protected String title;

    @Column(name = "Message")
    protected String message;

    @Column(name = "FromUser")
    protected String from;

    @Column(name = "Date")
    protected String date;

    @Column(name = "Hour")
    protected String hour;

    @Column(name = "Type")
    protected int type;

    @Column(name = "Seen")
    protected boolean seen;

    public Alert() {}

    public Alert(int idServer, String title, String message, String from, String date, String hour, int type) {
        this.idServer = idServer;
        this.title = title;
        this.message = message;
        this.from = from;
        this.date = date;
        this.hour = hour;
        this.type = type;
        this.seen = false;
    }

    public int getIdServer() {
        return idServer;
    }

    public void setIdServer(int idServer) {
        this.idServer = idServer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public static List<Alert> getAlerts(){
        return new Select()
                .from(Alert.class)
                .orderBy("id DESC")
                .execute();
    }

    public static Alert findOne(int idServer){
        return new Select()
                .from(Alert.class)
                .where("IdServer = ?", idServer)
                .executeSingle();
    }
}
