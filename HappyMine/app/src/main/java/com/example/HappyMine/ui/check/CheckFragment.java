package com.example.HappyMine.ui.check;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.HappyMine.R;
import com.example.HappyMine.databinding.FragmentCheckBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CheckFragment extends Fragment {
    private static final String TAG = "CheckFragment";
    private FragmentCheckBinding binding;
    ImageButton n0, n1, n2, n3, n4, n5, n6, n7, n8, n9, check, delete;
    TextView pn;
    String[] check_pn = new String[8];  // 전화번호를 저장할 배열
    int currentIndex = 0;  // 현재 배열 인덱스를 추적하기 위한 변수
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCheckBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 뷰 바인딩을 사용하여 XML 뷰에 접근
        n0 = binding.number0;
        n1 = binding.number1;
        n2 = binding.number2;
        n3 = binding.number3;
        n4 = binding.number4;
        n5 = binding.number5;
        n6 = binding.number6;
        n7 = binding.number7;
        n8 = binding.number8;
        n9 = binding.number9;
        check = binding.check;
        delete = binding.delete;
        pn = binding.mmemberPn;

        // 버튼 클릭 리스너 설정
        n0.setOnClickListener(v -> onNumberButtonClick(0));
        n1.setOnClickListener(v -> onNumberButtonClick(1));
        n2.setOnClickListener(v -> onNumberButtonClick(2));
        n3.setOnClickListener(v -> onNumberButtonClick(3));
        n4.setOnClickListener(v -> onNumberButtonClick(4));
        n5.setOnClickListener(v -> onNumberButtonClick(5));
        n6.setOnClickListener(v -> onNumberButtonClick(6));
        n7.setOnClickListener(v -> onNumberButtonClick(7));
        n8.setOnClickListener(v -> onNumberButtonClick(8));
        n9.setOnClickListener(v -> onNumberButtonClick(9));
        check.setOnClickListener(this::onCheckButtonClick);
        delete.setOnClickListener(v -> onDeleteButtonClick());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //숫자 버튼 클릭 시 이벤트
    private void onNumberButtonClick(int number) {
        // 번호 버튼 클릭 시 pn 텍스트 수정
        //현재 입력된 숫자가 8자리가 아니라면 배열에 추가 후 pn 수정
        if (currentIndex < 8) {
            check_pn[currentIndex] = String.valueOf(number);
            currentIndex++;
            updateTextView();
        } else {
            //8자리가 입력이 되어 있는 상태에서 숫자 버튼을 누른다면
            Toast.makeText(getActivity(), "010을 제외한 8자리만 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    //pn textview 텍스트 업데이트
    private void updateTextView() {
        StringBuilder sb = new StringBuilder();
        for (String num : check_pn) {
            if (num != null) {
                sb.append(num);
            }
        }
        pn.setText(sb.toString());
    }

    private void onCheckButtonClick(View v) {
        // 1. 입력된 전화번호가 8자리인지 확인
        String inputPhoneNumber = pn.getText().toString().trim();

        if (TextUtils.isEmpty(inputPhoneNumber) || inputPhoneNumber.length() != 8) {
            Toast.makeText(getActivity(), "8자리 전화번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. 현재 로그인 된 전화번호와 일치하는 지 확인
        checkPhoneNumber(v, "010" + inputPhoneNumber);
    }

    private void onDeleteButtonClick() {
        // 삭제 버튼 클릭 시 수행할 작업
        if (currentIndex > 0) {
            currentIndex--;
            check_pn[currentIndex] = null;
            updateTextView();
        }
    }

    private void checkPhoneNumber(View v, String inputPhoneNumber) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null && email.endsWith("@example.com")) {
                String storedPhoneNumber = email.replace("@example.com", "");
                if (storedPhoneNumber.endsWith(inputPhoneNumber)) {
                    //3. 구매한 회원권이 있는 지 확인
                    checkIfMembershipExists(v);
                } else {
                    Toast.makeText(getActivity(), "전화번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "전화번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "로그인된 사용자가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkIfMembershipExists(View v) {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("productBuy")
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // 4. 구매한 회원권의 기간 안에 오늘 날짜가 포함되어 있는 지 확인
                            checkMembershipValidity(document, v);
                        } else {
                            Toast.makeText(getActivity(), "구매한 회원권이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "회원권 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkMembershipValidity(DocumentSnapshot document, View v) {
        String startDateStr = document.getString("startDate");
        String endDateStr = document.getString("endDate");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy / MM / dd", Locale.getDefault());

        try {
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);
            Date today = new Date();

            if (startDate != null && endDate != null && !today.before(startDate) && !today.after(endDate)) {
                //5. 회원권이 유효한 경우
                saveCheckInfo(v);
            } else {
                Toast.makeText(getActivity(), "회원권이 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            Log.e(TAG, "날짜 형식이 잘못되었습니다.", e);
            Toast.makeText(getActivity(), "날짜 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCheckInfo(View v) {
        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference checkRef = db.collection("check").document(uid);

        checkRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // 문서가 존재하면 배열에 새 입장 기록 추가
                    checkRef.update("입장 기록", FieldValue.arrayUnion(new Date()))
                            .addOnSuccessListener(aVoid -> {
                                // 입장 체크 성공 시 메시지 및 화면 이동
                                Toast.makeText(getActivity(), "입장 체크 성공", Toast.LENGTH_SHORT).show();
                                navigateToSuccessFragment(v);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "입장 체크 실패", e);
                                Toast.makeText(getActivity(), "입장 체크 실패", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // 문서가 존재하지 않으면 새 문서를 생성하고 배열에 기록 추가
                    Map<String, Object> checkInfo = new HashMap<>();
                    checkInfo.put("입장 기록", new ArrayList<>(Arrays.asList(new Date())));

                    checkRef.set(checkInfo)
                            .addOnSuccessListener(aVoid -> {
                                // 입장 체크 성공 시 메시지 및 화면 이동
                                Toast.makeText(getActivity(), "입장 체크 성공", Toast.LENGTH_SHORT).show();
                                navigateToSuccessFragment(v);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "입장 체크 실패", e);
                                Toast.makeText(getActivity(), "입장 체크 실패", Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Log.e(TAG, "입장 체크 실패", task.getException());
                Toast.makeText(getActivity(), "입장 체크 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void navigateToSuccessFragment(View v) {
        try {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_nav_memberCheck_to_nav_memberCheckSuccess);
        } catch (Exception e) {
            Log.e(TAG, "화면 전환 중 오류 발생", e);
            Toast.makeText(getActivity(), "화면 전환 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
