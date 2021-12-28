package es.dylanhurtado.mybirthdayreminder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

public class OnLongClickDialog extends DialogFragment {
    private ImageView edit;
    private ImageView talk;
    private ImageView delete;
    private int position;
    private Birthday birthday;
    private List<Birthday> birthdayList;
    private boolean isNew;
    private View dialogView;
    private ConstraintLayout layout;
    private AutoCompleteTextView input;
    private String inputText;
    private Button sendButton, cancelButton;
    private Dialog talkInsertDialog;
    private Dialog deleteDialog;
    private Dialog mainDialog;
    private Animation shake;
    private Animation fade;
    private MainActivity mainActivity;
    private JsonSerializer jsonSerializer;
    private Adapter adapter;


    public OnLongClickDialog(List<Birthday> birthdayList, Birthday birthday, int position, boolean isNew, ConstraintLayout layout,Adapter adapter) {
        if (birthday == null) {
            birthday = new Birthday();
        }
        this.birthdayList = birthdayList;
        this.birthday = birthday;
        this.position = position;
        this.isNew = isNew;
        this.layout = layout;
        this.adapter=adapter;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(getPreferences().get(1)) {//If Switch Animations is true
            layout.clearAnimation();
            edit.clearAnimation();
            delete.clearAnimation();
            talk.clearAnimation();
        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        loadData();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.birthday_options, null);

        edit = dialogView.findViewById(R.id.imageViewEdit);
        delete = dialogView.findViewById(R.id.imageViewDelete);
        talk = dialogView.findViewById(R.id.imageViewTalk);

        shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        fade = AnimationUtils.loadAnimation(getContext(), R.anim.fade);

        if(getPreferences().get(2)){fade.setDuration(1000);}//Slow
        if(getPreferences().get(4)){fade.setDuration(250);}//Fast
        if(getPreferences().get(3)){fade.setDuration(500);}//Normal


        if(getPreferences().get(2)){shake.setDuration(200);}//Slow
        if(getPreferences().get(4)){shake.setDuration(50);}//Fast
        if(getPreferences().get(3)){shake.setDuration(100);}//Normal



        if(getPreferences().get(1)) {//If Animation Switch option is true
            edit.setAnimation(shake);
            delete.setAnimation(shake);
            talk.setAnimation(shake);
        }

        edit.setOnClickListener(view -> optionEdit());
        delete.setOnClickListener(view -> optionDelete());
        talk.setOnClickListener(view -> optionTalk());

        builder.setView(dialogView);
        if(getPreferences().get(1)) {//If Animation Switch option is true
            layout.startAnimation(fade);
        }
        return mainDialog = builder.show();
    }


    @SuppressLint({"IntentReset", "QueryPermissionsNeeded"})
    private void optionTalk() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.input_text, (ViewGroup) getView(), false);
        this.input = viewInflated.findViewById(R.id.input);
        this.sendButton = viewInflated.findViewById(R.id.buttonSend);
        this.cancelButton = viewInflated.findViewById(R.id.buttonCancel);
        builder.setView(viewInflated);

        talkInsertDialog = builder.show();
        // Set up the buttons
        sendButton.setOnClickListener(view -> {
            talkInsertDialog.dismiss();
            inputText = input.getText().toString();
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/*");
            share.putExtra(Intent.EXTRA_TEXT, inputText);
            startActivity(Intent.createChooser(share, "Share message"));
            mainDialog.dismiss();
        });

        cancelButton.setOnClickListener(view -> talkInsertDialog.dismiss());

    }

    private void optionDelete() {
        AlertDialog.Builder deleteAlert = new AlertDialog.Builder(getContext());

        deleteAlert.setMessage("The birthday will be deleted")
                .setPositiveButton("ok", (dialog, which) -> {
                    birthdayList.remove(position);
                    adapter.removeBirthday(position);
                    if(getPreferences().get(1)) {layout.clearAnimation();}
                    saveData();
                    mainDialog.dismiss();
                })
                .setNegativeButton("cancel", (dialog, which) -> {});

        deleteAlert.create();
        deleteDialog= deleteAlert.show();

    }

    private void optionEdit() {
        setPreferences();
        startActivity(new Intent(getContext(), AddBirthdayActivity.class));
        mainDialog.dismiss();

    }
    private void loadData(){
        jsonSerializer = new JsonSerializer("birthdays.json", getContext());

        try {
            birthdayList = jsonSerializer.load();
        } catch (Exception e){
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
    private List<Boolean> getPreferences() {
        SharedPreferences preferences = this.requireContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        List<Boolean> prefs = new ArrayList<>();
        prefs.add(preferences.getBoolean("notifications", true));
        prefs.add(preferences.getBoolean("animations", true));
        prefs.add(preferences.getBoolean("animationOptionSlow", true));
        prefs.add(preferences.getBoolean("animationOptionNormal", true));
        prefs.add(preferences.getBoolean("animationOptionFast", true));
        return prefs;
    }
    private void setPreferences() {
        SharedPreferences preferences = this.requireActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isNew", false);
        editor.putString("idToEdit", birthday.getId().toString());
        editor.apply();
    }


}
