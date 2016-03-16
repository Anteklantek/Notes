package com.example.antek.notes10clouds;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.antek.notes10clouds.database.DatabaseAdapter;
import com.example.antek.notes10clouds.events.AllItemsDeletedEvent;
import com.example.antek.notes10clouds.events.FromActivityToItemDetailsOnBackPressed;
import com.example.antek.notes10clouds.events.FromActivityToNewNoteOnBackPressed;
import com.example.antek.notes10clouds.events.FromAddNewNoteToActivityOnBackRespone;
import com.example.antek.notes10clouds.events.FromItemDetailsFragmentToActivityOnBackPressedResponse;
import com.example.antek.notes10clouds.events.NoteCanceledEvent;
import com.example.antek.notes10clouds.events.RemoveItemFromDatabaseEvent;
import com.example.antek.notes10clouds.fragments.MainFragment;
import com.example.antek.notes10clouds.models.Note;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Note> notes;
    private DatabaseAdapter databaseAdapter;
    private Cursor noteCursor;
    private MainFragment mainFragment;

    @Bind(R.id.toolbar)
    Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        databaseAdapter = new DatabaseAdapter(this);
        databaseAdapter.open();
        getAllTasks();
        if (savedInstanceState == null) {
            mainFragment = MainFragment.newInstance(notes, this);
            getSupportFragmentManager().beginTransaction().
                    add(R.id.container, mainFragment, getString(R.string.main_fragment_tag)).commit();
        }
    }

    private void getAllTasks() {
        notes = new ArrayList<>();
        noteCursor = getAllEntriesFromDb();
        updateNotesList();
    }

    private Cursor getAllEntriesFromDb() {
        noteCursor = databaseAdapter.getAllNotes();
        if (noteCursor != null) {
            startManagingCursor(noteCursor);
            noteCursor.moveToFirst();
        }
        return noteCursor;
    }

    private void updateNotesList() {
        if (noteCursor != null && noteCursor.moveToFirst()) {
            do {
                long id = noteCursor.getLong(DatabaseAdapter.ID_COLUMN);
                String body = noteCursor.getString(DatabaseAdapter.BODY_COLUMN);
                long dateInMillis = noteCursor.getLong(DatabaseAdapter.DATE_COLUMN);
                Date date = new Date(dateInMillis);
                notes.add(new Note(id, body, date));
            } while (noteCursor.moveToNext());
        }
        Collections.reverse(notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_all) {
            databaseAdapter.deleteAllNotes();
            Toast.makeText(this, getString(R.string.all_notes_deleted_toast), Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new AllItemsDeletedEvent());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        hideKeyboard();
        if (getSupportFragmentManager().findFragmentByTag(getString(R.string.details_fragment_tag)) != null) {
            EventBus.getDefault().post(new FromActivityToItemDetailsOnBackPressed());
        } else if (getSupportFragmentManager().findFragmentByTag(getString(R.string.add_note_fragment_tag)) != null) {
            EventBus.getDefault().post(new FromActivityToNewNoteOnBackPressed());
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (databaseAdapter != null)
            databaseAdapter.close();
        super.onDestroy();
    }


    private void getCurrentData() {
        noteCursor.requery();
        notes.clear();
        updateNotesList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Subscribe
    public void onEvent(FromItemDetailsFragmentToActivityOnBackPressedResponse event) {
        databaseAdapter.updateNote(event.getNote());
        Toast.makeText(this, getString(R.string.note_updated_toast), Toast.LENGTH_SHORT).show();
        getCurrentData();
        navigateToMainFragment(false);
    }

    @Subscribe
    public void onEvent(FromAddNewNoteToActivityOnBackRespone event) {
        if (event.getBody().equals("")) {
            Toast.makeText(this, getString(R.string.note_cannot_be_empty_toast), Toast.LENGTH_SHORT).show();
            navigateToMainFragment(false);
        } else {
            Toast.makeText(this, getString(R.string.new_note_created_toast), Toast.LENGTH_SHORT).show();
            databaseAdapter.insertNote(event.getBody());
            getCurrentData();
            navigateToMainFragment(true);
        }
    }

    @Subscribe
    public void onEvent(RemoveItemFromDatabaseEvent event) {
        databaseAdapter.deleteNote(event.getNote().getId());
    }


    @Subscribe
    public void onEvent(NoteCanceledEvent event){
        getCurrentData();
        navigateToMainFragment(false);
        hideKeyboard();
    }

    private void navigateToMainFragment(boolean newItemAdded) {
        getSupportFragmentManager().popBackStack();
        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.main_fragment_tag));
        mainFragment.getArguments().putParcelableArrayList(getString(R.string.from_activity_to_main_fragment_data), notes);
        mainFragment.getArguments().putBoolean(getString(R.string.new_note_added_boolean_bundle), newItemAdded);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.container, mainFragment, getString(R.string.main_fragment_tag)).commit();
    }
}
