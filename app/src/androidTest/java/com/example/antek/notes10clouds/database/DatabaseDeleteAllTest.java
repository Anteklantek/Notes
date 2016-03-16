package com.example.antek.notes10clouds.database;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;



import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DatabaseDeleteAllTest {
    private DatabaseAdapter databaseAdapter;
    private Cursor cursor;

    @Before
    public void setUp() throws Exception {
        getTargetContext().deleteDatabase(DatabaseAdapter.DB_NAME);
        databaseAdapter = new DatabaseAdapter(getTargetContext());
        databaseAdapter.open();
        for(int i = 0; i<100; i++){
            databaseAdapter.insertNote("test note"+i);
        }
    }

    @After
    public void tearDown() throws Exception {
        databaseAdapter.close();
    }

    @Test
    public void shouldDeleteAllNotes() {
        databaseAdapter.deleteAllNotes();
        assertThat(databaseAdapter.getAllNotes().getCount(),is(0));
    }

}
