package es.dylanhurtado.mybirthdayreminder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

public class OnClickDialog extends DialogFragment {

    private List<Birthday> birthdayList;
    private int position;
    private TextView name;
    private TextView birthdate;
    private ImageView photo;

    public OnClickDialog(List<Birthday> birthdayList, int position) {
        if (birthdayList == null) {
            birthdayList = new ArrayList<>();
            Birthday birthday = new Birthday();
            birthdayList.add(birthday);
        }
        this.birthdayList = birthdayList;
        this.position = position;

    }


    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_onclick, null);
        this.name = dialogView.findViewById(R.id.birthdayPreview_Name);
        this.birthdate = dialogView.findViewById(R.id.birthdayPreview_Date);
        this.photo = dialogView.findViewById(R.id.birthdayPreviewPhoto);


        name.setText(birthdayList.get(position).getName());
        birthdate.setText(birthdayList.get(position).getBirthdate());
        if(birthdayList.get(position).getPhoto()!=null){
            photo.setImageURI(Uri.parse((birthdayList.get(position).getPhoto())));
        }else{photo.setImageResource(R.drawable.cumpleanero);}

        builder.setView(dialogView);
        return builder.show();
    }

}
