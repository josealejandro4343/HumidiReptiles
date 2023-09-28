package com.example.humidireptiles;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.humidireptiles.databinding.ActivityFeedingScheduleBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public class FeedingSchedule extends AppCompatActivity {

    private TextView timeInit;
    private ActivityFeedingScheduleBinding binding;
    private MaterialTimePicker picker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    //private DatabaseReference mDatabase;
    private ImageButton Dashboardfeeding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_feeding_schedule);
        binding = ActivityFeedingScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        timeInit = (TextView) findViewById(R.id.selectedTime);

        createNotificationChannel();
        //mDatabase = FirebaseDatabase.getInstance().getReference();

        binding.selectTime.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
                showTimePicker();
            }
        });
        binding.setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String schedalarm = timeInit.getText().toString();

                //if(!timeInit.getText().toString().equals("00:00 PM")) {
                    setAlarm();
                //}
            }
        });

        binding.cancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });

        Dashboardfeeding = (ImageButton) findViewById(R.id.DashboardbtnFeedingSched);
        Dashboardfeeding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent refresh = new Intent(FeedingSchedule.this, Dashboard.class);
                startActivity(refresh);
                finish();
            }
        });

        /**WirelessAutoSpray = (ImageButton) findViewById(R.id.feedingSchedule);
        WirelessAutoSpray.setOnClickListener(this);**/

    }

    private void cancelAlarm() {

        Intent intent = new Intent(this,AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

        if (alarmManager == null){

            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();
        timeInit.setText("00:00 PM");

    }

    private void setAlarm() {

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        //writeNewSched();

        Toast.makeText(this, "Alarm set Successfully", Toast.LENGTH_SHORT).show();
    }

    private void showTimePicker() {

        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();

        picker.show(getSupportFragmentManager(),"humidiRepID");

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (picker.getHour() > 12){

                    binding.selectedTime.setText(
                            String.format("%02d",(picker.getHour()-12))+" : "+String.format("%02d",picker.getMinute())+" PM"
                    );
                }
                else if (picker.getHour() == 12){
                    binding.selectedTime.setText(
                            String.format("%02d",(picker.getHour()))+" : "+String.format("%02d",picker.getMinute())+" PM"
                    );
                }
                else {

                    binding.selectedTime.setText(
                            String.format("%02d",(picker.getHour()))+" : "+String.format("%02d",picker.getMinute())+ " AM"
                    );


                }
                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,picker.getHour());
                calendar.set(Calendar.MINUTE,picker.getMinute());
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);

            }
        });


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

    /*private void writeNewSched() {
        ScheduledAlarms scheduledAlarms = new ScheduledAlarms(timeInit.getText().toString());
        //mDatabase.child("Schedule").setValue(scheduledAlarms);
    }*/
}