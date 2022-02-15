package cz.cvut.kbss.ear.project.kosapi.entities;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import cz.cvut.kbss.ear.project.kosapi.links.RoomLink;

import java.io.Serializable;

public class KosTimetableSlot implements Serializable {
    @JacksonXmlProperty(localName = "day")
    private String day;

    @JacksonXmlProperty(localName = "duration")
    private String duration;

    @JacksonXmlProperty(localName = "endTime")
    private String endTime;

    @JacksonXmlProperty(localName = "parity")
    private String parity;

    @JacksonXmlProperty(localName = "firstHour")
    private String firstHour;

    @JacksonXmlProperty(localName = "startTime")
    private String startTime;

    @JacksonXmlProperty(localName = "room")
    private RoomLink room;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getParity() {
        return parity;
    }

    public void setParity(String parity) {
        this.parity = parity;
    }

    public String getFirstHour() {
        return firstHour;
    }

    public void setFirstHour(String firstHour) {
        this.firstHour = firstHour;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public RoomLink getRoom() {
        return room;
    }

    public void setRoom(RoomLink room) {
        this.room = room;
    }
}
