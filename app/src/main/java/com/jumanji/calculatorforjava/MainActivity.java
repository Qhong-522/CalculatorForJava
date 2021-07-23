package com.jumanji.calculatorforjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.jumanji.calculatorforjava.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ViewModelForMonitor viewModelForMonitor;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModelForMonitor = new ViewModelProvider(this).get(ViewModelForMonitor.class);
        binding.setMonitor(viewModelForMonitor);
        binding.setLifecycleOwner(this);
    }
}