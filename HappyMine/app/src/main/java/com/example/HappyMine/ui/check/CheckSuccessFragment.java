package com.example.HappyMine.ui.check;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.HappyMine.databinding.FragmentCheckSuccessBinding;
import com.example.HappyMine.ui.commonFunction.CommonF;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CheckSuccessFragment extends Fragment {
    private FragmentCheckSuccessBinding binding;
    TextView member_name, user_product, user_endDate, user_productDay;
    CommonF g;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // 새로운 XML 파일에 대한 바인딩 클래스 사용
        binding = FragmentCheckSuccessBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        member_name = binding.mmemberName;
        user_product = binding.showProduct;
        user_endDate = binding.showEndDay;
        user_productDay = binding.showEndDate;
        g = new CommonF();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //사용자 이름 가져오기
        g.fetchUserName(getContext(), member_name, 1);

        //해당 회원이 구매한 회원권 정보 불러오기
        fetchUserProductInfo();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchUserProductInfo() {
        //현재 로그인 된 회원의 UID 가져오기
        String uid = auth.getCurrentUser().getUid();

        //productBuy 컬렉션에서 사용자 UI로 된 문서 찾기
        db.collection("productBuy").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //찾았다면 productID와 endDate 필드 값 가져오기
                            Object productID = document.get("productID");
                            Object endDate = document.get("endDate");

                            //필드 값 가져왔다면 각 textview text값 설정, 가져오기에 실패했다면 각 문자열로 text값 설정
                            user_product.setText(productID != null ? productID.toString() + " 개월" : "회원권 정보가 없습니다");
                            user_productDay.setText(endDate != null ? endDate.toString() : "회원권 종료일자가 없습니다");

                            //endDate가 null값이 아니라면 종료날짜-현재날짜 계산해서 남은 일수 표시
                            if (endDate != null) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy / MM / dd", Locale.getDefault());
                                    Date endDateParsed = sdf.parse(endDate.toString());
                                    Date today = new Date();
                                    if (endDateParsed != null) {
                                        long diffInMillies = endDateParsed.getTime() - today.getTime();
                                        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                                        user_endDate.setText(diffInDays + " 일 남음");
                                    } else {
                                        user_endDate.setText("종료 날짜 변환 실패");
                                    }
                                } catch (ParseException e) {
                                    user_endDate.setText("날짜 변환 중 오류 발생");
                                    e.printStackTrace();
                                }
                            } else {
                                user_endDate.setText("endDate가 null");
                            }
                        } else {
                            user_product.setText("회원의 이용권 데이터가 존재하지 않음");
                            user_productDay.setText("");
                            user_endDate.setText("");
                        }
                    } else {
                        user_product.setText("Task 실패");
                        user_productDay.setText("");
                        user_endDate.setText("");
                    }
                });
    }
}
