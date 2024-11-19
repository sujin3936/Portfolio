package com.example.HappyMine;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.HappyMine.ui.commonFunction.CommonF;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    EditText name, pn, address, password;
    TextView birthdate;
    RadioGroup genderGroup;
    RadioButton man, woman;
    String[] items_gymname;
    Resources res;
    Spinner spinner_gymname;
    Button signupBtn;
    ImageButton calenderBtn, backBtn;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        name = findViewById(R.id.smember_name);
        birthdate = findViewById(R.id.sbirthdate);
        pn = findViewById(R.id.smember_pn);
        address = findViewById(R.id.address);
        genderGroup = findViewById(R.id.gender_group);
        man = findViewById(R.id.gender_man);
        woman = findViewById(R.id.gender_woman);
        password = findViewById(R.id.spassword);
        spinner_gymname = findViewById(R.id.gymSpinner);
        signupBtn = findViewById(R.id.ssingupBtn);
        calenderBtn = findViewById(R.id.calenderBtn);
        backBtn = findViewById(R.id.backBtn);

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        res = getResources();

        // 스피너 안 배열 넣기
        items_gymname = res.getStringArray(R.array.gym_name);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items_gymname);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gymname.setAdapter(adapter1);

        // 캘린더 버튼 클릭 시 캘린더 다이얼로그 표시
        calenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonF.showDatePickerDialog(Signup.this, birthdate, null, 1);
            }
        });

        // 회원가입 버튼 클릭 시 계정 생성 코드
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        //뒤로 가기 버튼 클릭 시 로그인 화면으로 이동
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void createAccount() {
        final String user_name = name.getText().toString().trim();
        final String user_gender;
        final String user_birthdate = birthdate.getText().toString().trim();
        final String user_pn = pn.getText().toString().trim();
        final String user_address = address.getText().toString().trim();
        final String gymName = spinner_gymname.getSelectedItem().toString().trim();
        final String pw = password.getText().toString().trim();
        final String email = pn.getText().toString()+"@example.com";

        // 성별 선택된 값 가져오기
        int selectedId = genderGroup.getCheckedRadioButtonId();
        if (selectedId == man.getId()) {
            user_gender = "남";
        } else if (selectedId == woman.getId()) {
            user_gender = "여";
        } else {
            user_gender = "";
        }

        // 입력 값 검증
        if (TextUtils.isEmpty(user_name) || TextUtils.isEmpty(user_gender) || TextUtils.isEmpty(user_birthdate)
                || TextUtils.isEmpty(user_pn) || TextUtils.isEmpty(user_address) || TextUtils.isEmpty(gymName) || TextUtils.isEmpty(pw)) {
            Toast.makeText(this, "모든 값을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // gymName을 숫자로 매핑
        int gymId = getGymId(gymName);

        // FirebaseAuth를 사용하여 계정 생성
        new Thread(() -> {
            auth.createUserWithEmailAndPassword(user_pn + "@example.com", pw)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            saveUserData(user_name, user_gender, user_birthdate, user_pn, user_address, gymId, email);
                        } else {
                            runOnUiThread(() -> {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(Signup.this, "이미 생성된 계정입니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "계정 생성 실패";
                                    Toast.makeText(Signup.this, "계정 생성 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
        }).start();
    }

    //데이터베이스 저장을 위한 각 문자열 값 int로 변환
    private int getGymId(String gymName) {
        switch (gymName) {
            case "셀프메이드짐 연희점":
                return 1;
            case "스퀘어짐 가좌점":
                return 2;
            case "짐라이트 명지대점":
                return 3;
            case "미녀와 야수짐 연신내점":
                return 4;
            default:
                return 0;
        }
    }

    private void saveUserData(String name, String gender, String birthdate, String phone, String address, int gymId, String email) {
        // Firestore에 저장할 데이터 생성
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("gender", gender);
        user.put("birthdate", birthdate);
        user.put("phone", phone);
        user.put("address", address);
        user.put("gymId", gymId);
        user.put("email", email);

        // Firestore에 데이터 저장
        db.collection("member")
                .document(auth.getCurrentUser().getUid())
                .set(user)
                .addOnSuccessListener(aVoid -> runOnUiThread(() -> {
                    Toast.makeText(Signup.this, "계정 생성 완료.", Toast.LENGTH_SHORT).show();
                    //회원가입 성공시 로그인 액티비티로 화면 전환
                    Intent intent = new Intent(Signup.this, Login.class);
                    startActivity(intent);
                    finish(); // 가입창 종료
                }))
                .addOnFailureListener(e -> runOnUiThread(() -> {
                    String errorMessage = e.getMessage();
                    Toast.makeText(Signup.this, "계정 생성 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
                }));
    }
}
