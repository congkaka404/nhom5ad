package com.example.sqlite0.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sqlite0.R;
import com.example.sqlite0.models.Item;
import com.example.sqlite0.utils.PreferenceUtils;
import com.example.sqlite0.viewmodels.ItemViewModel;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentDashboard extends Fragment {

    private TextView tvTotalMonth;
    private ItemViewModel itemViewModel;
    private PreferenceUtils preferenceUtils;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvTotalMonth = view.findViewById(R.id.tv_total_month);
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        preferenceUtils = new PreferenceUtils(getContext());

        loadData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        int userId = preferenceUtils.getUserId();
        if (userId == -1) return;

        Date start = getMonthStart();
        Date end = getMonthEnd();

        itemViewModel.getItems(userId).observe(getViewLifecycleOwner(), items -> {
            double total = calculateTotalInRange(items, start, end);
            tvTotalMonth.setText("Total of month: " + formatCurrency(total));
        });
    }

    private double calculateTotalInRange(List<Item> items, Date start, Date end) {
        double total = 0;

        for (Item item : items) {
            try {
                Date itemDate = dateFormat.parse(item.getDate());
                if (itemDate != null && !itemDate.before(start) && !itemDate.after(end)) {
                    String priceStr = item.getPrice().replaceAll("[^\\d.]", ""); // loại bỏ K, dấu phẩy, khoảng trắng
                    total += Double.parseDouble(priceStr);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return total;
    }

    private Date getMonthStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Date getMonthEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    private String formatCurrency(double amount) {
        return String.format(Locale.getDefault(), "%.0fK", amount);
    }
}
