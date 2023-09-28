package com.example.humidireptiles;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Dashboard extends AppCompatActivity implements View.OnClickListener, NetworkStateReceiver.NetworkStateReceiverListener {
    Context context;
    private NetworkStateReceiver networkStateReceiver;
    private ImageButton FeedingSched, WirelessSprayer;
    private ArrayList<ImageModel> imageModelArrayList;

    ArrayList<ChosenPet> mylist;
    ChosenPetAdapter chosenPetAdapter;

    RecyclerView newrecyclerView;

    DatabaseReference mydb_for_getting, dbGetChosenPet, mydb_for_baskinglight, mydb_for_baskinglight2, dbSaveReadings, getipadd, mydb_for_baskinglight_getState, mydb_for_servo, mydb_autospray, dbGetChosenPetImage, mydb_for_waterpump;
    private ProgressBar progBarencTemp,progBarWatTemp,progBarHum, progBarLoadImage;
    TextView temp,water_temp,hum, Status, StatusWaterTemp, statusHum;
    ImageButton petbutton, sprayerbutton, PetIcon;

    Button buttonCamSettings;
    Dialog myDialog, dialogForAutoSprayer;
    private static final String TAG = "Dashboard::";

    private HandlerThread stream_thread,flash_thread,rssi_thread;
    private Handler stream_handler,flash_handler,rssi_handler;
    private Button flash_button, connect_button;
    private ImageView monitor, NoInternet;
    private TextView rssi_text, flashstat, ideal_temp, ideal_temp_max,ideal_watertemp, ideal_watertempmax,ideal_hum, ideal_hum_max, waterlevelStatus;
    private EditText ip_text;
    double zero = 0;
    private final int ID_CONNECT = 200;
    private final int ID_FLASH = 201;
    private final int ID_RSSI = 202;
    View v;
    private boolean flash_on_off = false;
    String flash_url;
    URL url;
    String req_temp;
    String pet_specie;
    String ideal_temperature_max;
    String req_watert, req_watertmax;
    String req_humid, req_humid_max;
    Bitmap bitmap;
    HalfGauge IdGauge, IdGauge2, IdGauge3;
    com.ekn.gruzer.gaugelibrary.Range Range_1,Range_2,Range_3,Range_4,
                                      Range_1_wt,Range_2_wt,Range_3_wt,Range_4_wt,
                                      Range_1_hum, Range_2_hum , Range_3_hum, Range_4_hum ;
    int SetGauge;
    double dbl_req_temp;
    double gauge_enc_temp, gauge_enc_wtemp, gauge_enc_humidity;
    double req_tempmin, req_tempminusone, req_tempmax,
            req_wtempmin, req_wtempmax,req_wtempminusone,
            req_hum_min, req_hum_minusone, req_hum_max;
    //public static String req_temp;
    SwitchCompat switchBaskingLight;
    String getPetName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_dashboard);

        Status = (TextView) findViewById(R.id.Status);
        StatusWaterTemp = (TextView) findViewById(R.id.StatusWaterTemp);
        statusHum = (TextView) findViewById(R.id.statusHum);
        waterlevelStatus = (TextView) findViewById(R.id.waterlevelStatus);
        petbutton = findViewById(R.id.petIcon);
        buttonCamSettings = findViewById(R.id.buttonCamSettings);

        NoInternet = findViewById(R.id.noInternet);
        sprayerbutton = findViewById(R.id.WirelessSprayerbtnDash);

        myDialog = new Dialog(this);
        dialogForAutoSprayer = new Dialog(this);

        findViewById(R.id.connect).setOnClickListener(this);
        findViewById(R.id.flash).setOnClickListener(this);

        flash_button = findViewById(R.id.flash);
        connect_button = (Button) findViewById(R.id.connect);
        monitor = findViewById(R.id.monitor);
        rssi_text = findViewById(R.id.rssi);
        ip_text = findViewById(R.id.ip);
        //flashstat = (TextView) findViewById(R.id.FLASHSTAT);

        stream_thread = new HandlerThread("http");
        stream_thread.start();
        stream_handler = new HttpHandler(stream_thread.getLooper());

        flash_thread = new HandlerThread("http");
        flash_thread.start();
        flash_handler = new HttpHandler(flash_thread.getLooper());

        rssi_thread = new HandlerThread("http");
        rssi_thread.start();
        rssi_handler = new HttpHandler(rssi_thread.getLooper());

        temp=(TextView)findViewById(R.id.temp);
        water_temp=(TextView)findViewById(R.id.water_temp);
        hum=(TextView)findViewById(R.id.hum);
        progBarencTemp = (ProgressBar) findViewById(R.id.progressBarencTemp);
        progBarWatTemp = (ProgressBar) findViewById(R.id.progressBarWatTemp);
        progBarHum = (ProgressBar) findViewById(R.id.progressBarHum);

        FeedingSched = (ImageButton) findViewById(R.id.feedingSchedulebtnDash);
        FeedingSched.setOnClickListener(this);

        progBarLoadImage = (ProgressBar) findViewById(R.id.LoadImage);

        ideal_temp=(TextView)findViewById(R.id.ideal_temp);
        ideal_temp_max=(TextView)findViewById(R.id.ideal_temp_max);
        ideal_watertemp=(TextView)findViewById(R.id.ideal_water_temp);
        ideal_watertempmax=(TextView)findViewById(R.id.ideal_water_tempmax);
        ideal_hum=(TextView)findViewById(R.id.ideal_humidity);
        ideal_hum_max=(TextView)findViewById(R.id.ideal_humidity_max);

        IdGauge = findViewById(R.id.IdGauge);
        IdGauge2 = findViewById(R.id.IdGauge2);
        IdGauge3 = findViewById(R.id.IdGaugeHum);

        Range_1 = new com.ekn.gruzer.gaugelibrary.Range();
        Range_2 = new com.ekn.gruzer.gaugelibrary.Range();
        Range_3 = new com.ekn.gruzer.gaugelibrary.Range();
        Range_4 = new com.ekn.gruzer.gaugelibrary.Range();

        Range_1_wt = new com.ekn.gruzer.gaugelibrary.Range();
        Range_2_wt = new com.ekn.gruzer.gaugelibrary.Range();
        Range_3_wt = new com.ekn.gruzer.gaugelibrary.Range();
        Range_4_wt = new com.ekn.gruzer.gaugelibrary.Range();

        Range_1_hum = new com.ekn.gruzer.gaugelibrary.Range();
        Range_2_hum = new com.ekn.gruzer.gaugelibrary.Range();
        Range_3_hum = new com.ekn.gruzer.gaugelibrary.Range();
        Range_4_hum = new com.ekn.gruzer.gaugelibrary.Range();

        startNetworkBroadcastReceiver(this);
    }


    public void ShowPopup(View v) {
        EditText required_temp_turtle, required_temp_turtle_max,
                required_water_temp_turtle, required_water_temp_turtle_max,
                required_humid_turtle, required_humid_turtle_max, pet_specie_et;
        ImageView image, image2;
        Button save, changetypeofpet;
        TextView textView10;
        myDialog.setContentView(R.layout.chooseyourpet);
        image =(ImageView) myDialog.findViewById(R.id.default_turtle);
        image.setBackground(getDrawable(R.drawable.newapplogo));
        image2 =(ImageView) myDialog.findViewById(R.id.default_turtle_top);
        textView10 = (TextView) myDialog.findViewById(R.id.textView10);

        dbGetChosenPetImage = FirebaseDatabase.getInstance().getReference().child("ChosenPet");
        dbGetChosenPetImage.orderByChild("isChosen").equalTo("true").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for(DataSnapshot snapshot1: snapshot.getChildren()) {
                        image2.setBackgroundResource(0);
                        String getImage = (String) snapshot1.child("petImage").getValue();
                        getPetName = (String) snapshot1.child("petname").getValue();
                        //Picasso.get().load(getImage).into(image);
                        image2.setBackgroundResource(0);
                        Picasso.get().load(getImage).into(image);
                        textView10.setText(getPetName);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            pet_specie_et = (EditText) myDialog.findViewById(R.id.petSpecie);
            pet_specie_et.setText(pet_specie);

            pet_specie_et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

            required_temp_turtle = (EditText) myDialog.findViewById(R.id.required_temp_turtle);
            required_temp_turtle.setText(req_temp);

            required_temp_turtle_max = (EditText) myDialog.findViewById(R.id.required_temp_turtle_max);
            required_temp_turtle_max.setText(ideal_temperature_max);

            required_water_temp_turtle = (EditText) myDialog.findViewById(R.id.required_water_temp_turtle);
            required_water_temp_turtle.setText(req_watert);

            required_water_temp_turtle_max = (EditText) myDialog.findViewById(R.id.required_water_temp_turtle_max);
            required_water_temp_turtle_max.setText(req_watertmax);

            required_humid_turtle = (EditText) myDialog.findViewById(R.id.required_humid_turtle);
            required_humid_turtle.setText(req_humid);

            required_humid_turtle_max = (EditText) myDialog.findViewById(R.id.required_humid_turtle_max);
            required_humid_turtle_max.setText(req_humid_max);
            image2.setBackgroundResource(0);
            changetypeofpet = (Button) myDialog.findViewById(R.id.ChangeTypeofPet);
            changetypeofpet.setOnClickListener(new View.OnClickListener()
            {
            @Override
            public void onClick(View v){
                openChooseTypeofPet();
            }
            });
            save = (Button) myDialog.findViewById(R.id.SaveButton);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String specie = pet_specie_et.getText().toString().trim();
                    String tempmin = required_temp_turtle.getText().toString().trim();
                    String tempmax = required_temp_turtle_max.getText().toString().trim();

                    String water_tempmin = required_water_temp_turtle.getText().toString().trim();
                    String water_tempmax = required_water_temp_turtle_max.getText().toString().trim();

                    String hum_min = required_humid_turtle.getText().toString().trim();
                    String hum_max = required_humid_turtle_max.getText().toString().trim();

                    if(specie.isEmpty()){
                        pet_specie_et.setError("Empty field!");
                        pet_specie_et.requestFocus();
                        return;
                    }

                    if (tempmin.isEmpty()) {
                        required_temp_turtle.setError("Empty field!");
                        required_temp_turtle.requestFocus();
                        return;
                    }

                    if (tempmax.isEmpty()) {
                        required_temp_turtle_max.setError("Empty field!");
                        required_temp_turtle_max.requestFocus();
                        return;
                    }

                    if (water_tempmin.isEmpty()) {
                        required_water_temp_turtle.setError("Empty field!");
                        required_water_temp_turtle.requestFocus();
                        return;
                    }

                    if (water_tempmax.isEmpty()) {
                        required_water_temp_turtle_max.setError("Empty field!");
                        required_water_temp_turtle_max.requestFocus();
                        return;
                    }

                    if (hum_min.isEmpty()) {
                        required_humid_turtle.setError("Empty field!");
                        required_humid_turtle.requestFocus();
                        return;
                    }

                    if (hum_max.isEmpty()) {
                        required_humid_turtle_max.setError("Empty field!");
                        required_humid_turtle_max.requestFocus();
                        return;
                    }
                    else if (!specie.isEmpty() && !tempmin.isEmpty() && !tempmax.isEmpty() && !water_tempmin.isEmpty()
                             && !water_tempmax.isEmpty() && !hum_min.isEmpty() && !hum_max.isEmpty()){
                        String  str_specie = specie;
                        Integer int_tempmin = Integer.parseInt(tempmin);
                        Integer int_tempmax = Integer.parseInt(tempmax);
                        Integer int_water_tempmin = Integer.parseInt(water_tempmin);
                        Integer int_water_tempmax = Integer.parseInt(water_tempmax);
                        Integer int_hum_min = Integer.parseInt(hum_min);
                        Integer int_hum_max = Integer.parseInt(hum_max);

                        updateData(str_specie, int_tempmin, int_tempmax, int_water_tempmin, int_water_tempmax, int_hum_min, int_hum_max);
                        }
                    }
            });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void updateData(String str_specie, Integer int_tempmin, Integer int_tempmax, Integer int_water_tempmin, Integer int_water_tempmax, Integer int_hum_min, Integer int_hum_max) {

        HashMap updateData = new HashMap();
        updateData.put("petSpecie", str_specie);
        updateData.put("required_temp", int_tempmin);
        updateData.put("required_tempmax", int_tempmax);
        updateData.put("required_watertemp", int_water_tempmin);
        updateData.put("required_watertempmax", int_water_tempmax);
        updateData.put("required_humidity", int_hum_min);
        updateData.put("required_humidity_max", int_hum_max);

        dbSaveReadings = FirebaseDatabase.getInstance().getReference("ChosenPet");
        dbSaveReadings.child(getPetName).updateChildren(updateData).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(Dashboard.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
                    myDialog.dismiss();
                }else {
                    Toast.makeText(Dashboard.this, "Failed to Update", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /*
    @Override
    protected void onStart() {
        super.onStart();
        chosenPetAdapter.startListening();
    }

    @Override
    protected void onStop() {void
        super.onStop();
        chosenPetAdapter.stopListening();
    }*/
    public void opencamerasettings(View view) {
        String url = "http://" + ip_text.getText().toString();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
    public void ShowPopupAutoSprayer(View view) {
        Button sprayerbutton, waterpumpbutton;

        dialogForAutoSprayer.setContentView(R.layout.wirelessautosprayer);
        sprayerbutton = (Button) dialogForAutoSprayer.findViewById(R.id.sprayerbutton);
        waterpumpbutton = (Button) dialogForAutoSprayer.findViewById(R.id.waterpumpbutton);
        switchBaskingLight = (SwitchCompat) dialogForAutoSprayer.findViewById(R.id.switchBaskingLight);

        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        switchBaskingLight.setChecked(sharedPreferences.getBoolean("value", true));


        mydb_for_servo = FirebaseDatabase.getInstance().getReference().child("Servo");
        mydb_for_waterpump = FirebaseDatabase.getInstance().getReference().child("WaterPump");
        mydb_for_baskinglight = FirebaseDatabase.getInstance().getReference().child("BaskingLight");
        sprayerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydb_for_servo.child("servo1").setValue(1);
            }
        });
        waterpumpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydb_for_waterpump.child("wpumpState").setValue(1);
            }
        });

        mydb_for_baskinglight_getState= FirebaseDatabase.getInstance().getReference().child("BaskingLight");

        try {
            mydb_for_baskinglight_getState.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String baskingStatestr = snapshot.child("baskingState").getValue().toString();

                    if (baskingStatestr == "1"){
                        //switchBaskingLight.setChecked(true);
                    }
                    else{
                        //switchBaskingLight.setChecked(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){

        }

        switchBaskingLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switchBaskingLight.isChecked()){
                    mydb_for_baskinglight.child("baskingState").setValue(1);
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", true);
                    editor.apply();
                    switchBaskingLight.setChecked(true);
                }
                else{
                    mydb_for_baskinglight.child("baskingState").setValue(0);
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.apply();
                    switchBaskingLight.setChecked(false);
                }
            }
        });
        dialogForAutoSprayer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogForAutoSprayer.show();
    }

    public void openChooseTypeofPet(){
        Intent intent = new Intent(this, ChooseTypeofPet.class);
        startActivity(intent);
    }

    public void autoSprayerOn(){
        mydb_autospray = FirebaseDatabase.getInstance().getReference().child("Servo");
        mydb_autospray.child("servo1").setValue(1);
    }

    public void autoSprayerOff(){
        mydb_autospray = FirebaseDatabase.getInstance().getReference().child("Servo");
        mydb_autospray.child("servo1").setValue(0);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feedingSchedulebtnDash:
                Intent refresh = new Intent(Dashboard.this, FeedingSchedule.class);
                startActivity(refresh);
                finish();
                break;
            case R.id.connect:
                stream_handler.sendEmptyMessage(ID_CONNECT);
                rssi_handler.sendEmptyMessage(ID_RSSI);
                //flash_button.isClickable();
                break;
            case R.id.flash:
                flash_handler.sendEmptyMessage(ID_FLASH);
                //flash_button.setText(ID_FLASH);
                //flashstat.setText(flash_url.toString());
                /**if(ip_text.getText().equals("")){
                }
                else{
                    if(flash_url == "http://" + ip_text.getText() + ":80/led?var=flash&val=1"){
                        flash_button.setText("Flash ON");
                    }
                    if(flash_url == "http://" + ip_text.getText() + ":80/led?var=flash&val=0"){
                        flash_button.setText("Flash OFF");
                    }
                }**/
                break;
            /**case R.id.WirelessSprayer:
             startActivity(new Intent(this, WirelessSprayer.class));
             break;**/
        }
    }

    public void BaskingLightOn(){
        //switchBaskingLight = (SwitchCompat) dialogForAutoSprayer.findViewById(R.id.switchBaskingLight);
        mydb_for_baskinglight2 = FirebaseDatabase.getInstance().getReference().child("BaskingLight");

        mydb_for_baskinglight2.child("baskingState").setValue(1);
        /*SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
        editor.putBoolean("value", true);
        editor.apply();
        switchBaskingLight.setChecked(true);*/
    }

    public void BaskingLightOff(){
        //switchBaskingLight = (SwitchCompat) dialogForAutoSprayer.findViewById(R.id.switchBaskingLight);
        mydb_for_baskinglight2 = FirebaseDatabase.getInstance().getReference().child("BaskingLight");

        mydb_for_baskinglight2.child("baskingState").setValue(0);
        /*SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
        editor.putBoolean("value", false);
        editor.apply();
        switchBaskingLight.setChecked(false);*/
    }


    private class HttpHandler extends Handler
    {
        public HttpHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case ID_CONNECT:
                    VideoStream();
                    break;
                case ID_FLASH:
                    SetFlash();
                    break;
                case ID_RSSI:
                    GetRSSI();
                    break;
                default:
                    break;
            }
        }
    }

    private void SetFlash()
    {
        flash_on_off ^= true;

        if(flash_on_off){
            flash_url = "http://" + ip_text.getText() + ":80/led?var=flash&val=1";
        }
        else {
            flash_url = "http://" + ip_text.getText() + ":80/led?var=flash&val=0";
        }

        try
        {

            url = new URL(flash_url);

            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");
            huc.setConnectTimeout(1000 * 5);
            huc.setReadTimeout(1000 * 5);
            huc.setDoInput(true);
            huc.connect();
            if (huc.getResponseCode() == 200)
            {
                InputStream in = huc.getInputStream();

                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void GetRSSI() {
        rssi_handler.sendEmptyMessageDelayed(ID_RSSI,500);

        String rssi_url = "http://" + ip_text.getText() + "RSSI";

        try {
            URL url = new URL(rssi_url);

            try {

                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setRequestMethod("GET");
                huc.setConnectTimeout(1000 * 5);
                huc.setReadTimeout(1000 * 5);
                huc.setDoInput(true);
                huc.connect();
                if (huc.getResponseCode() == 200) {
                    InputStream in = huc.getInputStream();

                    InputStreamReader isr = new InputStreamReader(in);
                    BufferedReader br = new BufferedReader(isr);
                    final String data = br.readLine();
                    if (!data.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rssi_text.setText(data);
                            }
                        });
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void VideoStream()
    {
        String stream_url = "http://" + ip_text.getText() + ":81/stream";

        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        try
        {

            URL url = new URL(stream_url);

            try
            {
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setRequestMethod("GET");
                huc.setConnectTimeout(1000 * 5);
                huc.setReadTimeout(1000 * 5);
                huc.setDoInput(true);
                huc.connect();

                if (huc.getResponseCode() == 200)
                {
                    InputStream in = huc.getInputStream();

                    InputStreamReader isr = new InputStreamReader(in);
                    BufferedReader br = new BufferedReader(isr);

                    String data;

                    int len;
                    byte[] buffer;

                    while ((data = br.readLine()) != null)
                    {
                        if (data.contains("Content-Type:"))
                        {
                            data = br.readLine();

                            len = Integer.parseInt(data.split(":")[1].trim());

                            bis = new BufferedInputStream(in);
                            buffer = new byte[len];

                            int t = 0;
                            while (t < len)
                            {
                                t += bis.read(buffer, t, len - t);
                            }

                            Bytes2ImageFile(buffer, getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/0A.jpg");

                            bitmap = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/0A.jpg");

                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    monitor.setImageBitmap(bitmap);
                                    hideEditTextandConnectButton();
                                    flash_button.setEnabled(true);
                                }

                            });
                        }
                    }
                }

            } catch (IOException e)
            {
                e.printStackTrace();
            }

        } catch (MalformedURLException e)
        {
            e.printStackTrace();

        } finally
        {
            try
            {
                if (bis != null)
                {
                    bis.close();
                }
                if (fos != null)
                {
                    fos.close();
                }

                stream_handler.sendEmptyMessageDelayed(ID_CONNECT,3000);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void Bytes2ImageFile(byte[] bytes, String fileName)
    {
        try
        {
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes, 0, bytes.length);
            fos.flush();
            fos.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void hideEditTextandConnectButton(){
        connect_button.setVisibility(View.GONE);
        ip_text.setVisibility(View.GONE);
    }

    /*@Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
        //monitor.setImageBitmap(bitmap);
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        //monitor.setImageBitmap(bitmap);
    }*/

    @Override
    protected void onPause() {
        unregisterNetworkBroadcastReceiver(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerNetworkBroadcastReceiver(this);
        super.onResume();
    }

    @Override
    public void networkAvailable() {
        petbutton.setClickable(true);
        petbutton.setVisibility(View.VISIBLE);
        NoInternet.setVisibility(View.GONE);
        Toast.makeText(Dashboard.this, "Online", Toast.LENGTH_SHORT).show();
        temp.setText("");
        water_temp.setText("");
        hum.setText("");
        hum.setTextColor(Color.parseColor("#3A9262"));
        temp.setTextColor(Color.parseColor("#3A9262"));
        water_temp.setTextColor(Color.parseColor("#3A9262"));

        getipadd= FirebaseDatabase.getInstance().getReference().child("IPAdress");

        try {
            getipadd.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String myip = snapshot.child("my_ipadd").getValue().toString();
                    ip_text.setText(myip);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){

        }


        dbGetChosenPet = FirebaseDatabase.getInstance().getReference().child("ChosenPet");
        try {
            dbGetChosenPet.orderByChild("isChosen").equalTo("true").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //String image = snapshot.child("image").getValue().toString();
                    //String PetType = snapshot.child("PetType").getValue().toString();
                    //String isChosen = snapshot.child("isChosen").getValue().toString();
                    //String required_humidity = snapshot.child("required_humidity").getValue().toString();
                    // String required_temp = snapshot.child("required_temp").getValue().toString();
                    //String required_watertemp = snapshot.child("required_watertemp").getValue().toString();
                    // Status.setText(PetType);
                    //Log.i("OUR INFO", String.valueOf(snapshot.child("PetType").getValue()));

                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        String petType = snapshot1.child("petname").getValue().toString();
                        String getIcon = (String) snapshot1.child("petImage").getValue();
                        pet_specie = snapshot1.child("petSpecie").getValue().toString();
                        req_temp = snapshot1.child("required_temp").getValue().toString();
                        ideal_temperature_max = snapshot1.child("required_tempmax").getValue().toString();
                        req_watert = snapshot1.child("required_watertemp").getValue().toString();
                        req_watertmax = snapshot1.child("required_watertempmax").getValue().toString();
                        req_humid = snapshot1.child("required_humidity").getValue().toString();
                        req_humid_max = snapshot1.child("required_humidity_max").getValue().toString();
                        if(petType.equals("Turtle")){
                            petbutton.setBackgroundResource(R.drawable.peticon);
                            progBarLoadImage.setVisibility(View.GONE);
                            petbutton.setClickable(true);
                            ideal_temp.setText(req_temp);
                            ideal_temp_max.setText(ideal_temperature_max);
                            ideal_watertemp.setText(req_watert);
                            ideal_watertempmax.setText(req_watertmax);
                            ideal_hum.setText(req_humid);
                            ideal_hum_max.setText(req_humid_max);
                        }
                        if(petType.equals("Gecko")){
                            petbutton.setBackgroundResource(R.drawable.gecko_peticon);
                            progBarLoadImage.setVisibility(View.GONE);
                            petbutton.setClickable(true);
                            ideal_temp.setText(req_temp);
                            ideal_temp_max.setText(ideal_temperature_max);
                            ideal_watertemp.setText(req_watert);
                            ideal_watertempmax.setText(req_watertmax);
                            ideal_hum.setText(req_humid);
                            ideal_hum_max.setText(req_humid_max);
                        }
                        if(petType.equals("Frog")){
                            petbutton.setBackgroundResource(R.drawable.frog_peticon);
                            progBarLoadImage.setVisibility(View.GONE);
                            petbutton.setClickable(true);
                            ideal_temp.setText(req_temp);
                            ideal_temp_max.setText(ideal_temperature_max);
                            ideal_watertemp.setText(req_watert);
                            ideal_watertempmax.setText(req_watertmax);
                            ideal_hum.setText(req_humid);
                            ideal_hum_max.setText(req_humid_max);
                        }

                        if(petType.equals("Chameleon")){
                            petbutton.setBackgroundResource(R.drawable.pet_chameleon_noback);
                            progBarLoadImage.setVisibility(View.GONE);
                            petbutton.setClickable(true);
                            ideal_temp.setText(req_temp);
                            ideal_temp_max.setText(ideal_temperature_max);
                            ideal_watertemp.setText(req_watert);
                            ideal_watertempmax.setText(req_watertmax);
                            ideal_hum.setText(req_humid);
                            ideal_hum_max.setText(req_humid_max);
                        }
                        if(petType.equals("Snake")){
                            petbutton.setBackgroundResource(R.drawable.pet_snake_noback);
                            progBarLoadImage.setVisibility(View.GONE);
                            petbutton.setClickable(true);
                            ideal_temp.setText(req_temp);
                            ideal_temp_max.setText(ideal_temperature_max);
                            ideal_watertemp.setText(req_watert);
                            ideal_watertempmax.setText(req_watertmax);
                            ideal_hum.setText(req_humid);
                            ideal_hum_max.setText(req_humid_max);
                        }

                        if(petType.equals("Iguana")){
                            petbutton.setBackgroundResource(R.drawable.pet_iguana_noback);
                            progBarLoadImage.setVisibility(View.GONE);
                            petbutton.setClickable(true);
                            ideal_temp.setText(req_temp);
                            ideal_temp_max.setText(ideal_temperature_max);
                            ideal_watertemp.setText(req_watert);
                            ideal_watertempmax.setText(req_watertmax);
                            ideal_hum.setText(req_humid);
                            ideal_hum_max.setText(req_humid_max);
                        }
                        if(!petType.equals("Turtle") && !petType.equals("Gecko")
                            && !petType.equals("Frog") && !petType.equals("Chameleon")
                                && !petType.equals("Iguana") && !petType.equals("Snake") ){
                            petbutton.setBackgroundResource(R.drawable.noicon);
                            progBarLoadImage.setVisibility(View.GONE);
                            petbutton.setClickable(true);
                            ideal_temp.setText(req_temp);
                            ideal_temp_max.setText(ideal_temperature_max);
                            ideal_watertemp.setText(req_watert);
                            ideal_watertempmax.setText(req_watertmax);
                            ideal_hum.setText(req_humid);
                            ideal_hum_max.setText(req_humid_max);
                        }
                        mydb_for_getting= FirebaseDatabase.getInstance().getReference().child("Sensors");
                        try {
                            mydb_for_getting.addValueEventListener(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value and again
                                    // whenever data at this location is updated.
                                    String tempdata = dataSnapshot.child("temperature").getValue().toString();
                                    String watertempdata = dataSnapshot.child("watertemp").getValue().toString();
                                    String humdata = dataSnapshot.child("humidity").getValue().toString();
                                    String waterlvlStatus = dataSnapshot.child("float_switch").getValue().toString();

                                    if(waterlvlStatus.equals("0")){
                                        waterlevelStatus.setText("LOW");
                                        waterlevelStatus.setTextColor(Color.RED);
                                    }
                                    else if(waterlvlStatus.equals("1")){
                                        waterlevelStatus.setText("HIGH");
                                        waterlevelStatus.setTextColor(Color.parseColor("#169C03"));
                                    }
                                    //start of enclosure temp
                                    gauge_enc_temp = Double.parseDouble(tempdata); //double var of enclosure temp

                                    //double vars for ranges
                                    req_tempmin = Double.parseDouble(req_temp);
                                    req_tempminusone = req_tempmin - 1;
                                    req_tempmax = Double.parseDouble(ideal_temperature_max);

                                    //other vars for range
                                    double req_temp_diff = (req_tempmax - req_tempmin) + 1;
                                    double req_tempmaxplusone = req_tempmax + 1;
                                    double req_tempmaxplusn = req_tempmaxplusone + req_temp_diff;
                                    double req_tempmaxplusnplusone = req_tempmaxplusn + 1;
                                    double req_tempmaxplusnplusoneplusn = req_tempmaxplusnplusone + req_temp_diff;

                                    //start of if else statement enclosure temp
                                    if(gauge_enc_temp == 0 || gauge_enc_temp <= req_tempminusone){
                                        Status.setText("LOW");
                                        Status.setTextColor(Color.BLUE);
                                        //BaskingLightOn();
                                    }
                                    else if(gauge_enc_temp == req_tempmin || gauge_enc_temp <= req_tempmax){
                                        Status.setText("NORMAL");
                                        Status.setTextColor(Color.GREEN);
                                        autoSprayerOff();
                                        //BaskingLightOff();
                                    }
                                    else if(gauge_enc_temp == req_tempmaxplusone || gauge_enc_temp < req_tempmaxplusn){
                                        Status.setText("HIGH");
                                        Status.setTextColor(Color.YELLOW);
                                        autoSprayerOn();
                                        //BaskingLightOff();
                                    }
                                    else if(gauge_enc_temp == req_tempmaxplusnplusone || gauge_enc_temp <= req_tempmaxplusnplusoneplusn || gauge_enc_temp > req_tempmaxplusnplusoneplusn){
                                        Status.setText("TOO HOT");
                                        Status.setTextColor(Color.RED);
                                        autoSprayerOn();
                                        //BaskingLightOff();
                                    }
                                    else{
                                        Status.setText("Reading...");
                                        Status.setTextColor(Color.BLACK);
                                    }

                                    /**FOR TESTING ONLY
                                     String rth = Double.toString(req_tempmaxplusnplusoneplusn);
                                     Status.setText(rth);*/

                                    gauge_enc_wtemp = Double.parseDouble(watertempdata);

                                    req_wtempmin = Double.parseDouble(req_watert);
                                    req_wtempminusone = req_wtempmin - 1;
                                    req_wtempmax = Double.parseDouble(req_watertmax);

                                    double req_wtemp_difference = (req_wtempmax - req_wtempmin) + 1;
                                    double req_wtemp_maxplusone = req_wtempmax + 1;
                                    double req_wtemp_maxplusn = req_wtemp_maxplusone + req_wtemp_difference;
                                    double req_wtemp_maxplusnplusone = req_wtemp_maxplusn + 1;
                                    double req_wtemp_maxplusnplusoneplusn = req_wtemp_maxplusnplusone + req_wtemp_difference;

                                    if(watertempdata.equals("-127")){
                                        StatusWaterTemp.setText("Sensor Error");
                                        StatusWaterTemp.setTextColor(Color.BLACK);
                                        water_temp.setText("Error");
                                        water_temp.setTextColor(Color.RED);
                                    }
                                    else if(gauge_enc_wtemp == 0 || gauge_enc_wtemp <= req_wtempminusone){
                                        StatusWaterTemp.setText("LOW");
                                        StatusWaterTemp.setTextColor(Color.BLUE);
                                        //BaskingLightOn();
                                    }
                                    else if(gauge_enc_wtemp == req_wtempmin || gauge_enc_wtemp <= req_wtempmax){
                                        StatusWaterTemp.setText("NORMAL");
                                        StatusWaterTemp.setTextColor(Color.GREEN);
                                        //BaskingLightOff();
                                    }
                                    else if(gauge_enc_wtemp == req_wtemp_maxplusone || gauge_enc_wtemp < req_wtemp_maxplusn){
                                        StatusWaterTemp.setText("HIGH");
                                        StatusWaterTemp.setTextColor(Color.YELLOW);
                                        //BaskingLightOff();
                                    }
                                    else if(gauge_enc_wtemp == req_wtemp_maxplusnplusone || gauge_enc_wtemp <= req_wtemp_maxplusnplusoneplusn || gauge_enc_wtemp > req_wtemp_maxplusnplusoneplusn){
                                        StatusWaterTemp.setText("TOO HOT");
                                        StatusWaterTemp.setTextColor(Color.RED);
                                        //BaskingLightOff();
                                    }
                                    else{
                                        StatusWaterTemp.setText("Reading...");
                                        StatusWaterTemp.setTextColor(Color.BLACK);
                                        //BaskingLightOff();
                                    }

                                    temp.setText(tempdata);
                                    temp.append("°C");
                                    if(watertempdata.equals("-127")){
                                        StatusWaterTemp.setText("Sensor Error");
                                        StatusWaterTemp.setTextColor(Color.BLACK);
                                        water_temp.setText("Error");
                                        water_temp.setTextColor(Color.RED);
                                    }
                                    else{
                                        water_temp.setText(watertempdata);
                                        water_temp.append("°C");
                                        water_temp.setTextColor(Color.parseColor("#3A9262"));
                                    }

                                    gauge_enc_humidity = Double.parseDouble(humdata);
                                    req_hum_min = Double.parseDouble(req_humid);
                                    req_hum_minusone = req_hum_min - 1;
                                    req_hum_max = Double.parseDouble(req_humid_max);

                                    double req_hum_difference = (req_hum_max - req_hum_min) + 1;
                                    double req_hum_maxplusone = req_hum_max + 1;
                                    double req_hum_maxplusn = req_hum_maxplusone + req_hum_difference;
                                    double req_hum_maxplusnplusone = req_hum_maxplusn + 1;
                                    double req_hum_maxplusnplusoneplusn = req_hum_maxplusnplusone + req_hum_difference;

                                    //start of if else statement enclosure temp
                                    if(gauge_enc_humidity == 0 || gauge_enc_humidity <= req_hum_minusone){
                                        statusHum.setText("LOW");
                                        statusHum.setTextColor(Color.BLUE);
                                        BaskingLightOff();
                                    }
                                    else if(gauge_enc_humidity == req_hum_min || gauge_enc_humidity <= req_hum_max){
                                        statusHum.setText("NORMAL");
                                        statusHum.setTextColor(Color.GREEN);
                                        BaskingLightOff();
                                    }
                                    else if(gauge_enc_humidity == req_hum_maxplusone || gauge_enc_humidity < req_hum_maxplusn){
                                        statusHum.setText("HIGH");
                                        statusHum.setTextColor(Color.YELLOW);
                                        BaskingLightOn();
                                    }
                                    else if(gauge_enc_humidity == req_hum_maxplusnplusone || gauge_enc_humidity <= req_hum_maxplusnplusoneplusn || gauge_enc_humidity > req_hum_maxplusnplusoneplusn){
                                        statusHum.setText("TOO HUMID");
                                        statusHum.setTextColor(Color.RED);
                                        BaskingLightOn();
                                    }
                                    else{
                                        statusHum.setText("Reading...");
                                        statusHum.setTextColor(Color.BLACK);
                                        BaskingLightOff();
                                    }
                                    hum.setText(humdata);
                                    hum.append("%");

                                    progBarencTemp.setVisibility(View.GONE);
                                    progBarWatTemp.setVisibility(View.GONE);
                                    progBarHum.setVisibility(View.GONE);

                                    /**gauge_enc_temp = Double.parseDouble(tempdata);
                                     double gauge_enc_ideal_temp_minusone = Double.parseDouble(String.valueOf(req_tempminusone));
                                     double gauge_enc_ideal_temp = Double.parseDouble(String.valueOf(req_temp));
                                     double gauge_enc_ideal_tempmax = Double.parseDouble(String.valueOf(req_tempmax));*/

                                    Range_1.setFrom(0);
                                    Range_1.setTo(req_tempminusone);

                                    Range_2.setFrom(req_tempmin);
                                    Range_2.setTo(req_tempmax);

                                    Range_3.setFrom(req_tempmaxplusone);
                                    Range_3.setTo(req_tempmaxplusn);

                                    Range_4.setFrom(req_tempmaxplusnplusone);
                                    Range_4.setTo(req_tempmaxplusnplusoneplusn);

                                    Range_1.setColor(Color.parseColor("#1a75ff"));
                                    Range_2.setColor(Color.GREEN);
                                    Range_3.setColor(Color.YELLOW);
                                    Range_4.setColor(Color.RED);

                                    IdGauge.setMinValue(0);
                                    IdGauge.setMaxValue(req_tempmaxplusnplusoneplusn);
                                    IdGauge.setValue(gauge_enc_temp);

                                    IdGauge.addRange(Range_1);
                                    IdGauge.addRange(Range_2);
                                    IdGauge.addRange(Range_3);
                                    IdGauge.addRange(Range_4);

                                    ///water temp gauge
                                    Range_1_wt.setFrom(0);
                                    Range_1_wt.setTo(req_wtempminusone);

                                    Range_2_wt.setFrom(req_wtempmin);
                                    Range_2_wt.setTo(req_wtempmax);

                                    Range_3_wt.setFrom(req_wtemp_maxplusone);
                                    Range_3_wt.setTo(req_wtemp_maxplusn);

                                    Range_4_wt.setFrom(req_wtemp_maxplusnplusone);
                                    Range_4_wt.setTo(req_wtemp_maxplusnplusoneplusn);

                                    Range_1_wt.setColor(Color.parseColor("#1a75ff"));
                                    Range_2_wt.setColor(Color.GREEN);
                                    Range_3_wt.setColor(Color.YELLOW);
                                    Range_4_wt.setColor(Color.RED);

                                    IdGauge2.setMinValue(0);
                                    IdGauge2.setMaxValue(req_wtemp_maxplusnplusoneplusn);
                                    IdGauge2.setValue(gauge_enc_wtemp);

                                    IdGauge2.addRange(Range_1_wt);
                                    IdGauge2.addRange(Range_2_wt);
                                    IdGauge2.addRange(Range_3_wt);
                                    IdGauge2.addRange(Range_4_wt);

                                    ///humid gauge
                                    Range_1_hum.setFrom(0);
                                    Range_1_hum.setTo(req_hum_minusone);

                                    Range_2_hum.setFrom(req_hum_min);
                                    Range_2_hum.setTo(req_hum_max);

                                    Range_3_hum.setFrom(req_hum_maxplusone);
                                    Range_3_hum.setTo(req_hum_maxplusn);

                                    Range_4_hum.setFrom(req_hum_maxplusnplusone);
                                    Range_4_hum.setTo(req_hum_maxplusnplusoneplusn);

                                    Range_1_hum.setColor(Color.parseColor("#1a75ff"));
                                    Range_2_hum.setColor(Color.GREEN);
                                    Range_3_hum.setColor(Color.YELLOW);
                                    Range_4_hum.setColor(Color.RED);

                                    IdGauge3.setMinValue(0);
                                    IdGauge3.setMaxValue(req_hum_maxplusnplusoneplusn);
                                    IdGauge3.setValue(gauge_enc_humidity);

                                    IdGauge3.addRange(Range_1_hum);
                                    IdGauge3.addRange(Range_2_hum);
                                    IdGauge3.addRange(Range_3_hum);
                                    IdGauge3.addRange(Range_4_hum);
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                }
                            });
                        }
                        catch(Exception e)        {        }

                    }

                                /*else{
                                    Toast.makeText(Dashboard.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                    temp.setText("offline");
                                    water_temp.setText("offline");
                                    hum.setText("offline");

                                    hum.setTextColor(Color.parseColor("#fc2819"));
                                    temp.setTextColor(Color.parseColor("#fc2819"));
                                    water_temp.setTextColor(Color.parseColor("#fc2819"));

                                    progBarencTemp.setVisibility(View.GONE);
                                    progBarWatTemp.setVisibility(View.GONE);
                                    progBarHum.setVisibility(View.GONE);
                                }
                            }
                        });**/
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch(Exception e)        {        }

    }

    @Override
    public void networkUnavailable() {
        Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        temp.setText("offline");
        water_temp.setText("offline");
        hum.setText("offline");
        hum.setTextColor(Color.parseColor("#fc2819"));
        temp.setTextColor(Color.parseColor("#fc2819"));
        water_temp.setTextColor(Color.parseColor("#fc2819"));
        petbutton.setVisibility(View.INVISIBLE);
        progBarencTemp.setVisibility(View.GONE);
        progBarWatTemp.setVisibility(View.GONE);
        progBarHum.setVisibility(View.GONE);
        progBarLoadImage.setVisibility(View.GONE);
        NoInternet.setVisibility(View.VISIBLE);
        petbutton.setClickable(false);
        Status.setText("");
        StatusWaterTemp.setText("");
        statusHum.setText("");

        String zero_str = "0";
        double zero = Double.parseDouble(zero_str);

        IdGauge.setMinValue(0);
        IdGauge.setMaxValue(zero);

        IdGauge2.setMinValue(0);
        IdGauge2.setMaxValue(zero);

        IdGauge3.setMinValue(0);
        IdGauge3.setMaxValue(zero);

        IdGauge.setValue(zero);
        IdGauge2.setValue(zero);
        IdGauge3.setValue(zero);

        Range_1.setColor(Color.parseColor("#EAEAEA"));
        Range_2.setColor(Color.parseColor("#EAEAEA"));
        Range_3.setColor(Color.parseColor("#EAEAEA"));
        Range_4.setColor(Color.parseColor("#EAEAEA"));

        Range_1_wt.setColor(Color.parseColor("#EAEAEA"));
        Range_2_wt.setColor(Color.parseColor("#EAEAEA"));
        Range_3_wt.setColor(Color.parseColor("#EAEAEA"));
        Range_4_wt.setColor(Color.parseColor("#EAEAEA"));

        Range_1_hum.setColor(Color.parseColor("#EAEAEA"));
        Range_2_hum.setColor(Color.parseColor("#EAEAEA"));
        Range_3_hum.setColor(Color.parseColor("#EAEAEA"));
        Range_4_hum.setColor(Color.parseColor("#EAEAEA"));
        /*String zerostring = String.valueOf(zero);
        Status.setText(zerostring);*/
    }

    public void startNetworkBroadcastReceiver(Context currentContext) {
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener((NetworkStateReceiver.NetworkStateReceiverListener) currentContext);
        registerNetworkBroadcastReceiver(currentContext);
    }

    public void registerNetworkBroadcastReceiver(Context currentContext) {
        currentContext.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }
    public void unregisterNetworkBroadcastReceiver(Context currentContext) {
        currentContext.unregisterReceiver(networkStateReceiver);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "HumidiReptilesReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("humidiRepID",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }
}