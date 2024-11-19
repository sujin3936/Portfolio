package com.example.HappyMine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.HappyMine.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startapp);

        // 3초 후에 login창으로 전환
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, Login.class);
                startActivity(intent);
                overridePendingTransition(0, 0);  // 전환 애니메이션 비활성화
                finish(); // 스플래시 화면을 종료하여 뒤로 가기 버튼을 누르면 메인 화면으로 돌아가지 않도록 합니다.
            }
        }, 3000); // 3000 milliseconds = 3 seconds
    }
}
