package com.example.HappyMine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.HappyMine.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DrawerLayout drawer;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding을 사용하여 레이아웃을 설정합니다.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // FirebaseAuth 인스턴스 초기화
        auth = FirebaseAuth.getInstance();

        // DrawerLayout을 참조합니다.
        drawer = binding.drawerLayout;

        // menubar 이미지 버튼을 참조합니다.
        ImageButton menubarButton = findViewById(R.id.menubar);
        ImageButton menubarButton2 = findViewById(R.id.menubar2);

        // 각 프래그먼트로 이동할 수 있도록 리니어레이아웃 가져오기
        LinearLayout checkBtn = findViewById(R.id.member_checkBtn);
        LinearLayout goodsBtn = findViewById(R.id.member_goodsbuy);
        LinearLayout logout = findViewById(R.id.logout);

        // menubar 버튼에 클릭 리스너를 설정합니다.
        menubarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // menu_layout의 가시성을 토글합니다.
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    // 이미 열려있다면 닫습니다.
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    // 닫혀있다면 엽니다.
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });

        // menubar2 버튼에 클릭 리스너를 설정합니다.
        menubarButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미 열려있다면 닫습니다.
                drawer.closeDrawer(GravityCompat.END);
            }
        });

        // 네비게이션 호스트 프래그먼트를 가져옵니다.
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // 입장체크 리니어 레이아웃 클릭 시 해당 프래그먼트로 이동
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_memberCheck);
                drawer.closeDrawer(GravityCompat.END); // 메뉴바 닫기
            }
        });

        // 이용권 구매 리니어 레이아웃 클릭 시 해당 프래그먼트로 이동
        goodsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_goodsBuy);
                drawer.closeDrawer(GravityCompat.END); // 메뉴바 닫기
            }
        });

        //로그아웃
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish(); // 로그인 액티비티 종료
            }
        });
    }
}
