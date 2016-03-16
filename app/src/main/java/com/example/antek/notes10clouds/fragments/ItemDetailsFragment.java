package com.example.antek.notes10clouds.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.antek.notes10clouds.R;
import com.example.antek.notes10clouds.events.FromActivityToItemDetailsOnBackPressed;
import com.example.antek.notes10clouds.events.FromItemDetailsFragmentToActivityOnBackPressedResponse;
import com.example.antek.notes10clouds.events.NoteCanceledEvent;
import com.example.antek.notes10clouds.models.Note;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ItemDetailsFragment extends android.support.v4.app.Fragment {

    private Note note;
    private int position;

    @Bind(R.id.body)
    EditText bodyEditText;
    @Bind(R.id.date)
    TextView dateTextView;
    @Bind(R.id.background)
    LinearLayout background;
    @Bind(R.id.save_button)
    Button saveButton;
    @Bind(R.id.cancel_button)
    Button cancelButton;

    public static ItemDetailsFragment newInstance(Note note, int position, Context context) {
        ItemDetailsFragment fragment = new ItemDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(context.getString(R.string.from_main_fragment_to_item_details_fragment_note), note);
        bundle.putInt(context.getString(R.string.from_main_fragment_to_item_detail_position), position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_details, container, false);
        ButterKnife.bind(this, view);
        getDataFromBundle();
        setDataToViews();
        setActionBar();
        setTransitionNames();
        setButtons();
        return view;
    }

    private void getDataFromBundle() {
        note = (Note) getArguments().get(getString(R.string.from_main_fragment_to_item_details_fragment_note));
        position = getArguments().getInt(getString(R.string.from_main_fragment_to_item_detail_position));
    }

    private void setDataToViews() {
        if (note != null) {
            bodyEditText.setText(note.getBody());
            dateTextView.setText(note.getDate().toString());
        }
    }

    private void setActionBar() {
        android.support.v7.app.ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }
        setHasOptionsMenu(true);
    }

    private void setTransitionNames() {
        ViewCompat.setTransitionName(bodyEditText, "body" + position);
        ViewCompat.setTransitionName(dateTextView, "date" + position);
        ViewCompat.setTransitionName(background, "background" + position);
    }

    private void setButtons() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new NoteCanceledEvent());
            }
        });
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        focusEditTextAndShowKeyboard();
        super.onViewCreated(view, savedInstanceState);
    }

    private void focusEditTextAndShowKeyboard() {
        bodyEditText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(bodyEditText, InputMethodManager.SHOW_IMPLICIT);
    }


    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Subscribe
    public void onEvent(FromActivityToItemDetailsOnBackPressed e) {
        note.setBody(bodyEditText.getText().toString());
        EventBus.getDefault().post(new FromItemDetailsFragmentToActivityOnBackPressedResponse(note));
    }

}
