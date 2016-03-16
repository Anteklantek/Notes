package com.example.antek.notes10clouds.database;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;



import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class DatabaseInsertTest {
    private DatabaseAdapter databaseAdapter;
    private Cursor cursor;

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
    public void shouldAddNote() {
        for(int i = 0; i<100; i++){
        databaseAdapter.insertNote("test note"+i);
        cursor = databaseAdapter.getAllNotes();
        assertThat(cursor.getCount(),is(i + 1));
        }
    }
}