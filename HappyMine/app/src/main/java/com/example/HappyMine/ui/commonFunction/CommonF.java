package com.example.HappyMine.ui.commonFunction;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class CommonF {

    private static final String TAG = "CommonF";
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    String name;

    public interface DateSetListener {
        void onDateSet(String selectedDate);
    }

    //날짜 이미지 버튼 클릭시 데이트피커 표시되는 공통 메소드
    public static void showDatePickerDialog(Context context, final TextView dateTextView, DateSetListener listener, int num) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = year + " / " + (month + 1) + " / " + dayOfMonth;
                        dateTextView.setText(selectedDate);
                        if (listener != null) {
                            listener.onDateSet(selectedDate);
                        }
                    }
                },
                year, month, day);

        //goodsbuyfragment에서 호출된 날짜 데이트 피커는 현재 날짜 이전의 날짜는 선택 불가하게 설정
        if (num == 2) {
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        }

        datePickerDialog.show();
    }

    //로그인 된 사용자 이름 가져오는 공통 메소드
    public void fetchUserName(@NonNull Context context, TextView member_name, int num) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            db.collection("member")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    if(num == 1) {
                                        name = document.getString("name")+"님 안녕하세요";
                                    } else {
                                        name = document.getString("name");
                                    }
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                            member_name.setText(name);
                                    });
                                } else {
                                    Log.d(TAG, "No document found");
                                }
                            }
                        } else {
                            Log.d(TAG, "Fetch failed with ", task.getException());
                            Toast.makeText(context, "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(context, "로그인된 사용자가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
