package com.tripnetra.tripnetra.hotels.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.tripnetra.tripnetra.R;
import com.tripnetra.tripnetra.hotels.Hotel_Search_Act;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Hotel_Filter_Frag extends DialogFragment {

    RangeBar rangeBar;
    String HotelType[]={"Budget Hotel","Luxury Hotel","Refreshment Hotel","Trust Accommodation"},
            cinhours[]={"Standard Check-in","24 Hrs"},
            amenities[]={"Food","Transfer","Telephone","Service","Air Condition","Laundry","Gym	","Conference Room","Wi-Fi","Car Parking","Multi Cuisine Restaurant",
                    "CCTV Camera","Gift Shop","Swimming Pool","24 Hr's RoomService","Beverages","FitnessCentre","In Room Coffee Maker","Packaged DrinkingWater",
                    "Personal safe","Currency Exchange","Television","Games","Hair Dryer","Spa Services","Roll Away Bed","Mini Bar","Doctor on Call"},
            starcountlist[]={"1 Star   ★","2 Star   ★ ★","3 Star   ★ ★ ★","4 Star   ★ ★ ★ ★","5 Star   ★ ★ ★ ★ ★"};


    public Hotel_Filter_Frag() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_hotel_filter, container, false);

        rangeBar = view.findViewById(R.id.rangebar);

        ArrayList<String> listDataHeader = new ArrayList<>();
        HashMap<String, List<String>> listDataChild = new HashMap<>();

        listDataHeader.add("Hotel Type");
        listDataHeader.add("Checkin Hours");
        listDataHeader.add("Amenities");
        listDataHeader.add("Star Rating");

        listDataChild.put(listDataHeader.get(0), Arrays.asList(HotelType));
        listDataChild.put(listDataHeader.get(1), Arrays.asList(cinhours));
        listDataChild.put(listDataHeader.get(2), Arrays.asList(amenities));
        listDataChild.put(listDataHeader.get(3), Arrays.asList(starcountlist));

        final Hotel_Filter_Adapter listAdapter = new Hotel_Filter_Adapter(getActivity(), listDataHeader, listDataChild);
        ((ExpandableListView) view.findViewById(R.id.lvExp)).setAdapter(listAdapter);

        view.findViewById(R.id.button).setOnClickListener(v -> {

            String g1[]=listAdapter.G1Strings().toArray(new String[0]),
                    g2[]=listAdapter.G2Strings().toArray(new String[0]),
                    g3[]=listAdapter.G3Strings().toArray(new String[0]),
                    g4[]=listAdapter.G4Strings().toArray(new String[0]);

            ArrayList<String> G1A = new ArrayList<>();
            ArrayList<String> G2A = new ArrayList<>();
            ArrayList<String> G3A = new ArrayList<>();
            ArrayList<String> G4A = new ArrayList<>();

            for(int i = 0; i < HotelType.length; i++){
                if(Arrays.asList(g1).contains(HotelType[i])){
                    G1A.add(String.valueOf(i+1));
                }
            }

            for(int i = 0; i < cinhours.length; i++){
                if(Arrays.asList(g2).contains(cinhours[i])){
                    if(i==0) {
                        G2A.add("INACTIVE");
                    }
                    if(i==1){
                        G2A.add("ACTIVE");
                    }
                }
            }

            for(int i = 0; i < starcountlist.length; i++){
                if(Arrays.asList(g4).contains(starcountlist[i])){
                    G4A.add(String.valueOf(i+1));
                }
            }
            for(int i = 0; i < amenities.length; i++){
                if(Arrays.asList(g3).contains(amenities[i])){
                    G3A.add(String.valueOf(i+1));
                }
            }

            Hotel_Search_Act hs = (Hotel_Search_Act)getActivity();
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putStringArray("g1", G1A.toArray(new String[0]));
            bundle.putStringArray("g2", G2A.toArray(new String[0]));
            bundle.putStringArray("g3", G3A.toArray(new String[0]));
            bundle.putStringArray("g4", G4A.toArray(new String[0]));
            bundle.putString("lowprice", rangeBar.getLeftPinValue());
            bundle.putString("highprice",  rangeBar.getRightPinValue());
            intent.putExtras(bundle);

            assert hs != null;
            hs.getfilters(intent);
            dismiss();
        });

        rangeBar.setOnRangeBarChangeListener((rangeBar, leftPinIndex, rightPinIndex, leftPinValue, rightPinValue) -> {
            ((TextView) view.findViewById(R.id.minval)).setText(leftPinValue);
            ((TextView) view.findViewById(R.id.maxval)).setText(rightPinValue);
        });

        rangeBar.setPinTextFormatter(value -> value);//to show larger values like 100000
        return view;
    }

    @SuppressLint("UseSparseArrays")
    public class Hotel_Filter_Adapter extends BaseExpandableListAdapter {

        private Context context;
        private HashMap<String, List<String>> list_dataChild;
        private ArrayList<String> list_dataGroup;
        private HashMap<Integer, boolean[]> childCheckStates;
        private ArrayList<String> Group1 = new ArrayList<>();
        private ArrayList<String> Group2 = new ArrayList<>();
        private ArrayList<String> Group3 = new ArrayList<>();
        private ArrayList<String> Group4 = new ArrayList<>();

        Hotel_Filter_Adapter(Context context, ArrayList<String> listDataGroup, HashMap<String, List<String>> listDataChild){

            this.context = context;
            list_dataGroup = listDataGroup;
            list_dataChild = listDataChild;

            childCheckStates = new HashMap<>();
        }

        @SuppressLint("InflateParams")
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {

            String groupText = getGroup(groupPosition);
            GroupViewHolder groupViewHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = Objects.requireNonNull(inflater).inflate(R.layout.expandble_list_group, null);
                groupViewHolder = new GroupViewHolder();
                groupViewHolder.groupTv = convertView.findViewById(R.id.HeaderTv);
                convertView.setTag(groupViewHolder);
            } else {
                groupViewHolder = (GroupViewHolder) convertView.getTag();
            }
            groupViewHolder.groupTv.setText(groupText);


            groupViewHolder.arrowIv = convertView.findViewById(R.id.arrowIV);
            if (isExpanded) {
                groupViewHolder.arrowIv.setImageResource(R.drawable.arrow1u);
            } else {
                groupViewHolder.arrowIv.setImageResource(R.drawable.arrow1d);
            }

            return convertView;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            final int mGroupPosition = groupPosition;
            final int mChildPosition = childPosition;
            String childText = getChild(mGroupPosition, mChildPosition);

            final ChildViewHolder childViewHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = Objects.requireNonNull(inflater).inflate(R.layout.item_checkbox, null);
                childViewHolder = new ChildViewHolder();
                childViewHolder.c_box = convertView.findViewById(R.id.lstcheckBox);

                convertView.setTag(R.layout.item_checkbox, childViewHolder);

            } else {
                childViewHolder = (ChildViewHolder) convertView
                        .getTag(R.layout.item_checkbox);
            }

            childViewHolder.c_box.setText(childText);

            childViewHolder.c_box.setOnCheckedChangeListener(null);

            if (childCheckStates.containsKey(mGroupPosition)) {
                boolean getChecked[] = childCheckStates.get(mGroupPosition);
                childViewHolder.c_box.setChecked(Objects.requireNonNull(getChecked)[mChildPosition]);
            } else {
                boolean getChecked[] = new boolean[getChildrenCount(mGroupPosition)];
                childCheckStates.put(mGroupPosition, getChecked);
                childViewHolder.c_box.setChecked(false);
            }

            childViewHolder.c_box.setOnCheckedChangeListener((buttonView, isChecked) -> {
                //switch (mGroupPosition) {
                //case 0:
                if (isChecked) {
                    boolean getChecked[] = childCheckStates.get(mGroupPosition);
                    Objects.requireNonNull(getChecked)[mChildPosition] = true;
                    childCheckStates.put(mGroupPosition, getChecked);
                    switch (mGroupPosition) {
                        case 0:
                            Group1.add(childViewHolder.c_box.getText().toString());
                            break;
                        case 1:
                            Group2.add(childViewHolder.c_box.getText().toString());
                            break;
                        case 2:
                            Group3.add(childViewHolder.c_box.getText().toString());
                            break;
                        case 3:
                            Group4.add(childViewHolder.c_box.getText().toString());
                            break;
                        default:
                            break;
                    }
                } else {
                    boolean getChecked[] = childCheckStates.get(mGroupPosition);
                    Objects.requireNonNull(getChecked)[mChildPosition] = false;
                    childCheckStates.put(mGroupPosition, getChecked);
                    switch (mGroupPosition) {
                        case 0:
                            Group1.remove(childViewHolder.c_box.getText().toString());
                            break;
                        case 1:
                            Group2.remove(childViewHolder.c_box.getText().toString());
                            break;
                        case 2:
                            Group3.remove(childViewHolder.c_box.getText().toString());
                            break;
                        case 3:
                            Group4.remove(childViewHolder.c_box.getText().toString());
                            break;
                        default:
                            break;
                    }
                }

            });
            return convertView;
        }

        private final class GroupViewHolder { TextView groupTv;ImageView arrowIv;}

        private final class ChildViewHolder { CheckBox c_box;}

        ArrayList<String> G1Strings(){ return Group1; }
        ArrayList<String> G2Strings(){ return Group2; }
        ArrayList<String> G3Strings(){ return Group3; }
        ArrayList<String> G4Strings(){ return Group4; }

        @Override
        public int getGroupCount() { return list_dataGroup.size(); }

        @Override
        public int getChildrenCount(int groupPos) { return Objects.requireNonNull(list_dataChild.get(list_dataGroup.get(groupPos))).size(); }

        @Override
        public String getGroup(int groupPos) { return list_dataGroup.get(groupPos); }

        @Override
        public String getChild(int groupPos, int childPos) { return Objects.requireNonNull(list_dataChild.get(list_dataGroup.get(groupPos))).get(childPos); }

        @Override
        public long getGroupId(int groupPos) { return groupPos; }

        @Override
        public long getChildId(int groupPos, int childPos) { return childPos; }

        @Override
        public boolean isChildSelectable(int groupPos, int childPos) { return false; }

        @Override
        public boolean hasStableIds() { return false; }

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;//1300;//
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

}