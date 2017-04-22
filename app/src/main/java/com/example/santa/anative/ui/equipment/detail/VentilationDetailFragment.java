package com.example.santa.anative.ui.equipment.detail;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.santa.anative.R;
import com.example.santa.anative.model.entity.Equipment;
import com.example.santa.anative.widget.adapter.recycler.SettingAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by santa on 31.03.17.
 */

public class VentilationDetailFragment extends Fragment implements EquipmentDetail {

    @BindView(R.id.tv_ventilation_detail_carbon) TextView mTvCarbon;
    @BindView(R.id.tv_ventilation_detail_filter) TextView mTvFilter;
    @BindView(R.id.tv_ventilation_detail_house) TextView mTvHouse;
    @BindView(R.id.tv_ventilation_detail_street) TextView mTvStreet;
    @BindView(R.id.tv_ventilation_detail_pressure) TextView mTvPressure;
    @BindView(R.id.tv_ventilation_detail_humidity) TextView mTvHumidity;

    @BindView(R.id.switch_ventilation_detail_state) SwitchCompat mSwitchState;
    @BindView(R.id.iv_ventilation_detail_state) ImageView mIvState;

    @BindView(R.id.spinner_ventilation_speed) AppCompatSpinner mVentilationSpeed;
    @BindView(R.id.spinner_ventilation_temperature) AppCompatSpinner mVentilationTemperature;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_ventilation, container,  false);
        ButterKnife.bind(this, view);
        initializeSpinners();
        return view;
    }

    private void initializeSpinners() {
        // Speed
        String[] speed = getResources().getStringArray(R.array.fan_array);
        ArrayList<String> weekList = new ArrayList<>(Arrays.asList(speed));

        SettingAdapter weekdaysAdapter = new SettingAdapter(getActivity(), weekList);
        mVentilationSpeed.setAdapter(weekdaysAdapter);

        // Temperature
        ArrayList<String> temperature = new ArrayList<>();
        for (int i = 99; i > 0; i--) {
            temperature.add(String.valueOf(i));
        }
        SettingAdapter pointAdapter = new SettingAdapter(getActivity(), temperature);
        mVentilationTemperature.setAdapter(pointAdapter);
    }

    @Override
    public void onBindData(Equipment equipment) {
        mTvCarbon.setText(equipment.getCarbon());
        mTvFilter.setText(equipment.getFilter());
        mTvHouse.setText(equipment.getTemperature());
        mTvStreet.setText(equipment.getTemperatureStreet());
        mTvPressure.setText(equipment.getPressure());
        mTvHumidity.setText(equipment.getHumidity());

        mVentilationSpeed.setSelection(equipment.getSpeed());
        mVentilationTemperature.setSelection(equipment.getTemperature());

        mSwitchState.setChecked(equipment.isEnable());

        switch (equipment.getState()) {
            case Equipment.WARM:
                mIvState.setImageAlpha(R.drawable.warm);
                break;
            case Equipment.VENTILATION:
                mIvState.setImageAlpha(R.drawable.vent);
                break;
            case Equipment.COLD:
                mIvState.setImageAlpha(R.drawable.cold_21dp);
                break;
            default:
                mIvState.setImageAlpha(R.drawable.empty);
                break;
        }
    }

    @Override
    public void changeStatus() {

    }
}