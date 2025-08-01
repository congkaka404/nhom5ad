package com.example.sqlite0.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Pattern;

public class ValidationUtils {
    // Không nên dùng 1 instance dùng chung nếu có thể bị dùng từ nhiều luồng
    private static final ThreadLocal<SimpleDateFormat> dateFormatThreadLocal =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()));

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // Kiểm tra các trường có rỗng không
    public static boolean isEmpty(String... fields) {
        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // Kiểm tra định dạng ngày dd/MM/yyyy
    public static boolean isValidDate(String dateStr) {
        try {
            SimpleDateFormat dateFormat = dateFormatThreadLocal.get();
            dateFormat.setLenient(false);
            dateFormat.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Kiểm tra giá trị giá > minValue
    public static boolean isValidPrice(String priceStr, double minValue) {
        try {
            double priceValue = Double.parseDouble(priceStr);
            return priceValue > minValue;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Kiểm tra định dạng email cơ bản
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    // Kiểm tra độ dài mật khẩu
    public static boolean isValidPassword(String password, int minLength) {
        return password != null && password.length() >= minLength;
    }
}
