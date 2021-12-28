package es.dylanhurtado.mybirthdayreminder;

import androidx.constraintlayout.widget.ConstraintLayout;

public interface BirthdayOnClick {

    void OnClick(int position);

    void OnLongClick(int position, ConstraintLayout constraintLayout);
}
