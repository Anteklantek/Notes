package com.example.antek.notes10clouds.events;

import com.example.antek.notes10clouds.models.Note;


public final class FromItemDetailsFragmentToActivityOnBackPressedResponse {

    private final Note note;

    public FromItemDetailsFragmentToActivityOnBackPressedResponse(Note note) {
        this.note = note;
    }

    public Note getNote() {
        return note;
    }
}

