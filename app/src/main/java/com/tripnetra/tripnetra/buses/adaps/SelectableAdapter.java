package com.tripnetra.tripnetra.buses.adaps;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

	private SparseBooleanArray selectedItems;

	SelectableAdapter() { selectedItems = new SparseBooleanArray(); }

	boolean isSelected(int position) { return getSelectedItems().contains(position); }

	void toggleSelection(int position) {
		if (selectedItems.get(position, false)) {
			selectedItems.delete(position);
		} else {
			selectedItems.put(position, true);
		}
	}

	public void clearSelection() {
		List<Integer> selection = getSelectedItems();
		selectedItems.clear();
		for (Integer i : selection) {
			notifyItemChanged(i);
		}
	}

	int getSelectedItemCount() { return selectedItems.size(); }

	private List<Integer> getSelectedItems() {
		List<Integer> items = new ArrayList<>(selectedItems.size());
		for (int i = 0; i < selectedItems.size(); ++i) {
			items.add(selectedItems.keyAt(i));
		}
		return items;
	}

}