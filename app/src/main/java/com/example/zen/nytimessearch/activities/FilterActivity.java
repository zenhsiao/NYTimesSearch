package com.example.zen.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.zen.nytimessearch.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FilterActivity extends AppCompatActivity {
    EditText etDate;
    Spinner spOrder;
    CheckBox cbArts;
    CheckBox cbFashion;
    CheckBox cbSports;
    Button btSave;
    String category;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        setupViews();
        etDate.setInputType(InputType.TYPE_NULL);//使自動跳出鍵盤不顯示
    }

    private void setupViews() {
        etDate = (EditText) findViewById(R.id.etDate);
        spOrder = (Spinner) findViewById(R.id.spOrder);
        cbArts = (CheckBox) findViewById(R.id.cbArts);
        cbFashion = (CheckBox) findViewById(R.id.cbFashion);
        cbSports = (CheckBox) findViewById(R.id.cbSports);
        btSave = (Button) findViewById(R.id.btSave);

        etDate.setOnClickListener(new EditText.OnClickListener() {
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }


    public void showDatePickerDialog() {
        // 設定初始日期
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // 跳出日期選擇器
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // 實作OnDateSetListener 這個介面的事件, 它提供使用者操控完日期介面後, 所傳回的日期
                        c.set(year,monthOfYear,dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                        String date = (String) format.format(c.getTime());
                        etDate.setText(date);
                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }


//    public String setcategory(){
//        String category = new String("");
//        if (cbArts.isChecked() == true){
//            category = "\""+ cbArts.getText().toString()+"\"";
//        }
//        if (cbFashion.isChecked() == true){
//            category = category + "\""+ cbFashion.getText().toString()+"\"";
//        }
//        if (cbSports.isChecked() == true){
//            category = category + "\""+ cbSports.getText().toString()+"\"";
//        }
//        return category;
//    }

    public void onSubmit(View v) {
        // Prepare data intent
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("date", etDate.getText().toString());
        data.putExtra("order", spOrder.getSelectedItem().toString());

//        setcategory();
        String category = new String("");
        if (cbArts.isChecked() == true){
            category = "\""+ cbArts.getText().toString()+"\"";
        }
        if (cbFashion.isChecked() == true){
            category = category + "\""+ cbFashion.getText().toString()+"\"";
        }
        if (cbSports.isChecked() == true){
            category = category + "\""+ cbSports.getText().toString()+"\"";
        }

        data.putExtra("category", category);
        data.putExtra("code", 200); // ints work too
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }
}

