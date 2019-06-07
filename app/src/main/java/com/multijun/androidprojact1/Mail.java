package com.multijun.androidprojact1;
import java.util.*;
public class Mail {
    Calendar calendar;
    private String sender;
    private  String recipient;
    private String title;
    private String sendTime;
    private String content;
    private int open;
    private int id;

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCk() {
        return ck;
    }

    public void setCk(int ck) {
        this.ck = ck;
    }

    private int ck;

    {
        calendar = Calendar.getInstance();
    }


    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(){
        sendTime = calendar.get(Calendar.YEAR) + "년 " + (calendar.get(Calendar.MONTH)+1) + "월 " + calendar.get(Calendar.DATE) + "일 " + calendar.get(Calendar.HOUR) + "시 " + calendar.get(Calendar.MINUTE) + "분 ";
    }


}
