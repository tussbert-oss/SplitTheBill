package com.example.splititapp;

public class Member {
    private String id, name;
    private double totalAmount, paidAmount;

    public Member(String id, String name, double totalAmount, double paidAmount) {
        this.id = id;
        this.name = name;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getTotalAmount() { return totalAmount; }
    public double getPaidAmount() { return paidAmount; }
}