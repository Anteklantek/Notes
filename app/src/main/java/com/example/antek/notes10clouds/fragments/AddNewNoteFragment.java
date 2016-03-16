package com.example.antek.notes10clouds.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.antek.notes10clouds.R;
import com.example.antek.notes10clouds.events.FromActivityToNewNoteOnBackPressed;
import com.example.antek.notes10clouds.events.FromAddNewNoteToActivityOnBackRespone;
import com.example.antek.notes10clouds.events.NoteCanceledEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AddNewNoteFragment extends android.support.v4.app.Fragment {

    @Bind(R.id.body)
    EditText bodyEditText;
    @Bind(R.id.save_button)
    Button saveButton;
    @Bind(R.id.cancel_button)
    Button cancelButton;

    public static AddNewNoteFragment newInstance() {
        return new AddNewNoteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_note, container, false);
        ButterKnife.bind(this, view);
        setActionBar();
        setButtons();
        return view;
    }

    private void setActionBar() {
        android.support.v7.app.ActionBar supportActionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(supportActionBar!=null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);}
        setHasOptionsMenu(true);
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
        inputManager.showSoftInput(bodyEditText,InputMethodManager.SHOW_IMPLICIT);
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
    public void onEvent(FromActivityToNewNoteOnBackPressed event) {
        EventBus.getDefault().post(new FromAddNewNoteToActivityOnBackRespone(bodyEditText.getText().toString()));
    }
}
