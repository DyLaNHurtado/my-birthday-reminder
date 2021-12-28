package es.dylanhurtado.mybirthdayreminder;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AddBirthdayActivity extends AppCompatActivity {
    private final int VALOR_RETORNO = 1;
    private List<Birthday> birthdayList;
    private EditText date;
    private ImageView photo;
    private Button save, cancel, attachImage;
    private int day, month, year;
    private Birthday birthday;
    private JsonSerializer jsonSerializer;
    private Adapter adapter;

    private final int CODE_REQUEST_READ_PERM = 90;
    private final int CODE_REQUEST_WRITE_PERM = 91;

    public AddBirthdayActivity() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_birthday);
        loadData();

        date = findViewById(R.id.editTextDate);

        birthday = new Birthday();
        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);


        if (birthdayList == null) {
            birthdayList = new ArrayList<>();
        }
        adapter = Adapter.getInstance(birthdayList, null);


        // To navigation button of toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_add);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        save = findViewById(R.id.saveButton);
        cancel = findViewById(R.id.cancelButton);
        attachImage = findViewById(R.id.attachButton);


        save.setOnClickListener(view -> {
            if (!isNewPreference()) {
                editBirthday();
            } else {
                saveBirthday();
            }
            saveData();
        });

        cancel.setOnClickListener(view -> finish());

        date.setOnClickListener(view -> showDatePickerDialog());

        attachImage.setOnClickListener(view -> searchPhoto());

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    @SuppressLint("IntentReset")
    public void searchPhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (verificatePermisions()) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.chooseFile)), VALOR_RETORNO);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.chooseFile)), VALOR_RETORNO);
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void saveBirthday() {
        if (validateData()) {

            birthday.setId(UUID.randomUUID());
            birthday.setName(((EditText) findViewById(R.id.editTextName)).getText().toString());

            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String date;
            try {
                String dob_var = (((EditText) findViewById(R.id.editTextDate)).getText().toString());
                date = new SimpleDateFormat("dd/MM/yyyy").format(formatter.parse(dob_var));
                birthday.setBirthdate(date);

                adapter.addBirthday(birthday);
                birthdayList.add(birthday);


                finish();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
                Log.i("E1", e.toString());
            }

        } else {
            Toast.makeText(AddBirthdayActivity.this.getApplicationContext()
                    , this.getString(R.string.errorValidate)
                    , Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateData() {
        EditText textName = findViewById(R.id.editTextName);
        return !textName.getText().toString().trim().equals("") && !date.getText().toString().trim().equals("");
    }

    @SuppressLint("SimpleDateFormat")
    public void editBirthday() {
        if (validateData()) {
            birthday.setId(getIdPref());
            birthday.setName(((EditText) findViewById(R.id.editTextName)).getText().toString());

            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String date;
            try {
                String dob_var = (((EditText) findViewById(R.id.editTextDate)).getText().toString());
                date = new SimpleDateFormat("dd/MM/yyyy").format(formatter.parse(dob_var));
                birthday.setBirthdate(date);

                for (int i = 0; i < birthdayList.size(); i++) {
                    if (birthdayList.get(i).getId().toString().equals(birthday.getId().toString())) {
                        birthdayList.get(i).setPhoto(birthday.getPhoto());
                        birthdayList.get(i).setName(birthday.getName());
                        birthdayList.get(i).setBirthdate(birthday.getBirthdate());
                    }
                }
                adapter.editBirthday(birthday.getId(), birthday);
                saveData();

                finish();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
                Log.i("E1", e.toString());
            }

        } else {
            Toast.makeText(AddBirthdayActivity.this.getApplicationContext()
                    , this.getString(R.string.errorValidate)
                    , Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Cancelado por el usuario
        /* if (resultCode == RESULT_CANCELED) {} */
        if ((resultCode == RESULT_OK) && (requestCode == VALOR_RETORNO)) {
            //Procesar el resultado
            Uri path = data.getData(); //obtener el uri content
            photo = findViewById(R.id.birthdayPreviewPhoto);
            photo.setImageURI(path);
            birthday.setPhoto(String.valueOf(path));
        }
    }


    //Calendar Dialog Methods

    public void showDatePickerDialog() {
        showDialog(0);
    }


    @Override
    @Deprecated
    public Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    public final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @SuppressLint("SetTextI18n")
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            day = selectedDay;
            month = selectedMonth;
            year = selectedYear;
            date.setText(selectedDay + "/" + (selectedMonth + 1) + "/"
                    + selectedYear);
        }
    };

    private void saveData() {
        try {
            jsonSerializer.save(birthdayList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        jsonSerializer = new JsonSerializer("birthdays.json", AddBirthdayActivity.this.getApplicationContext());

        try {
            birthdayList = jsonSerializer.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isNewPreference() {
        SharedPreferences preferences = this.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        return preferences.getBoolean("isNew", true);
    }

    private UUID getIdPref() {
        SharedPreferences preferences = this.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        return UUID.fromString(preferences.getString("idToEdit", ""));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean verificatePermisions() {
        int permWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permWrite == PackageManager.PERMISSION_GRANTED && permRead == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_REQUEST_WRITE_PERM);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CODE_REQUEST_READ_PERM);
        }
        Toast.makeText(AddBirthdayActivity.this.getApplicationContext(), getString(R.string.failedPermisions), Toast.LENGTH_SHORT).show();
        return false;
    }

}