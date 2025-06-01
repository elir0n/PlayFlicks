package com.example.myapplication.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adapter for displaying a list of categories with checkboxes.
 */
public class CategorySelectionAdapter extends RecyclerView.Adapter<CategorySelectionAdapter.CategoryCheckBoxViewHolder> {

    private final List<String> categoryTitles; // List of category names
    private final List<Boolean> isChecked; // Tracks checked state of each category
    private final OnItemClickListener listener; // Click listener for checkboxes

    /**
     * Interface for handling checkbox click events.
     */
    public interface OnItemClickListener {
        void onItemClick(CheckBox item);
    }

    /**
     * Constructor for the adapter.
     *
     * @param categoryTitles List of category titles.
     * @param listener       Listener for checkbox click events.
     */
    public CategorySelectionAdapter(List<String> categoryTitles, OnItemClickListener listener) {
        this.categoryTitles = categoryTitles != null ? new ArrayList<>(categoryTitles) : new ArrayList<>();
        this.isChecked = new ArrayList<>(Collections.nCopies(this.categoryTitles.size(), false));
        this.listener = listener;
    }

    /**
     * Sets selected categories, updating their checked states.
     *
     * @param selectedCategories List of selected category titles.
     */
    public void setSelectedCategories(List<String> selectedCategories) {
        if (selectedCategories == null) return;
        for (int i = 0; i < categoryTitles.size(); i++) {
            boolean newState = selectedCategories.contains(categoryTitles.get(i));
            if (isChecked.get(i) != newState) {
                isChecked.set(i, newState);
                notifyItemChanged(i);
            }
        }
    }

    @NonNull
    @Override
    public CategoryCheckBoxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_chekbox, parent, false);
        return new CategoryCheckBoxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryCheckBoxViewHolder holder, int position) {
        holder.bind(categoryTitles.get(position), position);
    }

    @Override
    public int getItemCount() {
        return categoryTitles.size();
    }

    /**
     * Updates the adapter's data and preserves previous checked states when possible.
     *
     * @param newCategoryTitles List of new category titles.
     */
    public void updateData(List<String> newCategoryTitles) {
        if (newCategoryTitles == null) {
            int oldSize = categoryTitles.size();
            categoryTitles.clear();
            isChecked.clear();
            notifyItemRangeRemoved(0, oldSize);
            return;
        }

        // Preserve checked states for existing categories
        List<Boolean> newIsChecked = new ArrayList<>(Collections.nCopies(newCategoryTitles.size(), false));
        for (int i = 0; i < Math.min(categoryTitles.size(), newCategoryTitles.size()); i++) {
            if (categoryTitles.get(i).equals(newCategoryTitles.get(i))) {
                newIsChecked.set(i, isChecked.get(i));
            }
        }

        categoryTitles.clear();
        categoryTitles.addAll(newCategoryTitles);
        isChecked.clear();
        isChecked.addAll(newIsChecked);

        notifyItemRangeChanged(0, categoryTitles.size());
    }

    /**
     * ViewHolder for the category checkboxes.
     */
    public class CategoryCheckBoxViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox cbCategoryTitle;

        public CategoryCheckBoxViewHolder(@NonNull View itemView) {
            super(itemView);
            cbCategoryTitle = itemView.findViewById(R.id.cbCategoryTitle);
        }

        /**
         * Binds data to the checkbox view.
         *
         * @param title    Category title.
         * @param position Position in the list.
         */
        public void bind(String title, int position) {
            cbCategoryTitle.setOnCheckedChangeListener(null); // Avoid triggering listener during setup
            cbCategoryTitle.setText(title);
            cbCategoryTitle.setChecked(isChecked.get(position));

            cbCategoryTitle.setOnCheckedChangeListener((buttonView, checked) -> {
                isChecked.set(position, checked);
                listener.onItemClick(cbCategoryTitle);
            });
        }
    }
}
