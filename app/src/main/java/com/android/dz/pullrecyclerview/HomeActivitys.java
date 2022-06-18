package com.android.dz.pullrecyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.dz.pullrecyclerview.adapter.HomeAdapters;
import com.android.dz.pullrecyclerview.view.DZStickyNavLayouts;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ClickUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class HomeActivitys extends AppCompatActivity {

    private Button mBtnChange;
    private Button mBtnScale;
    Runnable mHideBtnTask = new Runnable() {
        @Override
        public void run() {
            mBtnScale.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homes);

        DZStickyNavLayouts layout = (DZStickyNavLayouts) findViewById(R.id.head_home_layout);

        layout.setOnStartActivity(() -> ActivityUtils.startActivity(MainActivity.class));

        RecyclerView mHeadRecyclerView = (RecyclerView) findViewById(R.id.head_home_recyclerview);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHeadRecyclerView.setLayoutManager(layoutManager2);
        updateRvList(layout, mHeadRecyclerView);

        mBtnChange = findViewById(R.id.btn_change_data);

        ClickUtils.applySingleDebouncing(mBtnChange, v -> {
            updateRvList(layout, mHeadRecyclerView);
            mBtnScale.setVisibility(View.VISIBLE);
            btnGoneAfter5s();
        });
        mBtnScale = findViewById(R.id.btn_change_scale);
        ClickUtils.applyPressedViewScale(mBtnScale, 0.7f);
        ClickUtils.applyPressedBgAlpha(mBtnScale, 0.7f);
        ClickUtils.applySingleDebouncing(mBtnScale, 500, v -> btnGoneAfter5s());
    }

    private void btnGoneAfter5s() {
        mBtnScale.removeCallbacks(mHideBtnTask);
        mBtnScale.postDelayed(mHideBtnTask, 5000L);
    }

    private void updateRvList(DZStickyNavLayouts layout, RecyclerView mHeadRecyclerView) {
        final List<Integer> list = generator();
        HomeAdapters mHomeAdapter = new HomeAdapters(list);
        mHeadRecyclerView.setAdapter(mHomeAdapter);
        layout.setNeedShowMore(list.size() > 4);
    }

    public List<Integer> generator() {
        List<Integer> ans = new ArrayList<>();
        int count = new Random().nextInt(6) + 1;
        for (int i = 0; i < count; i++) {
            ans.add(i);
        }
        Log.i(TAG, "generator list size: " + ans.size());
        return ans;
    }

    private static final String TAG = "HomeActivitys";
}
