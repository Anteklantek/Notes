package com.example.antek.notes10clouds.database;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.assertTrue;


import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;


import com.example.antek.notes10clouds.models.Note;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;


@RunWith(AndroidJUnit4.class)
public class DatabaseDeleteNoteTest {
    private DatabaseAdapter databaseAdapter;

    @Before
    public void setUp() throws Exception {
        getTargetContext().deleteDatabase(DatabaseAdapter.DB_NAME);
        databaseAdapter = new DatabaseAdapter(getTargetContext());
        databaseAdapter.open();

    }

    @After
    public void tearDown() throws Exception {
        databaseAdapter.close();
    }

    @Test
    public void shouldUpdateNote() {
        databaseAdapter.insertNote("test note");
        Cursor cursor = databaseAdapter.getAllNotes();
        cursor.moveToFirst();
        long id = cursor.getLong(DatabaseAdapter.ID_COLUMN);
        databaseAdapter.deleteNote(id);
        cursor = databaseAdapter.getAllNotes();
        assertTrue(cursor.getCount()==0);
    }
}