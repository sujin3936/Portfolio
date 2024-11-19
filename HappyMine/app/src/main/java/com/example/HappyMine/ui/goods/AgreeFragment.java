package com.example.HappyMine.ui.goods;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.HappyMine.R;
import com.example.HappyMine.databinding.FragmentAgreeBinding;

import java.util.Calendar;


public class AgreeFragment extends Fragment {

    TextView todayDate;
    Button agreeBtn;
    private FragmentAgreeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAgreeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        todayDate = binding.todayDate;
        agreeBtn = binding.agreeYesBtn;

        // 현재 날짜 가져오기
        Calendar calendar = Calendar.getInstance();

        // 년, 월, 일 얻기
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 0부터 시작하므로 1 더해줌
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // 현재 날짜로 변경
        String today = year + "-" + month + "-" + day;
        todayDate.setText(today);

        agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_nav_aree_to_nav_goodsBuy);
                //상품 구매에 데이터베이스 저장
            }
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
