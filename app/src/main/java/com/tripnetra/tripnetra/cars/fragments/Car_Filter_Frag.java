package com.tripnetra.tripnetra.cars.fragments;

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
import com.tripnetra.tripnetra.cars.Car_Search_Act;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Car_Filter_Frag extends DialogFragment {

    String CarNames[]={"Sedan","Mini","SUV","Van","Hatchback","Luxury Car","Coupe/Compact","Mini Bus","Jeep","Open Car"};

    public Car_Filter_Frag() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_car_filter, container, false);

        final RangeBar rangeBar = view.findViewById(R.id.rangebar);


        ArrayList<String> listDataHeader = new ArrayList<>();
        HashMap<String, List<String>> listDataChild = new HashMap<>();

        listDataHeader.add("Car Type");
        listDataChild.put(listDataHeader.get(0), Arrays.asList(CarNames));

        final Car_Filter_Adapter caradapter = new Car_Filter_Adapter(getActivity(), listDataHeader, listDataChild);
        ((ExpandableListView) view.findViewById(R.id.lvExp)).setAdapter(caradapter);

        view.findViewById(R.id.button).setOnClickListener(v -> {

            String g1[] = caradapter.G1Strings().toArray(new String[0]);
            ArrayList<String> G1A = new ArrayList<>();

            for(int i = 0; i < CarNames.length; i++){if(Arrays.asList(g1).contains(CarNames[i])){G1A.add(String.valueOf(i+19));}}

            Car_Search_Act carsrch = (Car_Search_Act)getActivity();
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putStringArray("cartypeids", G1A.toArray(new String[0]));
            bundle.putString("lowprice", rangeBar.getLeftPinValue());
            bundle.putString("highprice",  rangeBar.getRightPinValue());
            intent.putExtras(bundle);
            assert carsrch != null;
            carsrch.getfilters(intent);
            dismiss();
        });

        rangeBar.setOnRangeBarChangeListener((rangeBar1, leftPinIndex, rightPinIndex, leftPinValue, rightPinValue) -> {
            ((TextView) view.findViewById(R.id.minval)).setText(leftPinValue);
            ((TextView) view.findViewById(R.id.maxval)).setText(rightPinValue);
        });
        rangeBar.setPinTextFormatter(value -> value);

        return view;
    }

    class Car_Filter_Adapter extends BaseExpandableListAdapter {

        private Context mContext;
        private HashMap<String, List<String>> ChildDataList;
        private ArrayList<String> GroupDataList;
        private HashMap<Integer, boolean[]> CheckedChildsList;
        private ArrayList<String> Group1 = new ArrayList<>();

        @SuppressLint("UseSparseArrays")
        Car_Filter_Adapter(Context context, ArrayList<String> listDataGroup, HashMap<String, List<String>> listDataChild){

            mContext = context;
            GroupDataList = listDataGroup;
            ChildDataList = listDataChild;

            CheckedChildsList = new HashMap<>();
        }

        @Override
        public int getGroupCount() {
            return GroupDataList.size();
        }

        @Override
        public String getGroup(int groupPosition) {
            return GroupDataList.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            String groupText = getGroup(groupPosition);
            Car_Filter_Adapter.GroupViewHolder groupViewHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                convertView = inflater.inflate(R.layout.expandble_list_group, null);
                groupViewHolder = new Car_Filter_Adapter.GroupViewHolder();
                groupViewHolder.mGroupText = convertView.findViewById(R.id.HeaderTv);
                convertView.setTag(groupViewHolder);
            } else {
                groupViewHolder = (Car_Filter_Adapter.GroupViewHolder) convertView.getTag();
            }
            groupViewHolder.mGroupText.setText(groupText);


            groupViewHolder.img = convertView.findViewById(R.id.arrowIV);
            if (isExpanded) {
                groupViewHolder.img.setImageResource(R.drawable.arrow1u);
            } else {
                groupViewHolder.img.setImageResource(R.drawable.arrow1d);
            }

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return Objects.requireNonNull(ChildDataList.get(GroupDataList.get(groupPosition))).size();
        }

        @Override
        public String getChild(int groupPosition, int childPosition) {
            return Objects.requireNonNull(ChildDataList.get(GroupDataList.get(groupPosition))).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            final int mGroupPosition = groupPosition;
            final int mChildPosition = childPosition;
            String childText = getChild(mGroupPosition, mChildPosition);

            final Car_Filter_Adapter.ChildViewHolder childViewHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                convertView = inflater.inflate(R.layout.item_checkbox, null);
                childViewHolder = new Car_Filter_Adapter.ChildViewHolder();
                childViewHolder.mCheckBox = convertView.findViewById(R.id.lstcheckBox);

                convertView.setTag(R.layout.item_checkbox, childViewHolder);

            } else {
                childViewHolder = (Car_Filter_Adapter.ChildViewHolder) convertView
                        .getTag(R.layout.item_checkbox);
            }

            childViewHolder.mCheckBox.setText(childText);

            childViewHolder.mCheckBox.setOnCheckedChangeListener(null);

            if (CheckedChildsList.containsKey(mGroupPosition)) {
                boolean getChecked[] = CheckedChildsList.get(mGroupPosition);
                childViewHolder.mCheckBox.setChecked(getChecked[mChildPosition]);
            } else {
                boolean getChecked[] = new boolean[getChildrenCount(mGroupPosition)];
                CheckedChildsList.put(mGroupPosition, getChecked);
                childViewHolder.mCheckBox.setChecked(false);
            }
            childViewHolder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                //switch (mGroupPosition) {
                //case 0:
                if (isChecked) {
                    boolean getChecked[] = CheckedChildsList.get(mGroupPosition);
                    getChecked[mChildPosition] = true;
                    CheckedChildsList.put(mGroupPosition, getChecked);
                    switch (mGroupPosition) {
                        case 0:
                            Group1.add(childViewHolder.mCheckBox.getText().toString());
                            break;
                        default:
                            break;
                    }
                } else {
                    boolean getChecked[] = CheckedChildsList.get(mGroupPosition);
                    getChecked[mChildPosition] = false;
                    CheckedChildsList.put(mGroupPosition, getChecked);
                    switch (mGroupPosition) {
                        case 0:
                            Group1.remove(childViewHolder.mCheckBox.getText().toString());
                            break;
                        default:
                            break;
                    }
                }

            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        private final class GroupViewHolder {
            TextView mGroupText;
            ImageView img;
        }

        private final class ChildViewHolder {
            CheckBox mCheckBox;
        }

        private ArrayList<String> G1Strings(){
            return Group1;
        }


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