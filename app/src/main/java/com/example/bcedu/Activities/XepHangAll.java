package com.example.bcedu.Activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.bcedu.Fragment.ChallengerFragment;
import com.example.bcedu.Fragment.GrandMasterFragment;
import com.example.bcedu.Fragment.MasterFragment;
import com.example.bcedu.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class XepHangAll extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private ImageButton back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();


        bottomNavigationView = findViewById(R.id.bottomNaviagtion);
        frameLayout = findViewById(R.id.frame_layout);

        back = findViewById(R.id.backBXH);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.bn_thachdau) {
                    loadFragment(new ChallengerFragment(), false);
                    return true;
                } else if (itemId == R.id.bn_daicaothu) {
                    loadFragment(new GrandMasterFragment(), false);
                    return true;
                } else if (itemId == R.id.bn_caothu) {
                    loadFragment(new MasterFragment(), false);
                    return true;
                }
                return false;
            }
        });


        loadFragment(new ChallengerFragment(), true);
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frame_layout, fragment);
        } else
            fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_xep_hang_all);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}