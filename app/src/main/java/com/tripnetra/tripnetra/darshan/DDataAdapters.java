package com.tripnetra.tripnetra.darshan;

import android.os.Bundle;

public class DDataAdapters {

    /*public static HashMap checkedplayers;*/
    private String date,text,name,id,image,type,descrption,aprice;
    private Bundle bundle;

    public void setDate(String string) {this.date = string;}
    public String getDate() {return date;}

    public void setText(String string) {this.text = string;}
    public String getText() {return text;}

    public void setName(String string) {this.name = string;}
    public String getName() {return name;}

    public void setID(String string) {this.id = string;}
    public String getID() {return id;}

    public void setImage(String string) {this.image = string;}
    public String getImage() {return image;}

    public void setType(String string) {this.type = string;}
    public String getType() {return type;}

    public void setDescription(String string) {this.descrption = string;}
    public String getDescription() {return descrption;}

    public void setAPrice(String string) {this.aprice = string;}
    public String getAPrice() {return aprice;}


    public void setBundle(Bundle bundle) {this.bundle = bundle;}
    public Bundle getBundle() {return bundle;}



}