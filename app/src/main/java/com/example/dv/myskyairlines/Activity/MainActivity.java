package com.example.dv.myskyairlines.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.dv.myskyairlines.Model.Location;
import com.example.dv.myskyairlines.R;
import com.example.dv.myskyairlines.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private int adultPassengers = 1, childPassengers = 1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM, yyyy", Locale.ENGLISH);
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initLocation();
        initPassengers();
        initClassSeat();
        initDatePickup();
        setVariable();
        

    }

    private void setVariable() {
        binding.searchBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            intent.putExtra("from",((Location)binding.fromSp.getSelectedItem()).getName());
            intent.putExtra("to",((Location)binding.toSp.getSelectedItem()).getName());
            intent.putExtra("date",(binding.departureDateTxt.getText()).toString());
            intent.putExtra("numPassenger",adultPassengers + childPassengers);
            startActivity(intent);
        });
    }

    private void initDatePickup() {
        Calendar calendarToday = Calendar.getInstance();
        String curreDate = dateFormat.format(calendarToday.getTime());
        binding.departureDateTxt.setText(curreDate);

        Calendar calendarTomorrow = Calendar.getInstance();
        calendarTomorrow.add(Calendar.DAY_OF_YEAR, 1);
        String tommorowDate = dateFormat.format(calendarTomorrow.getTime());
        binding.returnDateTxt.setText(tommorowDate);

        binding.departureDateTxt.setOnClickListener(v -> showDatePickerDialog(binding.departureDateTxt));
        binding.returnDateTxt.setOnClickListener(v -> showDatePickerDialog(binding.returnDateTxt));
    }

    private void initClassSeat() {

        ArrayList<String> list = new ArrayList<>();
        list.add("Business Class");
        list.add("Fist Class");
        list.add("Economy Class");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.classSp.setAdapter(adapter);
        binding.progressBarClass.setVisibility(View.GONE);
    }

    private void initPassengers() {
        binding.progressBarClass.setVisibility(View.VISIBLE);
        binding.plusAdultBtn.setOnClickListener(v -> {
            adultPassengers++;
            binding.AdultTxt.setText(adultPassengers + " Adult");
        });

        binding.minusAdultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adultPassengers > 1){
                    adultPassengers--;
                    binding.AdultTxt.setText(adultPassengers + " Adult");
                }
            }
        });

        binding.plusChildBtn.setOnClickListener(v -> {
            childPassengers++;
            binding.ChildTxt.setText(childPassengers + " Child");
        });

        binding.minusChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (childPassengers > 1){
                    childPassengers--;
                    binding.ChildTxt.setText(childPassengers + " Child");
                }
            }
        });
    }

    private void initLocation() {
        binding.progressBarFrom.setVisibility(View.VISIBLE);
        binding.progressBarTo.setVisibility(View.VISIBLE);
        DatabaseReference myRef = database.getReference("Locations");
        ArrayList<Location> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue:snapshot.getChildren()){
                        list.add(issue.getValue(Location.class));
                    }
                    ArrayAdapter<Location> adapter = new ArrayAdapter<>(MainActivity.this,R.layout.sp_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.fromSp.setAdapter(adapter);
                    binding.toSp.setAdapter(adapter);
                    binding.fromSp.setSelection(1);
                    binding.progressBarFrom.setVisibility(View.GONE);
                    binding.progressBarTo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDatePickerDialog(TextView textView){
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,(view, selectdYear, selectdMonth, selectdDay) -> {
                calendar.set(selectdYear, selectdMonth, selectdDay);
                String formattedDate = dateFormat.format(calendar.getTime());
                textView.setText(formattedDate);
                }, year, month, day);
            datePickerDialog.show();
        }
    }
}