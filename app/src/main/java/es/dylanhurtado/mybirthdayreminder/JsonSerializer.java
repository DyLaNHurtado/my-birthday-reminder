package es.dylanhurtado.mybirthdayreminder;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonSerializer {

    ArrayList<Birthday> birthdays;

    private String mFilename;
    private Context mContext;


    public JsonSerializer(String mFilename, Context mContext) {
        this.mFilename = mFilename;
        this.mContext = mContext;
    }

    public void save(List<Birthday> birthdays) throws IOException {

        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").setPrettyPrinting().create();
        String json = gson.toJson(birthdays);
        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFilename, mContext.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(json);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

    }


    public ArrayList<Birthday> load() throws IOException {

        BufferedReader reader = null;
        try {
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));

            Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").setPrettyPrinting().create();

            Type type = new TypeToken<ArrayList<Birthday>>() {
            }.getType();

            birthdays = gson.fromJson(reader, type);

        } catch (FileNotFoundException e) {
            birthdays = new ArrayList<Birthday>();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return birthdays;
    }

}
