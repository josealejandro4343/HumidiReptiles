/*package com.example.humidireptiles;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class popup_window extends AppCompatActivity{
    ImageButton turtle, gecko, frog, peticon;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.popup_window);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("ChosenPet");
        turtle = findViewById(R.id.default_turtle);
        turtle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("Turtle").child("isChosen").setValue("true");
                databaseReference.child("Gecko").child("isChosen").setValue("false");
                databaseReference.child("Frog").child("isChosen").setValue("false");
            }
        });
        gecko = findViewById(R.id.gecko);
        gecko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("Gecko").child("isChosen").setValue("true");
                databaseReference.child("Turtle").child("isChosen").setValue("false");
                databaseReference.child("Frog").child("isChosen").setValue("false");
            }

        });
        frog = findViewById(R.id.frog);
        frog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("Frog").child("isChosen").setValue("true");
                databaseReference.child("Turtle").child("isChosen").setValue("false");
                databaseReference.child("Gecko").child("isChosen").setValue("false");
            }
        });
    }

    /*@Override
    public void onClick(View v) {
        HashMap hashMap = new HashMap();
        hashMap.put("isChosen","true");

        HashMap hashMap1 = new HashMap();
        hashMap1.put("isChosen","false");
        switch (v.getId()) {
            case R.id.default_turtle:
                    databaseReference.child("Turtle").updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            databaseReference.child("Frog").updateChildren(hashMap1);
                            databaseReference.child("Gecko").updateChildren(hashMap1);
                        }
                    });
                break;
            case R.id.gecko:
                databaseReference.child("Gecko").updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        databaseReference.child("Turtle").updateChildren(hashMap1);
                        databaseReference.child("Frog").updateChildren(hashMap1);
                    }
                });
                break;
            case R.id.frog:
                databaseReference.child("Frog").updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        databaseReference.child("Turtle").updateChildren(hashMap1);
                        databaseReference.child("Gecko").updateChildren(hashMap1);
                    }
                });
                break;
        }
}*/