package com.example.HappyMine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    Button loginBtn, signupBtn;
    EditText pn, pw;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();

        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.lsignupBtn);
        pn = findViewById(R.id.lmember_pn);
        pw = findViewById(R.id.lmember_pw);

        // 로그인 버튼 클릭 이벤트
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_pn = pn.getText().toString().trim();
                String user_pw = pw.getText().toString().trim();

                if (TextUtils.isEmpty(user_pn) || TextUtils.isEmpty(user_pw)) {
                    Toast.makeText(Login.this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 사용자 ID에 @example.com 추가
                String email = user_pn + "@example.com";

                signInWithEmailAndPassword(email, user_pw);
            }
        });

        // 회원가입 버튼 클릭 시 회원가입 창으로 이동
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void signInWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Toast.makeText(Login.this, "로그인 성공" , Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // 로그인 액티비티 종료
                            }
                        } else {
                            // 로그인 실패
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "로그인 실패";
                            Log.e(TAG, "로그인 실패"); // Logcat에 자세한 오류 메시지 출력
                            Toast.makeText(Login.this, "로그인 실패", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
