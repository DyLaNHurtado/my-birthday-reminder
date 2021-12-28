package es.dylanhurtado.mybirthdayreminder;


import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private static Adapter adapter;
    private static BirthdayOnClick onClick;
    private List<Birthday> birthdayList;
    private List<Birthday> originalList;


    private Adapter(List<Birthday> birthdayList, BirthdayOnClick onClick) {
        this.birthdayList = birthdayList;
        this.originalList = new ArrayList<>();
        this.originalList.addAll(birthdayList);
        Adapter.onClick = onClick;
    }

    public static Adapter getInstance(List<Birthday> birthdayList, BirthdayOnClick onClick) {
        if (adapter == null) {
            adapter = new Adapter(birthdayList, onClick);
            return adapter;
        }
        return adapter;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Birthday birthday = birthdayList.get(position);
        holder.name.setText(birthday.getName());
        holder.birthDate.setText(getStringDateWithFormat(birthday.getBirthdate()));
        holder.name.setText(birthday.getName());
        holder.daysLeft.setText(getDaysDiff(getDateWithFormat(birthday.getBirthdate())));
        holder.nextYear.setText(String.valueOf(getNextYear(getDateWithFormat(birthday.getBirthdate()))));
        if (birthday.getPhoto() != null) {
            holder.photo.setImageURI(Uri.parse(birthday.getPhoto()));
        } else {
            holder.photo.setImageResource(R.drawable.cumpleanero);
        }
    }


    @Override
    public int getItemCount() {
        return birthdayList.size();
    }

    public void searchFilter(String strSearch) {
        if (strSearch.trim().length() == 0) {
            birthdayList.clear();
            birthdayList.addAll(originalList);
        } else {
            birthdayList.clear();
            for (int i = 0; i < originalList.size(); i++) {
                if (originalList.get(i).getName().toLowerCase(Locale.ROOT).contains(strSearch.toLowerCase(Locale.ROOT))) {
                    birthdayList.add(originalList.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }

    public void addBirthday(Birthday birthday) {
        birthdayList = originalList;
        birthdayList.add(birthday);
        notifyDataSetChanged();
    }

    public void removeBirthday(int position) {
        birthdayList = originalList;
        birthdayList.remove(position);
        notifyDataSetChanged();
    }

    public void editBirthday(UUID id, Birthday birthdayBuilded) {
        birthdayList = originalList;
        for (int i = 0; i < birthdayList.size(); i++) {
            if (birthdayList.get(i).getId().toString().equals(id.toString())) {
                birthdayList.get(i).setPhoto(birthdayBuilded.getPhoto());
                birthdayList.get(i).setName(birthdayBuilded.getName());
                birthdayList.get(i).setBirthdate(birthdayBuilded.getBirthdate());
            }
        }
        notifyDataSetChanged();
    }


    private String getStringDateWithFormat(String date) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat("dd/MM/yyyy").format(formatter.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateFormat;
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

    public String getDaysDiff(Date startDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dob = formatter.format(startDate);
        String dateOB = dob.split("/")[0] + "/" + dob.split("/")[1] + "/" + Calendar.getInstance().get(Calendar.YEAR);
        if ((Calendar.getInstance().get(Calendar.YEAR) % 4 != 0) && (Integer.parseInt(dob.split("/")[0]) == 29) && (Integer.parseInt(dob.split("/")[1]) == 2)) {
            dateOB = "01/03/" + Calendar.getInstance().get(Calendar.YEAR);
        }
        long days = 0;
        try {
            Date bday = formatter.parse(dateOB);
            Date today = Calendar.getInstance().getTime();
            assert bday != null;
            if (bday.before(today)) {
                Calendar c = Calendar.getInstance();
                c.setTime(bday);
                c.add(Calendar.YEAR, 1);
                bday = new Date(c.getTimeInMillis());
            }
            days = TimeUnit.DAYS.convert(bday.getTime() - today.getTime(), TimeUnit.MILLISECONDS) + 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }

            if (days != 1) {
                return days + " days left";
            } else {
                return days + " day left";
        }
    }

    public int getNextYear(Date birthdate) {

        //This could be done in an one line in java 11 with Period.between() or LocalDate lmao
        Calendar birthDay = new GregorianCalendar();
        birthDay.setTime(birthdate);
        birthDay.set(Calendar.MONTH, birthDay.get(Calendar.MONTH) + 1);
        birthDay.set(Calendar.HOUR_OF_DAY, 0);
        birthDay.set(Calendar.MINUTE, 0);
        birthDay.set(Calendar.SECOND, 0);
        birthDay.set(Calendar.MILLISECOND, 0);

        Calendar today = new GregorianCalendar();
        today.setTime(Calendar.getInstance().getTime());
        today.set(Calendar.MONTH, today.get(Calendar.MONTH) + 1);
        int yearsInBetween = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int monthsDiff = today.get(Calendar.MONTH) - birthDay.get(Calendar.MONTH);
        if (monthsDiff < 0) {
            yearsInBetween--;
            monthsDiff = monthsDiff + 12;
        }
        return yearsInBetween + 1;

    }

    public List<Birthday> getBirthdayList() {
        return birthdayList;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final TextView name;
        private final TextView birthDate;
        private final TextView nextYear;
        private final TextView daysLeft;
        private final ImageView photo;
        private ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            photo = itemView.findViewById(R.id.photo);
            name = itemView.findViewById(R.id.name);
            birthDate = itemView.findViewById(R.id.birthdate);
            nextYear = itemView.findViewById(R.id.nextYear);
            daysLeft = itemView.findViewById(R.id.daysLeft);
            layout = itemView.findViewById(R.id.layoutItemList);
        }

        @Override
        public void onClick(View view) {
            int position = getBindingAdapterPosition();
            Log.i("info", "" + position);
            onClick.OnClick(position);
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getBindingAdapterPosition();
            Log.i("info", "" + position);
            onClick.OnLongClick(position, layout);
            return false;
        }
    }

}
