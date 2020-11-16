package com.example.logapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private int lastIndex;
    List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initBottomNavigation();
    }

    public void initBottomNavigation() {
        fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new ChartFragment());
        fragments.add(new ForecastFragment());
        fragments.add(new SettingFragment());
        // 初始化展示MessageFragment
        setFragmentPosition(0);

        bottomNavigationView = findViewById(R.id.navigation);
        // 添加监听
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        setFragmentPosition(0);
                        break;
                    case R.id.navigation_chart:
                        setFragmentPosition(1);
                        break;
                    case R.id.navigation_yuce:
                        setFragmentPosition(2);
                        break;
                    case R.id.navigation_setting:
                        setFragmentPosition(3);
                        break;
                    default:
                        break;
                }
                // 这里注意返回true,否则点击失效
                return true;
            }
        });
    }

    private void setFragmentPosition(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = fragments.get(position);
        Fragment lastFragment = fragments.get(lastIndex);
        lastIndex = position;
        ft.hide(lastFragment);
        if (!currentFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            ft.add(R.id.framePage, currentFragment);
        }
        ft.show(currentFragment);
        ft.commitAllowingStateLoss();
    }

}