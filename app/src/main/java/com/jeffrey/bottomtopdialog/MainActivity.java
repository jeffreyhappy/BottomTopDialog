package com.jeffrey.bottomtopdialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn:
                showBottomDialog();
                break;
            case R.id.btn2:
                showTopDialog(findViewById(R.id.btn2));
                break;
            case R.id.btn3:
                showTopDialog2(findViewById(R.id.btn3));
                break;
        }
    }


    private void showBottomDialog(){
        ShareDialog shareDialog = new ShareDialog();
        shareDialog.overrideAnim(R.anim.fade_in_center,R.anim.fade_out_center);
        shareDialog.show(getSupportFragmentManager(),"shareDialog");
    }

    private void showTopDialog(View view){
        FilterDialog filterDialog = new FilterDialog();
        filterDialog.show(getSupportFragmentManager(),"filterDialog",view);
    }

    private void showTopDialog2(View view){
        FilterDialog filterDialog = new FilterDialog();
        filterDialog.overrideAnim(R.anim.fade_in_center,R.anim.fade_out_center);
        filterDialog.show(getSupportFragmentManager(),"filterDialog",view);
    }
}