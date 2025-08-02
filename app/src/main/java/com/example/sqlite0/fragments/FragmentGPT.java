package com.example.sqlite0.fragments;

import static android.os.Build.VERSION_CODES.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.sqlite0.R;

public class FragmentGPT extends Fragment {

    private EditText etPrompt;
    private Button btnSend;
    private TextView tvResponse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gpt, container, false);

        etPrompt = view.findViewById(R.id.et_prompt);
        btnSend = view.findViewById(R.id.btn_send);
        tvResponse = view.findViewById(R.id.tv_response);

        btnSend.setOnClickListener(v -> {
            String prompt = etPrompt.getText().toString().trim();
            if (!prompt.isEmpty()) {
                // Giả lập phản hồi của GPT
                String fakeResponse = getFakeResponse(prompt);
                tvResponse.setText("You: " + prompt + "\n\nGPT: " + fakeResponse);
                etPrompt.setText("");
            }
        });

        return view;
    }

    // Phản hồi mô phỏng
    private String getFakeResponse(String prompt) {
        if (prompt.toLowerCase().contains("hello")) {
            return "Hi there! 😊";
        } else if (prompt.toLowerCase().contains("how are you")) {
            return "I'm just code, but I'm doing great!";
        } else {
            return "Sorry, I'm just a demo bot. 😅";
        }
    }
}
