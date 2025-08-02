package com.example.sqlite0.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqlite0.R;
import com.example.sqlite0.adapters.ItemAdapter;
import com.example.sqlite0.models.Item;
import com.example.sqlite0.utils.PreferenceUtils;
import com.example.sqlite0.viewmodels.ItemViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentHistory extends Fragment {

    private RecyclerView rvItems;
    private ItemAdapter itemAdapter;
    private final List<Item> itemList = new ArrayList<>();
    private ItemViewModel itemViewModel;
    private PreferenceUtils preferenceUtils;
    private Spinner spinnerFilter;

    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Ánh xạ RecyclerView và Spinner
        rvItems = view.findViewById(R.id.rv_expenses);
        spinnerFilter = view.findViewById(R.id.spinner_filter);

        rvItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        itemAdapter = new ItemAdapter(requireContext(), itemList);
        rvItems.setAdapter(itemAdapter);

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        preferenceUtils = new PreferenceUtils(requireContext());

        setupSpinner();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load  "All"
        filterItemsByCategory("All");
    }

    private void setupSpinner() {
        String[] categories = {"All", "Food", "Shopping", "Study", "Entertainment"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                filterItemsByCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });
    }

    private void filterItemsByCategory(String category) {
        int userId = preferenceUtils.getUserId();
        if (userId == -1) {
            Log.e("FragmentHistory", "User ID không hợp lệ");
            return;
        }

        itemViewModel.getItems(userId).observe(getViewLifecycleOwner(), items -> {
            if (items == null) return;

            itemList.clear();
            for (Item item : items) {
                if (category.equals("All") || item.getCategory().equalsIgnoreCase(category)) {
                    itemList.add(item);
                }
            }

            // Sắp xếp theo ngày giảm dần
            Collections.sort(itemList, (item1, item2) -> {
                try {
                    Date date1 = displayDateFormat.parse(item1.getDate());
                    Date date2 = displayDateFormat.parse(item2.getDate());
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    Log.e("DateParse", "Lỗi định dạng ngày: " + e.getMessage());
                    return 0;
                }
            });

            itemAdapter.updateData(itemList);
        });
    }
}
