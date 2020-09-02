package com.example.severapp.ui.food_list;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.severapp.Common.Common;
import com.example.severapp.Model.FoodModel;

import java.util.List;

public class FoodListViewModel extends ViewModel {

    private MutableLiveData<List<FoodModel>> mutableLiveDataFoodList ;



    public FoodListViewModel() {

    }
    public MutableLiveData<List<FoodModel>> getMutableLiveDataFoodList() {
        if(mutableLiveDataFoodList == null)
            mutableLiveDataFoodList = new MutableLiveData<>(  );
        mutableLiveDataFoodList.setValue( Common.categorySelected.getFoods() );
        return mutableLiveDataFoodList;
    }

}