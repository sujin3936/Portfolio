package com.example.HappyMine.ui.goods;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.HappyMine.CustomApplication;
import com.example.HappyMine.R;
import com.example.HappyMine.databinding.FragmentGoodsbuyBinding;
import com.example.HappyMine.ui.commonFunction.CommonF;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoodsBuyFragment extends Fragment {
    private static final String TAG = "GoodsBuyFragment";
    String[] items_goods;
    Resources res;
    Spinner spinner_goods;
    ImageButton calenderBtn;
    Button agreeBtn, goodsbuytBtn;
    TextView startDate, endDate, member_name, buyPrice;
    private FragmentGoodsbuyBinding binding;
    static int monthsToAdd;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    CommonF c;
    private GoodsBuyViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGoodsbuyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // ViewModel 초기화
        viewModel = new ViewModelProvider(requireActivity()).get(GoodsBuyViewModel.class);

        // xml 연결
        spinner_goods = binding.goodsSpinner;
        calenderBtn = binding.gcalenderBtn1;
        startDate = binding.startDate;
        endDate = binding.endDate;
        agreeBtn = binding.agreeBtn;
        goodsbuytBtn = binding.goodsBuyBtn;
        member_name = binding.bmemberName;
        buyPrice = binding.buyPrice;

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        c = new CommonF();

        res = getResources();

        // 사용자 이름 가져온 후 member_name text 값으로 설정
        c.fetchUserName(getContext(), member_name, 2);

        // 상품 구매 버튼 비활성화, 활성화 부분
        // 전역 변수 사용
        CustomApplication app = (CustomApplication) getActivity().getApplicationContext();
        int agreeCount = app.getAgreeCount();

        // 전역변수가 0일 때만 버튼 비활성화
        // 즉, agreeBtn을 한 번이라도 눌렀으면 상품 구매 버튼 활성화
        if (agreeCount == 0) {
            goodsbuytBtn.setEnabled(false);
            goodsbuytBtn.setClickable(false);

            //날짜 선택 전 이용약관 버튼 비활성화
            agreeBtn.setEnabled(false);
            agreeBtn.setClickable(false);
        } else {
            // 상품 구매 버튼 활성화
            goodsbuytBtn.setEnabled(true);
            goodsbuytBtn.setClickable(true);
        }

        // 스피너 안 배열 넣기
        items_goods = res.getStringArray(R.array.goods);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items_goods);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_goods.setAdapter(adapter1);

        // ViewModel에 저장된 데이터가 있으면 복원
        if (viewModel.getSelectedItem() != null) {
            spinner_goods.post(() -> {
                int spinnerPosition = adapter1.getPosition(viewModel.getSelectedItem());
                spinner_goods.setSelection(spinnerPosition);
            });
        }
        if (viewModel.getStartDate() != null) {
            startDate.setText(viewModel.getStartDate());
        }
        if (viewModel.getEndDate() != null) {
            endDate.setText(viewModel.getEndDate());
        }

        // 스피너 선택 리스너 설정
        spinner_goods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 선택된 아이템 값 얻기
                String selectedItem = parent.getItemAtPosition(position).toString();
                // 선택된 개월 수를 숫자로 환산
                switch (selectedItem) {
                    case "1개월":
                        monthsToAdd = 1;
                        break;
                    case "3개월":
                        monthsToAdd = 3;
                        break;
                    case "6개월":
                        monthsToAdd = 6;
                        break;
                    case "12개월":
                        monthsToAdd = 12;
                        break;
                    default:
                        monthsToAdd = 0;
                        break;
                }
                fetchMemberGymID();
                viewModel.setSelectedItem(selectedItem);  // ViewModel에 저장
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 선택된 값이 없을 때 처리
                monthsToAdd = 0;
            }
        });

        // 캘린더 버튼 클릭 시 캘린더 다이얼로그 표시
        calenderBtn.setOnClickListener(v -> CommonF.showDatePickerDialog(getContext(), startDate, selectedDate -> {
            // 선택된 날짜가 startDate에 설정된 후 이를 가져와 종료 날짜 계산
            calculateEndDate();
            // 이용약관 버튼 활성화
            agreeBtn.setEnabled(true);
            agreeBtn.setClickable(true);
            viewModel.setStartDate(startDate.getText().toString());  // ViewModel에 저장
            viewModel.setEndDate(endDate.getText().toString());  // ViewModel에 저장
        }, 2));

        // 이용약관 버튼 클릭 시 프래그먼트 전환
        agreeBtn.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_nav_goodsBuy_to_nav_agree);

            // 전역 변수 +1 증가시키기
            app.setAgreeCount(app.getAgreeCount() + 1);

            // ViewModel에 값 저장
            viewModel.setStartDate(startDate.getText().toString());
            viewModel.setEndDate(endDate.getText().toString());
        });

        goodsbuytBtn.setOnClickListener(view -> {
            // 데이터베이스 저장
            saveProductData(monthsToAdd, startDate, endDate);
        });

        return root;
    }

    // 종료 날짜 계산
    private void calculateEndDate() {
        String startDateStr = startDate.getText().toString();
        String[] parts = startDateStr.split(" / ");
        int year = Integer.parseInt(parts[0].trim());
        int month = Integer.parseInt(parts[1].trim()) - 1; // 월은 0부터 시작
        int day = Integer.parseInt(parts[2].trim());

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        calendar.add(Calendar.MONTH, monthsToAdd); //선택한 개월 수에 맞게 더함
        calendar.add(Calendar.DAY_OF_MONTH, -1); // 종료 날짜는 하루를 빼야함

        String endDateString = calendar.get(Calendar.YEAR) + " / " + (calendar.get(Calendar.MONTH) + 1) + " / " + calendar.get(Calendar.DAY_OF_MONTH);
        endDate.setText(endDateString);
    }

    private void saveProductData(int product, TextView startDate1, TextView endDate1) {
        // Firestore에 저장할 데이터 생성
        Map<String, Object> user = new HashMap<>();

        String startDate2 = startDate1.getText().toString().trim();
        String endDate2 = endDate1.getText().toString().trim();

        user.put("productID", product);
        user.put("startDate", startDate2);
        user.put("endDate", endDate2);

        // Firestore에 데이터 저장
        db.collection("productBuy")
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 기존에 데이터가 있으면 토스트 메시지 생성
                        Toast.makeText(getContext(), "이미 이용중인 회원권이 존재합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 기존에 데이터가 없으면 새로운 데이터를 추가
                        db.collection("productBuy")
                                .document(auth.getCurrentUser().getUid())
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    // UI 업데이트 코드를 액티비티에서 실행
                                    getActivity().runOnUiThread(() -> {
                                        Toast.makeText(getContext(), "이용권 구매 완료.", Toast.LENGTH_SHORT).show();
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    // UI 업데이트 코드를 액티비티에서 실행
                                    getActivity().runOnUiThread(() -> {
                                        String errorMessage = e.getMessage();
                                        Toast.makeText(getContext(), "데이터 저장 실패", Toast.LENGTH_SHORT).show();
                                    });
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Firestore에서 문서를 읽어오는데 실패한 경우 토스트 메시지 생성
                    Toast.makeText(getContext(), "데이터를 읽어오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    //각 헬스장 월 가격 표시
    private void fetchMemberGymID() {
        //로그인 된 사용자 UID 가져오기
        String uid = auth.getCurrentUser().getUid();

        //member 컬렉션에서 해당 uid로 된 문서 이름 찾기
        db.collection("member").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //문서 안 gymId 필드값 가져오기
                            Object gymIDObject = document.get("gymId");
                            if (gymIDObject instanceof Number) {
                                String gymID = String.valueOf(gymIDObject);
                                //해당 헬스장 id 넘기기
                                fetchProductPrice(gymID);
                            } else {
                                Log.e(TAG, "Field 'gymId' is not a Number: " + gymIDObject);
                                Toast.makeText(getContext(), "Gym ID가 숫자 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "사용자 정보를 읽어오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //해당 헬스장 가격으로 textview 수정하는 메소드
    private void fetchProductPrice(String gymID) {
        Log.d(TAG, "Fetching product prices for gymID: " + gymID);

        //product 컬렉션에서
        db.collection("product").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean found = false;
                        for (DocumentSnapshot document : task.getResult()) {
                            String fitnessCenterID = String.valueOf(document.get("fitnessCenterID"));

                            // 각 문서의 fitnessCenterID와 gymID와 일치한다면 if문 실행
                            if (fitnessCenterID.equals(gymID)) {
                                found = true;
                                Log.d(TAG, "Document ID: " + document.getId() + ", Data: " + document.getData());

                                //일치하는 문서의 price 필드값 선택한 개월 수에 맞게 가져오고 textview 수정
                                List<Long> prices = (List<Long>) document.get("price");
                                if (prices != null) {
                                    switch (monthsToAdd) {
                                        case 1:
                                            buyPrice.setText(prices.get(0) + "원");
                                            break;
                                        case 3:
                                            buyPrice.setText(prices.get(1) + "원");
                                            break;
                                        case 6:
                                            buyPrice.setText(prices.get(2) + "원");
                                            break;
                                        case 12:
                                            buyPrice.setText(prices.get(3) + "원");
                                            break;
                                        default:
                                            buyPrice.setText("가격 정보를 찾을 수 없습니다.");
                                            break;
                                    }
                                } else {
                                    Log.d(TAG, "가격 정보를 찾을 수 없습니다.");
                                    Toast.makeText(getContext(), "가격 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                        }
                        if (!found) {
                            Log.d(TAG, "해당 피트니스 센터의 상품을 찾을 수 없습니다.");
                            Toast.makeText(getContext(), "해당 피트니스 센터의 상품을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "상품 정보를 읽어오는 데 실패했습니다.", task.getException());
                        Toast.makeText(getContext(), "상품 정보를 읽어오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
