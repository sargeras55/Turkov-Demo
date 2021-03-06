package com.example.santa.anative.ui.equipment.addition;

import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.santa.anative.R;
import com.example.santa.anative.model.entity.Equipment;
import com.example.santa.anative.util.common.ExtraKey;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdditionEquipmentActivity extends AppCompatActivity implements AdditionView {


    @BindView(R.id.toolbar_title) TextView mTitleAdditionEquipment;
    @BindView(R.id.spinner_additional_name) AppCompatSpinner mSpinnerName;
    @BindView(R.id.et_equipment_pin_code) TextView mEtPinCode;
    @BindView(R.id.et_equipment_serial) TextView mEtSerialNumber;
    @BindView(R.id.toolbar_addition_equipment) Toolbar mToolbarAdditionEquipment;

    private AdditionEquipmentPresenter mAdditionalPresenter;
    private Equipment mEquipment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_equipment);
        ButterKnife.bind(this);
        initializeToolbar();
        initializePresenter();
    }


    private void initializeToolbar() {
        mTitleAdditionEquipment.setText(R.string.title_addition_equipment);
        setSupportActionBar(mToolbarAdditionEquipment);
        mToolbarAdditionEquipment.inflateMenu(R.menu.edit_equipment);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }


    private void initializePresenter() {
        mAdditionalPresenter = new AdditionEquipmentPresenter(this);
        mAdditionalPresenter.onCreate();
    }


    @OnClick(R.id.btn_create_equipment)
    public void onCreateEquipment() {
        mAdditionalPresenter.onCreateEquipment("SPINNER TEXT", // TODO
                mEtPinCode.getText().toString().getBytes(),
                mEtSerialNumber.getText().toString());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void showMessage(@StringRes int res) {
        Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onCreateSuccess() {
        onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdditionalPresenter.onDestroy();
    }
}
