package com.example.antek.notes10clouds.events;

import com.example.antek.notes10clouds.models.Note;


public final class RemoveItemFromDatabaseEvent {
    Note note;

    public RemoveItemFromDatabaseEvent(Note note) {
        this.note = note;
    }

    public Note getNote() {
        return note;
    }
}
