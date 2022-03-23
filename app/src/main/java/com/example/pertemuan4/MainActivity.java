package com.example.pertemuan4;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pertemuan4.databinding.ActivityMainBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

    public class MainActivity extends AppCompatActivity {

        private ActivityMainBinding binding;
        private MaterialTimePicker picker;
        private Calendar calendar;
        private AlarmManager alarmManager;
        private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createNotificationChannel();
        Button ext=findViewById(R.id.exit);
        Button stp=findViewById(R.id.Stop);

        binding.setwaktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        binding.setalarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm();
            }
        });

        binding.exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });

    }

        private void cancelAlarm() {
            Intent intent = new Intent(this,MyReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
            if (alarmManager == null){
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            }

            alarmManager.cancel(pendingIntent);
            final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
            builder.setIcon(R.drawable.ic_baseline_android_24);
            builder.setTitle("Confirmation ");
            builder.setMessage("Apakah anda ingin keluar?");
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("Tidak",null);
            final AlertDialog alertDialog =builder.create();
            alertDialog.show();
        }

        private void setAlarm() {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this,MyReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,pendingIntent);

            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Alert ");
            alert.setMessage("Alarm telah di buat");
            alert.setPositiveButton("OK", null);
            alert.show();
        }

        private void showTimePicker() {
            picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Pilih Waktu")
                    .build();
            picker.show(getSupportFragmentManager(),"aulia");
            picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (picker.getHour() > 12){
                        binding.setwaktu.setText(
                                String.format("%02d",(picker.getHour()-12))+" : "+String.format("%02d",picker.getMinute())+" PM"
                        );
                    }else {
                        binding.setwaktu.setText(picker.getHour()+" : " + picker.getMinute() + " AM");
                    }
                    calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY,picker.getHour());
                    calendar.set(Calendar.MINUTE,picker.getMinute());
                    calendar.set(Calendar.SECOND,0);
                    calendar.set(Calendar.MILLISECOND,0);
                }
            });
            Toast.makeText(this, "Setelah berhasil set waktu,klik button 'Mulai Alarm' untuk mengaktifkan Alarm.", Toast.LENGTH_SHORT).show();
        }

        private void createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                CharSequence name = "AuliaChannel";
                String description = "Alarm Ku";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel("aulia",name,importance);
                channel.setDescription(description);

                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);

            }


        }

}