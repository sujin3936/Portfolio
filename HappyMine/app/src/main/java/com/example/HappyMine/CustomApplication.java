package com.example.HappyMine;

import android.app.Application;

public class CustomApplication extends Application {
    private int agreeCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        // 초기화 코드 작성 (필요 시)
    }

    // 전역 변수의 getter와 setter 메서드
    public int getAgreeCount() {
        return agreeCount;
    }

    public void setAgreeCount(int agreeCount) {
        this.agreeCount = agreeCount;
    }
}
