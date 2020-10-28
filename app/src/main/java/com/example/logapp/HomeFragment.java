package com.example.logapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_home, container, false);
        }

        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar2);
        final TextView textView = (TextView) view.findViewById(R.id.textView2);
        final Button btn = (Button) view.findViewById(R.id.button2);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                textView.setTextSize(progress);
                btn.setText(progress + "px");
            }
        });

        Button btn3 = (Button) view.findViewById(R.id.button3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setTextColor(Color.parseColor("#DC143C"));
            }
        });
        Button btn4 = (Button) view.findViewById(R.id.button4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setTextColor(Color.parseColor("#0000FF"));
            }
        });


        Spinner spinner = (Spinner) view.findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String[] styles = getResources().getStringArray(R.array.font_style);
                switch (styles[pos]){
                    case "常规" :
                        Typeface font1 = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
                        textView.setTypeface(font1);
                        break;
                    case "粗体" :
                        Typeface font2 = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
                        textView.setTypeface(font2);
                        break;
                    case "斜体" :
                        Typeface font4 = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC);
                        textView.setTypeface(font4);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });


        Button btn5 = (Button) view.findViewById(R.id.button5);
        final EditText editText5 = (EditText) view.findViewById(R.id.editText5);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(editText5.getText());
            }
        });

        editText5.setOnTouchListener(new View.OnTouchListener() {
            int count=0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (count== 0){
                    editText5.setText("");
                }
                count++;
                return false;
            }
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) view.getParent()).removeView(view);
    }

}