package com.tripnetra.tripnetra.buses.adaps;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tripnetra.tripnetra.R;

import java.util.ArrayList;
import java.util.List;

public class SeatRecycleAdapter extends SelectableAdapter<SeatRecycleAdapter.SeatViewHolder> {

    private List<SeatDataAdapter> listadapter;
    private Context context;
    private List<Integer> namesList;

    public SeatRecycleAdapter(Context context, List<SeatDataAdapter> items) {
        this.context = context;
        listadapter = items;
        namesList = new ArrayList<>();
    }

    class SeatViewHolder extends RecyclerView.ViewHolder {

        ImageView imgSeat;
        TextView NumTv;

        SeatViewHolder(View itemView) {
            super(itemView);
            imgSeat = itemView.findViewById(R.id.img_seat);
            NumTv = itemView.findViewById(R.id.seatnumTv);

        }
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SeatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_seat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SeatViewHolder vh, int position) {
        final SeatDataAdapter adapter = listadapter.get(position);

        if (adapter.getType() == SeatDataAdapter.TYPE_SEAT) {
            vh.imgSeat.setVisibility(View.VISIBLE);vh.NumTv.setVisibility(View.VISIBLE);

            if (adapter.getPosition().equals("vertical")) { vh.imgSeat.setRotation(270); }

            if(adapter.getSeattype().equals("sleeper")) {
                android.view.ViewGroup.LayoutParams relay = vh.imgSeat.getLayoutParams();
                relay.width = 100;relay.height = 150;
                vh.imgSeat.setLayoutParams(relay);
            }

            String ImageString = adapter.getSeattype().equals("sleeper")? "sleeper_" : "seater_";

            final String SelectSeat = ImageString+"selected_bus",AvlSeat = ImageString + "available";

            switch (adapter.getStatus()) {
                case "BK":
                    ImageString = ImageString + (adapter.getGender().equals("Female") ? "booked_female" : "blocked");
                    vh.imgSeat.setEnabled(false);
                    break;
                case "F":
                    ImageString = ImageString + "available_female";
                    break;
                default:
                    ImageString = ImageString + "available";
                    break;
            }

            vh.imgSeat.setImageDrawable(GetImage(context,ImageString));
            vh.NumTv.setText(adapter.getName());

            vh.imgSeat.setOnClickListener(v -> {

                int pos = vh.getAdapterPosition();
                toggleSelection(pos);

                if (isSelected(pos)) { if (namesList.size() < 6) {vh.imgSeat.setImageDrawable(GetImage(context, SelectSeat));}
                } else { vh.imgSeat.setImageDrawable(GetImage(context, AvlSeat)); }

                int ss = adapter.getApos();
                if (namesList.contains(ss)) { namesList.remove(ss);
                } else { if (namesList.size() < 6) {namesList.add(ss);} }

                ((OnSeatSelected) context).onSeatSelected(namesList);

            });


        }else {
            vh.imgSeat.setVisibility(View.INVISIBLE);
            vh.NumTv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() { return listadapter.size(); }

    private static Drawable GetImage(Context c, String ImageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()));
    }

}