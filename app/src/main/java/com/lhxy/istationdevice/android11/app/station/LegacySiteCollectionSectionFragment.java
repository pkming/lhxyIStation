package com.lhxy.istationdevice.android11.app.station;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.lhxy.istationdevice.android11.app.R;
import com.lhxy.istationdevice.android11.app.common.LegacyWheelPickerDialog;
import com.lhxy.istationdevice.android11.app.line.LegacyLineCatalog;
import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.core.TraceIds;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.domain.module.ModuleRunResult;
import com.lhxy.istationdevice.android11.domain.module.StationBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.TerminalBusinessModule;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.util.List;
import java.util.Locale;

/**
 * 旧版站点学习右侧内容区宿主。
 * <p>
 * 旧项目这里是“线路/方向/站点或属性选择 + GPS 实时采集 + 保存覆盖数据库”的完整流程。
 * 新壳当前先承接旧页面壳、GPS 展示和“保存并推进下一项”流程，
 * 站点/提醒点学习结果会按线路和方向落到本地持久层。
 */
public final class LegacySiteCollectionSectionFragment extends Fragment {
    private static final String ARG_LAYOUT = "layout";
    private static final String ARG_SECTION = "section";

    private static final String SECTION_SITE = "SITE";
    private static final String SECTION_OTHER = "OTHER";

    private String selectedLineName = "101路";
    private int selectedSiteIndex;
    private int selectedAttributeIndex;
    private final Handler refreshHandler = new Handler(Looper.getMainLooper());
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            rerenderCurrentSection();
            refreshHandler.postDelayed(this, 1000L);
        }
    };

    public static LegacySiteCollectionSectionFragment newInstance(@LayoutRes int layoutRes, String sectionName) {
        LegacySiteCollectionSectionFragment fragment = new LegacySiteCollectionSectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layoutRes);
        args.putString(ARG_SECTION, sectionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutRes = requireArguments().getInt(ARG_LAYOUT, 0);
        return inflater.inflate(layoutRes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (SECTION_OTHER.equals(requireSection())) {
            bindOtherCollection(view);
        } else {
            bindSiteCollection(view);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        rerenderCurrentSection();
        refreshHandler.post(refreshRunnable);
    }

    @Override
    public void onPause() {
        refreshHandler.removeCallbacks(refreshRunnable);
        super.onPause();
    }

    private void bindSiteCollection(View view) {
        StationState stationState = requireStationState();
        selectedLineName = resolveCurrentLineName(stationState);
        applySelectedLineProfile(stationState.getDirectionText());

        TextView tvLineName = view.findViewById(R.id.tvCollectionLineName);
        TextView tvSiteName = view.findViewById(R.id.tvCollectionSiteName);
        TextView tvOrderNumber = view.findViewById(R.id.tvCollectionOrderNumber);
        RadioGroup directionGroup = view.findViewById(R.id.rgCollectionDirection);
        RadioButton rbUpstream = view.findViewById(R.id.rbDirectionUpstream);
        RadioButton rbDownstream = view.findViewById(R.id.rbDirectionDown);
        Button saveButton = view.findViewById(R.id.butCollectionOperation);

        bindDirection(directionGroup, rbUpstream, rbDownstream, stationState.getDirectionText());
        bindLinePicker(tvLineName);
        bindSitePicker(tvSiteName, tvOrderNumber, false);
        renderSiteCollection(view, tvLineName, tvSiteName, tvOrderNumber);

        if (saveButton != null) {
            saveButton.setOnClickListener(v -> confirmSaveCollection(view, false));
        }
    }

    private void bindOtherCollection(View view) {
        StationState stationState = requireStationState();
        selectedLineName = resolveCurrentLineName(stationState);
        applySelectedLineProfile(stationState.getDirectionText());

        TextView tvLineName = view.findViewById(R.id.tvCollectionOtherLineName);
        TextView tvAttributeName = view.findViewById(R.id.tvCollectionOtherSiteName);
        TextView tvOrderNumber = view.findViewById(R.id.tvCollectionOtherOrderNumber);
        RadioGroup directionGroup = view.findViewById(R.id.rgCollectionOtherDirection);
        RadioButton rbUpstream = view.findViewById(R.id.rbOtherDirectionUpstream);
        RadioButton rbDownstream = view.findViewById(R.id.rbOtherDirectionDown);
        Button saveButton = view.findViewById(R.id.butCollectionOtherOperation);

        bindDirection(directionGroup, rbUpstream, rbDownstream, stationState.getDirectionText());
        bindLinePicker(tvLineName);
        bindSitePicker(tvAttributeName, tvOrderNumber, true);
        renderOtherCollection(view, tvLineName, tvAttributeName, tvOrderNumber);

        if (saveButton != null) {
            saveButton.setOnClickListener(v -> confirmSaveCollection(view, true));
        }
    }

    private void confirmSaveCollection(@NonNull View root, boolean otherSection) {
        if (getContext() == null) {
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setMessage(R.string.collection_dlg_msg)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> saveCollection(root, otherSection))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void bindDirection(
            @Nullable RadioGroup radioGroup,
            @Nullable RadioButton rbUpstream,
            @Nullable RadioButton rbDownstream,
            @Nullable String directionText
    ) {
        boolean upstream = directionText == null || directionText.contains("上");
        if (rbUpstream != null) {
            rbUpstream.setChecked(upstream);
        }
        if (rbDownstream != null) {
            rbDownstream.setChecked(!upstream);
        }
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                String selectedDirection = checkedId == R.id.rbDirectionDown || checkedId == R.id.rbOtherDirectionDown
                        ? "下行"
                        : "上行";
                applySelectedLineProfile(selectedDirection);
                selectedSiteIndex = 0;
                selectedAttributeIndex = 0;
                View root = getView();
                if (root == null) {
                    return;
                }
                if (SECTION_OTHER.equals(requireSection())) {
                    renderOtherCollection(
                            root,
                            root.findViewById(R.id.tvCollectionOtherLineName),
                            root.findViewById(R.id.tvCollectionOtherSiteName),
                            root.findViewById(R.id.tvCollectionOtherOrderNumber)
                    );
                } else {
                    renderSiteCollection(
                            root,
                            root.findViewById(R.id.tvCollectionLineName),
                            root.findViewById(R.id.tvCollectionSiteName),
                            root.findViewById(R.id.tvCollectionOrderNumber)
                    );
                }
            });
        }
    }

    private void bindLinePicker(@Nullable TextView tvLineName) {
        if (tvLineName == null) {
            return;
        }
        tvLineName.setOnClickListener(v -> {
            List<LegacyLineCatalog.LineProfile> profiles = LegacyLineCatalog.all(requireContext());
            if (profiles.isEmpty()) {
                return;
            }
            java.util.ArrayList<String> options = new java.util.ArrayList<>(profiles.size());
            int checkedItem = 0;
            for (int index = 0; index < profiles.size(); index++) {
                LegacyLineCatalog.LineProfile profile = profiles.get(index);
                options.add(profile.getLineName());
                if (profile.matchesLineName(selectedLineName)) {
                    checkedItem = index;
                }
            }
            showSelectionDialog(
                    getString(R.string.collection_line_wp_hint),
                    options,
                    checkedItem,
                    selectedIndex -> {
                        selectedLineName = profiles.get(selectedIndex).getLineName();
                        selectedSiteIndex = 0;
                        selectedAttributeIndex = 0;
                        applySelectedLineProfile(requireStationState().getDirectionText());
                        rerenderCurrentSection();
                    }
            );
        });
    }

    private void bindSitePicker(@Nullable TextView targetView, @Nullable TextView orderView, boolean otherSection) {
        if (targetView == null) {
            return;
        }
        targetView.setOnClickListener(v -> {
            List<String> candidates = otherSection ? buildOtherCandidates() : buildSiteCandidates();
            if (candidates.isEmpty()) {
                return;
            }
            showSelectionDialog(
                    getString(otherSection ? R.string.collection_attribute_wp_hint : R.string.collection_site_name_wp_hint),
                    candidates,
                    otherSection ? selectedAttributeIndex : selectedSiteIndex,
                    selectedIndex -> {
                        if (otherSection) {
                            selectedAttributeIndex = selectedIndex;
                        } else {
                            selectedSiteIndex = selectedIndex;
                        }
                        rerenderCurrentSection();
                    }
            );
        });
    }

    private void showSelectionDialog(
            @NonNull String title,
            @NonNull List<String> options,
            int checkedItem,
            @NonNull SelectionCallback callback
    ) {
        if (getContext() == null || options.isEmpty()) {
            return;
        }
        LegacyWheelPickerDialog.show(
                requireContext(),
                title,
                options,
                checkedItem,
                (selectedIndex, selectedValue) -> callback.onSelected(selectedIndex)
        );
    }

    private void renderSiteCollection(
            @NonNull View root,
            @Nullable TextView tvLineName,
            @Nullable TextView tvSiteName,
            @Nullable TextView tvOrderNumber
    ) {
        List<String> candidates = buildSiteCandidates();
        String siteName = candidates.get(Math.min(selectedSiteIndex, candidates.size() - 1));
        LegacySiteCollectionStore.LearnedValue learnedValue = loadLearnedValue(false, siteName);
        bindText(tvLineName, selectedLineName);
        bindText(tvSiteName, siteName);
        bindText(tvOrderNumber, String.valueOf(Math.min(selectedSiteIndex, candidates.size() - 1) + 1));
        renderGpsPanels(
                root,
            learnedValue,
                ShellRuntime.get().getGpsSerialMonitor().getLatestSnapshot(),
                R.id.tvLongitude,
                R.id.tvLatitude,
                R.id.tvSpeed,
                R.id.tvAngle,
                R.id.tvAltitude,
                R.id.tvCurrentLongitude,
                R.id.tvCurrentLatitude,
                R.id.tvCurrentSpeed,
                R.id.tvCurrentAngle,
                R.id.tvCurrentAltitude
        );
    }

    private void renderOtherCollection(
            @NonNull View root,
            @Nullable TextView tvLineName,
            @Nullable TextView tvAttributeName,
            @Nullable TextView tvOrderNumber
    ) {
        List<String> candidates = buildOtherCandidates();
        String attribute = candidates.get(Math.min(selectedAttributeIndex, candidates.size() - 1));
        LegacySiteCollectionStore.LearnedValue learnedValue = loadLearnedValue(true, attribute);
        bindText(tvLineName, selectedLineName);
        bindText(tvAttributeName, attribute);
        bindText(tvOrderNumber, String.valueOf(Math.min(selectedAttributeIndex, candidates.size() - 1) + 1));
        renderGpsPanels(
                root,
            learnedValue,
                ShellRuntime.get().getGpsSerialMonitor().getLatestSnapshot(),
                R.id.tvOtherLongitude,
                R.id.tvOtherLatitude,
                R.id.tvOtherSpeed,
                R.id.tvOtherAngle,
                R.id.tvOtherAltitude,
                R.id.tvOtherCurrentLongitude,
                R.id.tvOtherCurrentLatitude,
                R.id.tvOtherCurrentSpeed,
                R.id.tvOtherCurrentAngle,
                R.id.tvOtherCurrentAltitude
        );
    }

    private void renderGpsPanels(
            @NonNull View root,
            @Nullable LegacySiteCollectionStore.LearnedValue learned,
            @Nullable GpsFixSnapshot current,
            int learnedLongitudeId,
            int learnedLatitudeId,
            int learnedSpeedId,
            int learnedAngleId,
            int learnedAltitudeId,
            int currentLongitudeId,
            int currentLatitudeId,
            int currentSpeedId,
            int currentAngleId,
            int currentAltitudeId
    ) {
        bindText(root.findViewById(learnedLongitudeId), formatLearnedLongitude(learned));
        bindText(root.findViewById(learnedLatitudeId), formatLearnedLatitude(learned));
        bindText(root.findViewById(learnedSpeedId), formatLearnedSpeed(learned));
        bindText(root.findViewById(learnedAngleId), formatLearnedAngle(learned));
        bindText(root.findViewById(learnedAltitudeId), formatLearnedAltitude(learned));
        bindText(root.findViewById(currentLongitudeId), formatLongitude(current));
        bindText(root.findViewById(currentLatitudeId), formatLatitude(current));
        bindText(root.findViewById(currentSpeedId), formatSpeed(current));
        bindText(root.findViewById(currentAngleId), formatAngle(current));
        bindText(root.findViewById(currentAltitudeId), formatAltitude(current));
    }

    private void saveCollection(@NonNull View root, boolean otherSection) {
        ShellRuntime runtime = ShellRuntime.get();
        GpsSerialMonitor gpsMonitor = runtime.getGpsSerialMonitor();
        GpsFixSnapshot currentSnapshot = gpsMonitor.getLatestSnapshot();
        if (!hasLearnableGps(currentSnapshot)) {
            toast(getString(R.string.legacy_site_collection_save_failed));
            return;
        }
        String currentItemName = otherSection
            ? buildOtherCandidates().get(Math.min(selectedAttributeIndex, buildOtherCandidates().size() - 1))
            : buildSiteCandidates().get(Math.min(selectedSiteIndex, buildSiteCandidates().size() - 1));
        int currentItemIndex = otherSection ? selectedAttributeIndex : selectedSiteIndex;
        try {
            if (otherSection) {
                LegacySiteCollectionResourceStore.saveReminder(
                        requireContext(),
                        selectedLineName,
                        requireStationState().getDirectionText(),
                        currentItemName,
                        currentItemIndex,
                        currentSnapshot
                );
            } else {
                LegacySiteCollectionResourceStore.saveSite(
                        requireContext(),
                        selectedLineName,
                        requireStationState().getDirectionText(),
                        currentItemName,
                        currentItemIndex,
                        currentSnapshot
                );
            }
            TerminalBusinessModule module = runtime.getModuleHub().findModule("station");
            if (module instanceof StationBusinessModule) {
                ((StationBusinessModule) module).reloadRouteResources();
            }
        } catch (Exception e) {
            toast(valueOrDefault(e.getMessage(), getString(R.string.legacy_site_collection_save_failed)));
            return;
        }
        LegacySiteCollectionStore.save(
            requireContext(),
            requireSection(),
            selectedLineName,
            requireStationState().getDirectionText(),
            currentItemName,
            currentItemIndex,
            currentSnapshot
        );
        applySelectedLineProfile(requireStationState().getDirectionText());
        ModuleRunResult result = runtime.getModuleHub()
                .runAction("station", "advance_station", TraceIds.next(otherSection ? "legacy-other-collection" : "legacy-site-collection"));
        boolean switchedDirection = false;
        if (otherSection) {
            int nextIndex = selectedAttributeIndex + 1;
            if (nextIndex >= buildOtherCandidates().size()) {
                switchedDirection = switchDirectionForLearning();
                selectedAttributeIndex = 0;
            } else {
                selectedAttributeIndex = nextIndex;
            }
            renderOtherCollection(
                    root,
                    root.findViewById(R.id.tvCollectionOtherLineName),
                    root.findViewById(R.id.tvCollectionOtherSiteName),
                    root.findViewById(R.id.tvCollectionOtherOrderNumber)
            );
        } else {
            int nextIndex = selectedSiteIndex + 1;
            if (nextIndex >= buildSiteCandidates().size()) {
                switchedDirection = switchDirectionForLearning();
                selectedSiteIndex = 0;
            } else {
                selectedSiteIndex = nextIndex;
            }
            renderSiteCollection(
                    root,
                    root.findViewById(R.id.tvCollectionLineName),
                    root.findViewById(R.id.tvCollectionSiteName),
                    root.findViewById(R.id.tvCollectionOrderNumber)
            );
        }
        AppLogCenter.log(
                LogCategory.UI,
                LogLevel.INFO,
                "LegacySiteCollection",
                result.describeInline(),
                TraceIds.next("legacy-site-collection-ui")
        );
        String message = result.isSuccess()
                ? getString(R.string.legacy_site_collection_saved)
                : getString(R.string.legacy_site_collection_save_failed);
        if (switchedDirection) {
            message = message + " " + getString(R.string.legacy_site_collection_switched_direction, requireStationState().getDirectionText());
        }
        toast(message);
    }

    private void rerenderCurrentSection() {
        View root = getView();
        if (root == null) {
            return;
        }
        if (SECTION_OTHER.equals(requireSection())) {
            renderOtherCollection(
                    root,
                    root.findViewById(R.id.tvCollectionOtherLineName),
                    root.findViewById(R.id.tvCollectionOtherSiteName),
                    root.findViewById(R.id.tvCollectionOtherOrderNumber)
            );
            return;
        }
        renderSiteCollection(
                root,
                root.findViewById(R.id.tvCollectionLineName),
                root.findViewById(R.id.tvCollectionSiteName),
                root.findViewById(R.id.tvCollectionOrderNumber)
        );
    }

    private List<String> buildSiteCandidates() {
        LegacyLineCatalog.LineProfile profile = LegacyLineCatalog.findByName(requireContext(), selectedLineName);
        List<String> candidates = profile.stationsForDirection(requireStationState().getDirectionText());
        return candidates.isEmpty() ? java.util.Arrays.asList("火车站", "市政府", "科技园") : candidates;
    }

    private List<String> buildOtherCandidates() {
        LegacyLineCatalog.LineProfile profile = LegacyLineCatalog.findByName(requireContext(), selectedLineName);
        List<String> candidates = profile.remindersForDirection(requireStationState().getDirectionText());
        return candidates.isEmpty() ? java.util.Arrays.asList("到站提醒", "转弯提醒", "限速提醒", "进站提醒") : candidates;
    }

    private StationState requireStationState() {
        TerminalBusinessModule module = ShellRuntime.get().getModuleHub().findModule("station");
        if (module instanceof StationBusinessModule) {
            return ((StationBusinessModule) module).getStationState();
        }
        return new StationState();
    }

    private String requireSection() {
        return requireArguments().getString(ARG_SECTION, SECTION_SITE);
    }

    private String resolveCurrentLineName(StationState stationState) {
        String stationLine = stationState.getLineName();
        if (stationLine != null && !stationLine.trim().isEmpty() && !"-".equals(stationLine.trim())) {
            return LegacyLineCatalog.findByName(requireContext(), stationLine.trim()).getLineName();
        }
        LegacyStationResourceStateRepository.StationResourceState state = LegacyStationResourceStateRepository.getState(requireContext());
        if (state.getLineName() != null && !state.getLineName().trim().isEmpty() && !"-".equals(state.getLineName().trim())) {
            return LegacyLineCatalog.findByName(requireContext(), state.getLineName().trim()).getLineName();
        }
        return LegacyLineCatalog.first(requireContext()).getLineName();
    }

    private void applySelectedLineProfile(String directionText) {
        StationState stationState = requireStationState();
        String resolvedDirection = valueOrDefault(directionText, "上行");
        LegacyLineCatalog.LineProfile profile = LegacyLineCatalog.findByName(requireContext(), selectedLineName);
        stationState.applyLineProfile(profile.getLineName(), resolvedDirection, profile.stationsForDirection(resolvedDirection));
        stationState.setLineAttribute(profile.getLineAttribute());
        LegacyStationResourceStateRepository.updateRouteSelection(
                requireContext(),
                "site-collection",
                profile.getLineName(),
                resolvedDirection,
                profile.getLineAttribute()
        );
    }

    private boolean switchDirectionForLearning() {
        StationState stationState = requireStationState();
        String currentDirection = valueOrDefault(stationState.getDirectionText(), "上行");
        String nextDirection = currentDirection.contains("下") ? "上行" : "下行";
        applySelectedLineProfile(nextDirection);
        View root = getView();
        if (root == null) {
            return true;
        }
        RadioButton rbUpstream = root.findViewById(SECTION_OTHER.equals(requireSection()) ? R.id.rbOtherDirectionUpstream : R.id.rbDirectionUpstream);
        RadioButton rbDownstream = root.findViewById(SECTION_OTHER.equals(requireSection()) ? R.id.rbOtherDirectionDown : R.id.rbDirectionDown);
        if (rbUpstream != null) {
            rbUpstream.setChecked(nextDirection.contains("上"));
        }
        if (rbDownstream != null) {
            rbDownstream.setChecked(nextDirection.contains("下"));
        }
        return true;
    }

    @Nullable
    private LegacySiteCollectionStore.LearnedValue loadLearnedValue(boolean otherSection, @NonNull String itemName) {
        if (getContext() == null) {
            return null;
        }
        String directionText = valueOrDefault(requireStationState().getDirectionText(), "上行");
        LegacySiteCollectionStore.LearnedValue resourceValue = otherSection
            ? LegacySiteCollectionResourceStore.loadReminder(
                requireContext(),
                selectedLineName,
                directionText,
                itemName,
                selectedAttributeIndex
            )
            : LegacySiteCollectionResourceStore.loadSite(
                requireContext(),
                selectedLineName,
                directionText,
                itemName,
                selectedSiteIndex
            );
        LegacySiteCollectionStore.LearnedValue cachedValue = LegacySiteCollectionStore.load(
                requireContext(),
                otherSection ? SECTION_OTHER : SECTION_SITE,
                selectedLineName,
            directionText,
                itemName,
                otherSection ? selectedAttributeIndex : selectedSiteIndex
        );
        if (resourceValue == null) {
            return cachedValue;
        }
        if (cachedValue == null || otherSection) {
            return resourceValue;
        }
        return new LegacySiteCollectionStore.LearnedValue(
            resourceValue.getLongitude(),
            resourceValue.getLatitude(),
            cachedValue.getSpeedKmh(),
            resourceValue.getAngle(),
            resourceValue.getAltitude()
        );
    }

    private boolean hasLearnableGps(@Nullable GpsFixSnapshot snapshot) {
        return snapshot != null
                && snapshot.isValid()
                && !"-".equals(valueOrDefault(snapshot.getLongitudeDecimal(), "-"))
                && !"-".equals(valueOrDefault(snapshot.getLatitudeDecimal(), "-"));
    }

    private String formatLearnedLongitude(@Nullable LegacySiteCollectionStore.LearnedValue learned) {
        return learned == null ? "-" : valueOrDefault(learned.getLongitude(), "-");
    }

    private String formatLearnedLatitude(@Nullable LegacySiteCollectionStore.LearnedValue learned) {
        return learned == null ? "-" : valueOrDefault(learned.getLatitude(), "-");
    }

    private String formatLearnedSpeed(@Nullable LegacySiteCollectionStore.LearnedValue learned) {
        return learned == null ? "-" : valueOrDefault(learned.getSpeedKmh(), "-");
    }

    private String formatLearnedAngle(@Nullable LegacySiteCollectionStore.LearnedValue learned) {
        return learned == null ? "-" : valueOrDefault(learned.getAngle(), "-");
    }

    private String formatLearnedAltitude(@Nullable LegacySiteCollectionStore.LearnedValue learned) {
        return learned == null ? "-" : valueOrDefault(learned.getAltitude(), "-");
    }

    private void bindText(@Nullable TextView textView, @Nullable String value) {
        if (textView == null) {
            return;
        }
        textView.setText(valueOrDefault(value, "-"));
    }

    private String formatLongitude(@Nullable GpsFixSnapshot snapshot) {
        return snapshot == null ? "-" : valueOrDefault(snapshot.getLongitudeDecimal(), "-");
    }

    private String formatLatitude(@Nullable GpsFixSnapshot snapshot) {
        return snapshot == null ? "-" : valueOrDefault(snapshot.getLatitudeDecimal(), "-");
    }

    private String formatSpeed(@Nullable GpsFixSnapshot snapshot) {
        if (snapshot == null || snapshot.getSpeedKnots() == null || snapshot.getSpeedKnots().trim().isEmpty()) {
            return "-";
        }
        try {
            return String.format(Locale.US, "%.2f", Double.parseDouble(snapshot.getSpeedKnots().trim()) * 1.852d);
        } catch (Exception ignored) {
            return "-";
        }
    }

    private String formatAngle(@Nullable GpsFixSnapshot snapshot) {
        return snapshot == null ? "-" : valueOrDefault(snapshot.getCourse(), "-");
    }

    private String formatAltitude(@Nullable GpsFixSnapshot snapshot) {
        return snapshot == null ? "-" : valueOrDefault(snapshot.getAltitudeMeters(), "-");
    }

    private String valueOrDefault(@Nullable String value, @NonNull String fallback) {
        return value == null || value.trim().isEmpty() || "-".equals(value.trim()) ? fallback : value.trim();
    }

    private void toast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private interface SelectionCallback {
        void onSelected(int selectedIndex);
    }
}
