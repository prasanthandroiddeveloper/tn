package com.tripnetra.tripnetra.darshan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tripnetra.tripnetra.R;

import java.util.ArrayList;

import static com.tripnetra.tripnetra.utils.Constants.Addon_Select_Code;

public class addons_activity extends AppCompatActivity {

    LinearLayout MainLayt;
    Bundle bundle;
    ArrayList<String> id_list;
    ArrayList<String> name_list;
    ArrayList<String> price_list;
    ArrayList<String> duration_list;

  //  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addons_act);
        MainLayt=findViewById(R.id.addon);

        bundle = getIntent().getExtras();
    }

    public void submit(View view) {

        if(id_list==null){
            temples.checkedplayers = null;
        }
        startActivityForResult(new Intent(addons_activity.this,temples.class),Addon_Select_Code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Addon_Select_Code && resultCode == RESULT_OK){

            id_list = new ArrayList<>();name_list = new ArrayList<>();
            price_list = new ArrayList<>();duration_list = new ArrayList<>();

            id_list = data.getStringArrayListExtra("ids");
            name_list =  data.getStringArrayListExtra("name");
            price_list =  data.getStringArrayListExtra("price");
            duration_list =  data.getStringArrayListExtra("duration");

            MainLayt.removeAllViews();

            for(int i=0;i<id_list.size();i++){
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.selectedaddondetails, null);
                ((TextView) rowView.findViewById(R.id.templenameTV)).setText(name_list.get(i));
                ((TextView) rowView.findViewById(R.id.durationTv)).setText(duration_list.get(i));
                ((TextView) rowView.findViewById(R.id.priceTv)).setText(price_list.get(i));
                MainLayt.addView(rowView,MainLayt.getChildCount());}
            }
        }

    public void send(View view) {
    Intent intent = new Intent(addons_activity.this,passenger_details_act.class);

    bundle.putStringArrayList("name",name_list);
    bundle.putStringArrayList("price",price_list);
    bundle.putStringArrayList("duration",duration_list);
    bundle.putStringArrayList("ids",id_list);

    intent.putExtras(bundle);

    startActivity(intent);

    }
}
