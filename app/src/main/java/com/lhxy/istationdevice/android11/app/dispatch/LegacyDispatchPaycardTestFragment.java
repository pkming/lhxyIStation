package com.lhxy.istationdevice.android11.app.dispatch;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
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
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.util.Locale;

public final class LegacyDispatchPaycardTestFragment extends Fragment {
    private static final long POLL_INTERVAL_MS = 400L;
    private static final long WAIT_CARD_REMOVED_TIMEOUT_MS = 5000L;
    private static final long WAIT_CARD_REMOVED_POLL_MS = 150L;

    private TextView tvPaycard;
    private AudioManager audioManager;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private TextToSpeech textToSpeech;
    private Thread pollThread;
    private volatile boolean polling;
    private volatile boolean ttsReady;
    private volatile String pendingSpeechText = "";
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
        audioManager = (AudioManager) view.getContext().getSystemService(android.content.Context.AUDIO_SERVICE);
        Button butBroadcastTest = view.findViewById(R.id.butBroadcastTest);
        initTts();
        renderCard();
        startPolling();
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
    public void onDestroy() {
        stopPolling();
        stopDispatchSpeech();
        shutdownTts();
        super.onDestroy();
    }

    private void initTts() {
        if (getContext() == null || textToSpeech != null) {
            return;
        }
        textToSpeech = new TextToSpeech(getContext(), status -> {
            ttsReady = status == TextToSpeech.SUCCESS;
            if (ttsReady && textToSpeech != null) {
                textToSpeech.setLanguage(Locale.CHINA);
                if (!pendingSpeechText.trim().isEmpty()) {
                    speakCurrentCard(pendingSpeechText.trim());
                }
            }
        });
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onDone(String utteranceId) {
                mainHandler.post(LegacyDispatchPaycardTestFragment.this::disableDispatchHorn);
            }

            @Override
            public void onError(String utteranceId) {
                mainHandler.post(LegacyDispatchPaycardTestFragment.this::disableDispatchHorn);
            }

            @Override
            public void onStop(String utteranceId, boolean interrupted) {
                mainHandler.post(LegacyDispatchPaycardTestFragment.this::disableDispatchHorn);
            }
        });
    }

    private void handleBroadcastTest() {
        String cardNo = currentCardNo == null ? "" : currentCardNo.trim();
        if (cardNo.isEmpty()) {
            toast(getString(R.string.paycard_fail));
            return;
        }
        speakCurrentCard(cardNo);
        toast(getString(R.string.paycard_suss));
    }

    private void startPolling() {
        if (pollThread != null) {
            return;
        }
        polling = true;
        pollThread = new Thread(this::pollLoop, "dispatch-paycard-poll");
        pollThread.start();
    }

    private void stopPolling() {
        polling = false;
        Thread thread = pollThread;
        pollThread = null;
        if (thread != null) {
            thread.interrupt();
        }
    }

    private void pollLoop() {
        while (polling) {
            String cardNo = readCardDirectly();
            if (!cardNo.isEmpty()) {
                onCardDetected(cardNo);
                waitForCardRemoval();
            }
            try {
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void waitForCardRemoval() {
        try {
            ShellRuntime.get().getRfidAdapter().waitCardRemoved(
                    "legacy-paycard-wait-off-" + System.currentTimeMillis(),
                    WAIT_CARD_REMOVED_TIMEOUT_MS,
                    WAIT_CARD_REMOVED_POLL_MS
            );
        } catch (Exception ignore) {
            // 某些桥接只支持读一次卡号；这种场景保持页面继续轮询即可。
        }
    }

    private String readCardDirectly() {
        try {
            if (!ShellRuntime.get().getRfidAdapter().isAvailable()) {
                return "";
            }
            String cardNo = ShellRuntime.get().getRfidAdapter().readCard("legacy-paycard-test-" + System.currentTimeMillis());
            return cardNo == null ? "" : cardNo.trim();
        } catch (Exception ignore) {
            return "";
        }
    }

    private void onCardDetected(String cardNo) {
        currentCardNo = cardNo;
        mainHandler.post(this::renderCard);
    }

    private void speakCurrentCard(String cardNo) {
        String normalized = cardNo == null ? "" : cardNo.trim();
        if (normalized.isEmpty()) {
            disableDispatchHorn();
            return;
        }
        pendingSpeechText = normalized;
        initTts();
        enableDispatchHorn();
        applyDispatchVolume();
        if (!ttsReady || textToSpeech == null) {
            return;
        }
        pendingSpeechText = "";
        textToSpeech.stop();
        textToSpeech.speak(normalized, TextToSpeech.QUEUE_FLUSH, null, "dispatch-paycard-test");
    }

    private void stopDispatchSpeech() {
        pendingSpeechText = "";
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        disableDispatchHorn();
    }

    private void shutdownTts() {
        ttsReady = false;
        if (textToSpeech == null) {
            return;
        }
        textToSpeech.stop();
        textToSpeech.shutdown();
        textToSpeech = null;
    }

    private void applyDispatchVolume() {
        if (audioManager == null) {
            return;
        }
        ShellConfig shellConfig = ShellRuntime.get().getActiveConfig();
        if (shellConfig == null) {
            return;
        }
        int targetVolume = shellConfig.getBasicSetupConfig().getOtherSettings().getDispatchVolume();
        int bounded = Math.max(0, Math.min(targetVolume, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)));
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, bounded, 0);
    }

    private void enableDispatchHorn() {
        writePin("inner_audio", 0);
        writePin("outer_audio", 0);
        writePin("headphone_detect_power", 0);
        writePin("inner_speaker", 1);
    }

    private void disableDispatchHorn() {
        writePin("inner_audio", 0);
        writePin("outer_audio", 0);
        writePin("headphone_detect_power", 1);
        writePin("inner_speaker", 0);
    }

    private void writePin(String pinKey, int value) {
        ShellConfig shellConfig = ShellRuntime.get().getActiveConfig();
        if (shellConfig == null || !shellConfig.getGpioConfig().getPins().containsKey(pinKey)) {
            return;
        }
        try {
            ShellRuntime.get().getGpioAdapter().write(pinKey, value, "dispatch-paycard-" + pinKey + "-" + value);
        } catch (Exception ignore) {
            // GPIO 不可用时仍允许页面完成刷卡测试。
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