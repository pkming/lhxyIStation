package com.lhxy.istationdevice.android11.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 通用旧布局宿主 Fragment。
 */
public final class LegacyLayoutFragment extends Fragment {
    private static final String ARG_LAYOUT_RES = "layout_res";

    public static LegacyLayoutFragment newInstance(@LayoutRes int layoutResId) {
        LegacyLayoutFragment fragment = new LegacyLayoutFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES, layoutResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        int layoutRes = args == null ? 0 : args.getInt(ARG_LAYOUT_RES, 0);
        if (layoutRes == 0) {
            return new View(requireContext());
        }
        return inflater.inflate(layoutRes, container, false);
    }
}
