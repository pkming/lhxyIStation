package com.lhxy.istationdevice.android11.app.dispatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.lhxy.istationdevice.android11.domain.module.SignInBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.TerminalBusinessModule;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

public final class LegacyDispatchPaycardTestFragment extends Fragment {
    private static final long POLL_INTERVAL_MS = 400L;
    private static final long WAIT_CARD_REMOVED_TIMEOUT_MS = 5000L;
    private static final long WAIT_CARD_REMOVED_POLL_MS = 150L;

    private TextView tvPaycard;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private LegacyCardSpeechController cardSpeechController;
    private Thread pollThread;
    private volatile boolean polling;
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
        cardSpeechController = new LegacyCardSpeechController(
                view.getContext(),
                "PaycardTest",
                "paycard-tts",
                "dispatch-paycard",
                "dispatch-paycard-test",
                LegacyCardSpeechController.VolumeSource.DISPATCH
        );
        Button butBroadcastTest = view.findViewById(R.id.butBroadcastTest);
        cardSpeechController.init();
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
        if (cardSpeechController != null) {
            cardSpeechController.stop();
            cardSpeechController.shutdown();
        }
        super.onDestroy();
    }

    private void handleBroadcastTest() {
        String cardNo = currentCardNo == null ? "" : currentCardNo.trim();
        if (cardNo.isEmpty()) {
            toast(getString(R.string.paycard_fail));
            return;
        }
        if (cardSpeechController != null) {
            cardSpeechController.speakCard(cardNo);
        }
        toast(getString(R.string.paycard_suss));
    }

    private void startPolling() {
        if (pollThread != null) {
            return;
        }
        SignInBusinessModule signInModule = findSignInModule();
        if (signInModule != null) {
            signInModule.pauseAutoPollingForExclusiveRfid("legacy-paycard-exclusive-start");
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
        SignInBusinessModule signInModule = findSignInModule();
        if (signInModule != null) {
            signInModule.resumeAutoPollingAfterExclusiveRfid("legacy-paycard-exclusive-stop");
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

    private SignInBusinessModule findSignInModule() {
        TerminalBusinessModule module = ShellRuntime.get().getModuleHub().findModule("signin");
        return module instanceof SignInBusinessModule ? (SignInBusinessModule) module : null;
    }

    private void onCardDetected(String cardNo) {
        currentCardNo = cardNo;
        mainHandler.post(this::renderCard);
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
