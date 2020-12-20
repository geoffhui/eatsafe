package com.example.eatsafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class RestaurantDetailsActivity extends AppCompatActivity {

    TextView resName;
    TextView resAddress;
    TextView resDate;
    TextView resInspection;
    TextView resGrade;
    TextView resHazard;
    TextView resViolation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String hazardRating = intent.getStringExtra("hazard");
        String date = intent.getStringExtra("date");
        String inspection = intent.getStringExtra("inspection");
        int critical = intent.getIntExtra("critical", 0);
        int nonCritical = intent.getIntExtra("noncritical", 0);
        String violation = intent.getStringExtra("violation").replaceAll("\\[.*?\\],", "\n\n");
        String address = intent.getStringExtra("address");


        resName = findViewById(R.id.name);
        resAddress = findViewById(R.id.address);
        resDate = findViewById(R.id.date);
        resHazard = findViewById(R.id.hazard);
        resViolation = findViewById(R.id.violations);
        resGrade = findViewById(R.id.grade);

        resName.setText(name);
        resHazard.setText("Hazard Rating\n\n"+ hazardRating);
        if (violation.length() > 0){
            resViolation.setText(violation);
        } else {
            resViolation.setText("No Violations");
        }
        System.out.println("Baby Batter " + critical);
        char grade = Grading(critical, nonCritical);
        resAddress.setText(address);
        resDate.setText(date);
        resGrade.setText("Infraction Grade\n\n" + grade);



    }

    protected char Grading(int crit, int nonCrit){
        double totalGrade = crit + (0.5 * nonCrit);
        if (totalGrade < 2){
            return 'A';
        } else if (totalGrade < 4){
            return 'B';
        } else if (totalGrade < 6){
            return 'C';
        } else {
            return 'F';
        }

    }


}
