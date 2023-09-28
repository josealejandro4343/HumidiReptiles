package com.example.humidireptiles;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChooseTypeofPet extends AppCompatActivity {
    private ArrayList<ImageModel> imageModelArrayList;

    ArrayList<ChosenPet> mylist;
    ChosenPetAdapter ChosenPetAdapter;
    RecyclerView newrecyclerView;
    RelativeLayout addOtherpet;
    Button addOtherpetbtn;
    DatabaseReference databaseReference, dbaddtorecord, dbGetChosenPetImage;
    Dialog myDialogforChooseTypeofPet;

    String str_typeofpet, str_pet_specie, str_required_temp_turtle, str_required_temp_turtle_max , str_required_water_temp_turtle , str_required_water_temp_turtle_max , str_required_humid_turtle , str_required_humid_turtle_max, petImageurl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_choose_typeof_pet);
        myDialogforChooseTypeofPet = new Dialog(this);
        addOtherpet = findViewById(R.id.addotherpetrl);
        addOtherpetbtn = findViewById(R.id.addotherpetbtn);
        newrecyclerView = findViewById(R.id.recyclerView_new);
        newrecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mylist =new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference("ChosenPet");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    ChosenPet fetchDatalist = ds.getValue(ChosenPet.class);
                    mylist.add(fetchDatalist);
                }
                ChosenPetAdapter =new ChosenPetAdapter(mylist);
                newrecyclerView.setAdapter(ChosenPetAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void ShowPopupforChangingPet(View v) {
        EditText typeofpet, et_required_temp_turtle, et_required_temp_turtle_max,
                et_required_water_temp_turtle, et_required_water_temp_turtle_max,
                et_required_humid_turtle, et_required_humid_turtle_max, pet_specie_et;
        //ImageView image, image2;
        Button addtorecordbtn;
        TextView textView10;
        myDialogforChooseTypeofPet.setContentView(R.layout.addtypeof_pet);
        typeofpet = (EditText) myDialogforChooseTypeofPet.findViewById(R.id.et_petType);
        pet_specie_et = (EditText) myDialogforChooseTypeofPet.findViewById(R.id.et_petSpecie);
        pet_specie_et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        et_required_temp_turtle = (EditText) myDialogforChooseTypeofPet.findViewById(R.id.et_required_temp_turtle);
        et_required_temp_turtle_max = (EditText) myDialogforChooseTypeofPet.findViewById(R.id.et_required_temp_turtle_max);
        et_required_water_temp_turtle = (EditText) myDialogforChooseTypeofPet.findViewById(R.id.et_required_water_temp_turtle);
        et_required_water_temp_turtle_max = (EditText) myDialogforChooseTypeofPet.findViewById(R.id.et_required_water_temp_turtle_max);
        et_required_humid_turtle = (EditText) myDialogforChooseTypeofPet.findViewById(R.id.et_required_humid_turtle);
        et_required_humid_turtle_max = (EditText) myDialogforChooseTypeofPet.findViewById(R.id.et_required_humid_turtle_max);

        addtorecordbtn = (Button) myDialogforChooseTypeofPet.findViewById(R.id.AddtoRecordButton);
        addtorecordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                str_typeofpet = typeofpet.getText().toString().trim();
                str_pet_specie = pet_specie_et.getText().toString().trim();
                str_required_temp_turtle =  et_required_temp_turtle.getText().toString().trim();
                str_required_temp_turtle_max =  et_required_temp_turtle_max.getText().toString().trim();
                str_required_water_temp_turtle =  et_required_water_temp_turtle.getText().toString().trim();
                str_required_water_temp_turtle_max =  et_required_water_temp_turtle_max.getText().toString().trim();
                str_required_humid_turtle =  et_required_humid_turtle.getText().toString().trim();
                str_required_humid_turtle_max =  et_required_humid_turtle_max.getText().toString().trim();
                petImageurl = "https://firebasestorage.googleapis.com/v0/b/humidireptiles-8fa35.appspot.com/o/icon.png?alt=media&token=0f4cf30e-1a6a-48f4-bc8b-4f8a9318d72d";

                if(str_typeofpet.isEmpty()){
                    typeofpet.setError("Empty field!");
                    typeofpet.requestFocus();
                    return;
                }

                if (str_pet_specie.isEmpty()) {
                    pet_specie_et.setError("Empty field!");
                    pet_specie_et.requestFocus();
                    return;
                }

                if (str_required_temp_turtle.isEmpty()) {
                    et_required_temp_turtle.setError("Empty field!");
                    et_required_temp_turtle.requestFocus();
                    return;
                }

                if (str_required_temp_turtle_max.isEmpty()) {
                    et_required_temp_turtle_max.setError("Empty field!");
                    et_required_temp_turtle_max.requestFocus();
                    return;
                }

                if (str_required_water_temp_turtle.isEmpty()) {
                    et_required_water_temp_turtle.setError("Empty field!");
                    et_required_water_temp_turtle.requestFocus();
                    return;
                }

                if (str_required_water_temp_turtle_max.isEmpty()) {
                    et_required_water_temp_turtle_max.setError("Empty field!");
                    et_required_water_temp_turtle_max.requestFocus();
                    return;
                }

                if (str_required_humid_turtle.isEmpty()) {
                    et_required_humid_turtle.setError("Empty field!");
                    et_required_humid_turtle.requestFocus();
                    return;
                }

                if (str_required_humid_turtle_max.isEmpty()) {
                    et_required_humid_turtle_max.setError("Empty field!");
                    et_required_humid_turtle_max.requestFocus();
                    return;
                }
                else if (!str_typeofpet.isEmpty() && !str_pet_specie.isEmpty() && !str_required_temp_turtle.isEmpty() && !str_required_temp_turtle_max.isEmpty() && !str_required_water_temp_turtle.isEmpty()
                        && !str_required_water_temp_turtle_max.isEmpty() && !str_required_humid_turtle.isEmpty() && !str_required_humid_turtle.isEmpty()) {
                    String petname = str_typeofpet;
                    String petSpecie = str_pet_specie;
                    Integer required_temp = Integer.valueOf(str_required_temp_turtle);
                    Integer required_tempmax = Integer.valueOf(str_required_temp_turtle_max);
                    Integer required_watertemp = Integer.valueOf(str_required_water_temp_turtle);
                    Integer required_watertempmax = Integer.valueOf(str_required_water_temp_turtle_max);
                    Integer required_humidity = Integer.valueOf(str_required_humid_turtle);
                    Integer required_humidity_max = Integer.valueOf(str_required_humid_turtle_max);
                    String petImage = petImageurl.toString();
                    insertdata(petname, petSpecie, required_temp, required_tempmax, required_watertemp, required_watertempmax, required_humidity, required_humidity_max, petImage);
                }
            }
        });

        myDialogforChooseTypeofPet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialogforChooseTypeofPet.show();
    }

    private void insertdata(String petname, String petSpecie, Integer required_temp, Integer required_tempmax, Integer required_watertemp, Integer required_watertempmax, Integer required_humidity, Integer required_humidity_max, String petImage){

        dbaddtorecord = FirebaseDatabase.getInstance().getReference().child("ChosenPet").child(petname);
        dbaddtorecord.child("petname").setValue(petname);
        dbaddtorecord.child("petSpecie").setValue(petSpecie);
        dbaddtorecord.child("isChosen").setValue("false");
        dbaddtorecord.child("petImage").setValue(petImage);
        dbaddtorecord.child("required_temp").setValue(required_temp);
        dbaddtorecord.child("required_tempmax").setValue(required_tempmax);
        dbaddtorecord.child("required_watertemp").setValue(required_watertemp);
        dbaddtorecord.child("required_watertempmax").setValue(required_watertempmax);
        dbaddtorecord.child("required_humidity").setValue(required_humidity);
        dbaddtorecord.child("required_humidity_max").setValue(required_humidity_max);

        Toast.makeText(ChooseTypeofPet.this, "Data Inserted Successfully!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }
    @Override
    protected void onStart() {
        super.onStart();
        addOtherpet.postDelayed(new Runnable() {
            public void run() {
                addOtherpetbtn.setVisibility(View.VISIBLE);
            }
        }, 2000);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}