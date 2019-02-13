package com.tripnetra.tripnetra.darshan;

import android.os.Bundle;

public class DDataAdapter {

    private String date,text,name,id,image,price,type,typeid,suppid,feature,descrption,policy,tax,address,
            status,company,adult,child,rid,aprice,dprice,avail,max;
    private Bundle bundle;



    public void setDate(String string) {this.date = string;}
    public String getDate() {return date;}

    public void setText(String string) {this.text = string;}
    public String getText() {return text;}

    public void setPrice(String string) {this.price = string;}
    public String getPrice() {return price;}

    public void setName(String string) {this.name = string;}
    public String getName() {return name;}

    public void setID(String string) {this.id = string;}
    public String getID() {return id;}

    public void setImage(String string) {this.image = string;}
    public String getImage() {return image;}

    public void setType(String string) {this.type = string;}
    public String getType() {return type;}

    public void setFeature(String string) {this.feature = string;}
    public String getFeature() {return feature;}

    public void setDescription(String string) {this.descrption = string;}
    public String getDescription() {return descrption;}

    public void setStatus(String string) {this.status = string;}
    public String getStatus() {return status;}

    public void setChild(String string) {this.child = string;}
    public String getChild() {return child;}

    public void setAPrice(String string) {this.aprice = string;}
    public String getAPrice() {return aprice;}


    public void setaddress(String string) { this.address = string; }
    public String getaddress() {return address;}

    public void setBundle(Bundle bundle) {this.bundle = bundle;}
    public Bundle getBundle() {return bundle;}



}