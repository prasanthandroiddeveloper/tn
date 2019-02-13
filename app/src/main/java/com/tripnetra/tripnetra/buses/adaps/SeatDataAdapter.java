package com.tripnetra.tripnetra.buses.adaps;

public class SeatDataAdapter {

    public static final int TYPE_SEAT = 1;
    public static final int TYPE_EMPTY = 2;

    private String name,position,status,seattype,gender;
    private int type,apos;

    public void setName(String label) { this.name = label; }

    public String getName() { return name; }

    public void setPosition(String label) { this.position = label; }

    public String getPosition() { return position; }

    public void setStatus(String label) { this.status = label; }

    public String getStatus() { return status; }

    public void setSeattype(String label) { this.seattype = label; }

    public String getSeattype() { return seattype; }

    public void setGender(String label) { this.gender = label; }

    public String getGender() { return gender; }

    public void setType(int label) { this.type = label; }

    public int getType() { return type; }

    public void setApos(int label) { this.apos = label; }

    public int getApos() { return apos; }
}