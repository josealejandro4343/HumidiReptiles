package com.example.humidireptiles;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChosenPetAdapter extends RecyclerView.Adapter{
    private Context context;
    private ArrayList<ImageModel> imageModelArrayList;
    ArrayList<ChosenPet> mylist;

    public ChosenPetAdapter(ArrayList<ChosenPet> mylist) {
        this.mylist=mylist;

    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_image_layout, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        myViewHolder viewHolderClass=(myViewHolder)holder;
        final ChosenPet fetchDatalist=mylist.get(position);
        viewHolderClass.PetType.setText(fetchDatalist.getPetname());
        String pet_name = fetchDatalist.getPetname().toString();
        Glide.with(viewHolderClass.PetImage.getContext()).load(fetchDatalist.getPetImage()).into(viewHolderClass.PetImage);
        viewHolderClass.ideal_enc_temp.setText(fetchDatalist.getRequired_temp().toString());
        viewHolderClass.ideal_water_temp.setText(fetchDatalist.getRequired_watertemp().toString());
        viewHolderClass.ideal_humid.setText(fetchDatalist.getRequired_humidity().toString());
        viewHolderClass.ideal_enc_temp_max.setText(fetchDatalist.getRequired_tempmax().toString());
        viewHolderClass.ideal_water_temp_max.setText(fetchDatalist.getRequired_watertempmax().toString());
        viewHolderClass.ideal_humid_max.setText(fetchDatalist.getRequired_humidity_max().toString());
        viewHolderClass.PetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent=new Intent(view.getContext(),Dashboard.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("key", fetchDatalist);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);*/
                DatabaseReference mydb, dbGetChosenPetImage;

                String petType = fetchDatalist.getPetname();
                String false_str = "false";
                String true_str = "true";

                mydb = FirebaseDatabase.getInstance().getReference().child("ChosenPet");
                mydb.orderByChild("isChosen").equalTo("true").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        HashMap updateFalseData = new HashMap();
                        updateFalseData.put("isChosen", false_str);

                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            String petTypeTrue = snapshot1.child("petname").getValue().toString();

                            mydb.child(petTypeTrue).updateChildren(updateFalseData);
                            //mydb.child(petname).child("isChosen").setValue("true");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dbGetChosenPetImage = FirebaseDatabase.getInstance().getReference().child("ChosenPet");

                dbGetChosenPetImage.orderByChild("petname").equalTo(petType).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap updateTrueData = new HashMap();
                        updateTrueData.put("isChosen", true_str);
                                dbGetChosenPetImage.child(petType).updateChildren(updateTrueData);

                            //mydb.child(petname).child("isChosen").setValue("true");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                /*dbGetChosenPetImage.orderByChild("petname").equalTo(valueOf(petType)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap updateTrueData = new HashMap();
                        updateTrueData.put("isChosen", true_str);

                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            dbGetChosenPetImage.child(valueOf(petType)).updateChildren(updateTrueData);
                            //mydb.child(petname).child("isChosen").setValue("true");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });*/
                Intent intent = new Intent (view.getContext(), Dashboard.class);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mylist.size();
    }

    /*@Override
    protected void onBindViewHolder(@NonNull ChosenPetAdapter.myViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ChosenPet model) {
        holder.PetType.setText(model.getPetname());
        holder.PetType.setText(model.getPetname());
        Glide.with(holder.PetImage.getContext()).load(model.getPetImage()).into(holder.PetImage);
        holder.ideal_enc_temp.setText(model.getRequired_temp().toString());
        holder.ideal_water_temp.setText(model.getRequired_watertemp().toString());
        holder.ideal_humid.setText(model.getRequired_humidity().toString());
        holder.ideal_enc_temp_max.setText(model.getRequired_tempmax().toString());
        holder.ideal_water_temp_max.setText(model.getRequired_watertempmax().toString());
        holder.ideal_humid_max.setText(model.getRequired_humidity_max().toString());
        mydb = FirebaseDatabase.getInstance().getReference().child("ChosenPet");

        holder.PetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String petname = holder.PetImage.toString();
                dbGetChosenPetImage = FirebaseDatabase.getInstance().getReference().child("ChosenPet");
                dbGetChosenPetImage.orderByChild("isChosen").equalTo(valueOf(petname)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot snapshot1: snapshot.getChildren()) {
                            dbGetChosenPetImage.child(valueOf
                                    (petname)).child("isChosen").setValue("true");
                            //mydb.child(petname).child("isChosen").setValue("true");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }*/

    class myViewHolder extends RecyclerView.ViewHolder{
        ImageButton PetImage;
        TextView PetType;
        TextView ideal_enc_temp, ideal_water_temp, ideal_humid, ideal_enc_temp_max, ideal_water_temp_max, ideal_humid_max;


        public myViewHolder(@NonNull View itemView){
            super(itemView);
            PetImage = (ImageButton) itemView.findViewById(R.id.PetImage);
            PetType = (TextView) itemView.findViewById(R.id.typeofPet);
            ideal_enc_temp = (TextView) itemView.findViewById(R.id.required_temp_sil);
            ideal_water_temp = (TextView) itemView.findViewById(R.id.required_water_temp_sil);
            ideal_humid = (TextView) itemView.findViewById(R.id.required_humid_sil);
            ideal_enc_temp_max = (TextView) itemView.findViewById(R.id.required_temp_silmax);
            ideal_water_temp_max = (TextView) itemView.findViewById(R.id.required_water_temp_silmax);
            ideal_humid_max = (TextView) itemView.findViewById(R.id.required_humid_silmax);
        }
    }
}
    