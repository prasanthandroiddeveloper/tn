package com.tripnetra.tripnetra;

import android.app.Application;
import android.os.Bundle;

public class G_Class extends Application {

    private String HotelId,Hotelname,Cin,Cout,RCount,ACount,CCount,Nights,Coupcode,payType,baseurl,imageurl,paytmMid,payuMid;
    private Bundle bundle;

    public void setRoomDetails(String from,String to,String rooms,String adult, String child,String nights){
        this.Cin = from;this.Cout = to;
        this.RCount = rooms;this.ACount = adult;
        this.CCount = child;this.Nights = nights;
    }

    public String getHotelId(){return HotelId;}
    public void setHotelId(String string){this.HotelId = string;}

    public String getHotelName(){return Hotelname;}
    public void setHotelname(String string){this.Hotelname = string;}

    public String getCheckin(){return Cin;}
    public String getCheckout(){return Cout;}
    public String getRoomCount(){return RCount;}
    public String getAdultCount(){return ACount;}
    public String getChildCount(){return CCount;}

    public String getNights(){return Nights; }
    public void setNights(String string){this.Nights = string;}

    public String getCoupcode(){return Coupcode; }
    public void setCoupcode(String string){this.Coupcode = string;}

    public String getPayType(){return payType; }
    public void setPayType(String string){this.payType = string;} // hotel car tour

    public Bundle getBundle(){return bundle; }
    public void setBundle(Bundle bundl){this.bundle = bundl;}

    public String getBaseurl() { return baseurl; }
    public void setBaseurl(String baseurl) { this.baseurl = baseurl; }

    public String getPayuMid() { return payuMid; }
    public void setPayuMid(String payuMid) { this.payuMid = payuMid; }

    public String getPaytmMid() { return paytmMid; }
    public void setPaytmMid(String paytmMid) { this.paytmMid = paytmMid; }

    public String getImageurl() { return imageurl; }
    public void setImageurl(String imageurl) { this.imageurl = imageurl; }

}