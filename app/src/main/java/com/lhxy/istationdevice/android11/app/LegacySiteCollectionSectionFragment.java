package com.lhxy.istationdevice.android11.app;

import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

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

import java.util.Arrays;
import java.util.List;

/**
 * 旧版站点学习右侧内容区宿主。
 * <p>
 * 旧项目这里是“线路/方向/站点或属性选择 + GPS 实时采集 + 保存覆盖数据库”的完整流程。
 * 新壳当前先承接旧页面壳、GPS 展示和“推进下一项”入口，
 * 后面再继续补线路数据库、滚轮选择器和真实采集落库流程。
 */
public final class LegacySiteCollectionSectionFragment extends Fragment {
    private static final String ARG_LAYOUT = "layout";
    private static final String ARG_SECTION = "section";

    private static final String SECTION_SITE = "SITE";
    private static final String SECTION_OTHER = "OTHER";

    private static final List<String> SAMPLE_LINES = Arrays.asList("101路", "102路", "K7支线");
    private static final List<String> SAMPLE_ATTRIBUTES = Arrays.asList("到站提醒", "转弯提醒", "限速提醒", "进站提醒");

    private String selectedLineName = "101路";
    private int selectedSiteIndex;
    private int selectedAttributeIndex;
    @Nullable
    private GpsFixSnapshot learnedSnapshot;

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

    private void bindSiteCollection(View view) {
        StationState stationState = requireStationState();
        selectedLineName = valueOrDefault(stationState.getLineName(), SAMPLE_LINES.get(0));

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
            saveButton.setOnClickListener(v -> saveCollection(view, false));
        }
    }

    private void bindOtherCollection(View view) {
        StationState stationState = requireStationState();
        selectedLineName = valueOrDefault(stationState.getLineName(), SAMPLE_LINES.get(0));

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
            saveButton.setOnClickListener(v -> saveCollection(view, true));
        }
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
            // 旧项目这里是滚轮选择器；当前先用循环切换把页面路径和状态联动跑通。
            int index = SAMPLE_LINES.indexOf(selectedLineName);
            int nextIndex = index < 0 ? 0 : (index + 1) % SAMPLE_LINES.size();
            selectedLineName = SAMPLE_LINES.get(nextIndex);
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

    private void bindSitePicker(@Nullable TextView targetView, @Nullable TextView orderView, boolean otherSection) {
        if (targetView == null) {
            return;
        }
        targetView.setOnClickListener(v -> {
            if (otherSection) {
                selectedAttributeIndex = (selectedAttributeIndex + 1) % SAMPLE_ATTRIBUTES.size();
            } else {
                selectedSiteIndex = (selectedSiteIndex + 1) % buildSiteCandidates().size();
            }
            View root = getView();
            if (root == null) {
                return;
            }
            if (otherSection) {
                renderOtherCollection(root, root.findViewById(R.id.tvCollectionOtherLineName), targetView, orderView);
            } else {
                renderSiteCollection(root, root.findViewById(R.id.tvCollectionLineName), targetView, orderView);
            }
        });
    }

    private void renderSiteCollection(
            @NonNull View root,
            @Nullable TextView tvLineName,
            @Nullable TextView tvSiteName,
            @Nullable TextView tvOrderNumber
    ) {
        List<String> candidates = buildSiteCandidates();
        String siteName = candidates.get(Math.min(selectedSiteIndex, candidates.size() - 1));
        bindText(tvLineName, selectedLineName);
        bindText(tvSiteName, siteName);
        bindText(tvOrderNumber, String.valueOf(Math.min(selectedSiteIndex, candidates.size() - 1) + 1));
        renderGpsPanels(
                root,
                learnedSnapshot,
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
        String attribute = SAMPLE_ATTRIBUTES.get(Math.min(selectedAttributeIndex, SAMPLE_ATTRIBUTES.size() - 1));
        bindText(tvLineName, selectedLineName);
        bindText(tvAttributeName, attribute);
        bindText(tvOrderNumber, String.valueOf(Math.min(selectedAttributeIndex, SAMPLE_ATTRIBUTES.size() - 1) + 1));
        renderGpsPanels(
                root,
                learnedSnapshot,
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
            @Nullable GpsFixSnapshot learned,
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
        bindText(root.findViewById(learnedLongitudeId), formatLongitude(learned));
        bindText(root.findViewById(learnedLatitudeId), formatLatitude(learned));
        bindText(root.findViewById(learnedSpeedId), formatSpeed(learned));
        bindText(root.findViewById(learnedAngleId), formatAngle(learned));
        bindText(root.findViewById(learnedAltitudeId), formatAltitude(learned));
        bindText(root.findViewById(currentLongitudeId), formatLongitude(current));
        bindText(root.findViewById(currentLatitudeId), formatLatitude(current));
        bindText(root.findViewById(currentSpeedId), formatSpeed(current));
        bindText(root.findViewById(currentAngleId), formatAngle(current));
        bindText(root.findViewById(currentAltitudeId), formatAltitude(current));
    }

    private void saveCollection(@NonNull View root, boolean otherSection) {
        ShellRuntime runtime = ShellRuntime.get();
        GpsSerialMonitor gpsMonitor = runtime.getGpsSerialMonitor();
        learnedSnapshot = gpsMonitor.getLatestSnapshot();
        // 旧项目这里会把当前 GPS 位置写回站点/属性数据；
        // 当前先把“保存并推进下一项”的页面语义挂到 station 模块动作上。
        ModuleRunResult result = runtime.getModuleHub()
                .runAction("station", "advance_station", TraceIds.next(otherSection ? "legacy-other-collection" : "legacy-site-collection"));
        if (otherSection) {
            selectedAttributeIndex = (selectedAttributeIndex + 1) % SAMPLE_ATTRIBUTES.size();
            renderOtherCollection(
                    root,
                    root.findViewById(R.id.tvCollectionOtherLineName),
                    root.findViewById(R.id.tvCollectionOtherSiteName),
                    root.findViewById(R.id.tvCollectionOtherOrderNumber)
            );
        } else {
            selectedSiteIndex = (selectedSiteIndex + 1) % buildSiteCandidates().size();
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
        toast(result.isSuccess()
                ? getString(R.string.legacy_site_collection_saved)
                : getString(R.string.legacy_site_collection_save_failed));
    }

    private List<String> buildSiteCandidates() {
        StationState stationState = requireStationState();
        return Arrays.asList(
                valueOrDefault(stationState.getCurrentStation(), "火车站"),
                valueOrDefault(stationState.getNextStation(), "市政府"),
                valueOrDefault(stationState.getTerminalStation(), "科技园")
        );
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
        return snapshot == null ? "-" : valueOrDefault(snapshot.getSpeedKnots(), "-");
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
}
