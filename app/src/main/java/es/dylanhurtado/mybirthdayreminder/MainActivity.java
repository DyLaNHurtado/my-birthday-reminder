package es.dylanhurtado.mybirthdayreminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BirthdayOnClick, SearchView.OnQueryTextListener {
    //Notification
    private final int NOTIFICATION_ID = 0;
    private static final String CHANNEL_ID = "BirthdayNotification";
    //OnClick
    private OnLongClickDialog dialogOptions;
    private OnClickDialog onClickDialog;
    //SearchBar
    private SearchView searchView;
    //RecycledView and Adapter
    private RecyclerView recyclerView;
    private Adapter adapter;
    private LinearLayoutManager layoutManager;
    private static MainActivity mainActivity;
    private List<Birthday> birthdayList;
    //JSON
    private JsonSerializer jsonSerializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheme(R.style.Theme_MyBirthdayReminder_NoActionBar);
        mainActivity = this;
        loadData();
        resetEditPrefs();

        if (birthdayList == null) {
            birthdayList = new ArrayList<>();
        }
        this.recyclerView = findViewById(R.id.rv);
        this.layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(layoutManager);

        this.adapter = Adapter.getInstance(birthdayList, this);
        this.recyclerView.setAdapter(adapter);
        this.searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        sendNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainActivity = this;
        loadData();
        resetEditPrefs();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainActivity = this;
        loadData();
        resetEditPrefs();
    }

    public void openAddBirthdayActivity(MenuItem item) {
        startActivity(new Intent(this, AddBirthdayActivity.class));
    }

    public void openSettingsActivity(MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class));
    }


    private void loadData() {
        jsonSerializer = new JsonSerializer("birthdays.json", MainActivity.this.getApplicationContext());

        try {
            birthdayList = jsonSerializer.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        try {
            jsonSerializer.save(birthdayList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnClick(int position) {
        loadData();
        onClickDialog = new OnClickDialog(adapter.getBirthdayList(), position);
        onClickDialog.show(mainActivity.getSupportFragmentManager(), "onClickDialog");
    }

    @Override
    public void OnLongClick(int position, ConstraintLayout constraintLayout) {
        loadData();
        dialogOptions = new OnLongClickDialog(adapter.getBirthdayList(), adapter.getBirthdayList().get(position), position, false, constraintLayout, adapter);
        dialogOptions.show(mainActivity.getSupportFragmentManager(), "dialogOptions");
        loadData();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.searchFilter(newText);
        return false;
    }

    private boolean getNotificationsStatusPrefs() {
        SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        return preferences.getBoolean("notifications", true);
    }

    private void sendNotification() {
        for (Birthday i : birthdayList) {
            if (adapter.getDaysDiff(getDateWithFormat(i.getBirthdate())).startsWith("365")) {//If birthdate equals today
                if (getNotificationsStatusPrefs()) { //If notifications are enabled send it
                    createNotification(i.getName());
                }
            }
        }
    }

    private void createNotification(String name) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){//To versions O or more
            CharSequence nameChannel= "Notfication";
            NotificationChannel notificationChannel= new NotificationChannel(CHANNEL_ID,nameChannel, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.cake_icon)
                .setContentTitle(getResources().getString(R.string.notificationTitle))
                .setContentText(getResources().getString(R.string.notificationTextPart1)+" " + name +" "+ getResources().getString(R.string.notificationTextPart2))
                .setColor(Color.MAGENTA)
                .setLights(Color.MAGENTA, 1000, 1000)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());
    }

    private Date getDateWithFormat(String stringDate) {

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = formatter.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    private void resetEditPrefs(){
            SharedPreferences preferences = this.getSharedPreferences("preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isNew", true);
            editor.putString("idToEdit", "");
            editor.apply();
    }



}