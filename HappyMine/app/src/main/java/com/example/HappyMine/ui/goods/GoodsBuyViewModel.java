// GoodsBuyViewModel.java
package com.example.HappyMine.ui.goods;

import androidx.lifecycle.ViewModel;

public class GoodsBuyViewModel extends ViewModel {
    private String selectedItem;
    private String startDate;
    private String endDate;

    public String getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(String selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
