package com.example.antek.notes10clouds.transitions;


import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;

public class DetailsTransition extends TransitionSet {
    public DetailsTransition() {
        addTransition(new ChangeBounds()).addTransition(new ChangeTransform());
    }
}
