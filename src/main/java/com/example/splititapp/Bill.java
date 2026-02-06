package com.example.splititapp;

import java.util.ArrayList;

public class Bill {
    private String id, title, total, payer, date;
    private int totalMembers, paidMembers;

    public Bill(String id, String title, String total, String payer, String date, int totalMembers, int paidMembers ){
        this.id = id;
        this.title = title;
        this.total = total;
        this.payer = payer;
        this.date = date;
        this.totalMembers = totalMembers;
        this.paidMembers = paidMembers;
    }
    public String getTitle(){return title;}
    public String getTotal(){return total;}
    public String getPayer(){return payer;}
    public String getDate(){return date;}

    public int getTotalMembers(){return totalMembers;}
    public int getPaidMembers(){return paidMembers;}
    public String getId(){return id;}

}
