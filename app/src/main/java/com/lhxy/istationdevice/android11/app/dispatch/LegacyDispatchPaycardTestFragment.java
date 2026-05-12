package com.lhxy.istationdevice.android11.app.dispatch;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.util.Locale;

public final class LegacyDispatchPaycardTestFragment extends Fragment {
    private TextView tvPaycard;
    private TextToSpeech textToSpeech;
    private String currentCardNo = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_paycard_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvPaycard = view.findViewById(R.id.tvPaycard);
        Button butBroadcastTest = view.findViewById(R.id.butBroadcastTest);
        initTts();
        renderCard();
        if (butBroadcastTest != null) {
            butBroadcastTest.setOnClickListener(v -> handleBroadcastTest());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        renderCard();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
    }

    private void initTts() {
        if (getContext() == null || textToSpeech != null) {
            return;
        }
        textToSpeech = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS && textToSpeech != null) {
                textToSpeech.setLanguage(Locale.getDefault());
            }
        });
    }

    private void handleBroadcastTest() {
        String cardNo = readCardDirectly();
        if (cardNo == null || cardNo.trim().isEmpty()) {
            toast(getString(R.string.paycard_fail));
            return;
        }
        currentCardNo = cardNo.trim();
        renderCard();
        if (textToSpeech != null) {
            textToSpeech.speak(currentCardNo, TextToSpeech.QUEUE_FLUSH, null, "dispatch-paycard-test");
        }
        toast(getString(R.string.paycard_suss));
    }

    private String readCardDirectly() {
        try {
            if (!ShellRuntime.get().getRfidAdapter().isAvailable()) {
                return currentCardNo;
            }
            return ShellRuntime.get().getRfidAdapter().readCard("legacy-paycard-test-" + System.currentTimeMillis());
        } catch (Exception ignore) {
            return "";
        }
    }

    private void renderCard() {
        if (tvPaycard == null) {
            return;
        }
        if (currentCardNo == null || currentCardNo.trim().isEmpty()) {
            tvPaycard.setText(getString(R.string.dispatch_center_paycard_empty));
            return;
        }
        tvPaycard.setText(currentCardNo);
    }

    private void toast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}