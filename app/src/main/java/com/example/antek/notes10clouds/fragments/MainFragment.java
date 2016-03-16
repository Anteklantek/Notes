package com.example.antek.notes10clouds.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.antek.notes10clouds.R;
import com.example.antek.notes10clouds.adapters.RecyclerViewAdapter;
import com.example.antek.notes10clouds.decorators.VerticalSpaceItemDecoration;
import com.example.antek.notes10clouds.events.AllItemsDeletedEvent;
import com.example.antek.notes10clouds.events.RemoveItemFromDatabaseEvent;
import com.example.antek.notes10clouds.models.Note;
import com.example.antek.notes10clouds.transitions.DetailsTransition;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainFragment extends android.support.v4.app.Fragment {

    private ArrayList<Note> notes;
    private RecyclerViewAdapter recyclerViewAdapter;
    private Note noteToBeAdded;
    private ItemDetailsFragment itemDetailsFragment;
    private Transition sharedElementTransition;

    private boolean newNote;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;
    @Bind(R.id.help_text)
    TextView helpInfoTextView;

    public static MainFragment newInstance(ArrayList<Note> data, Context context) {
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(context.getResources().getString(R.string.from_activity_to_main_fragment_data), data);
        mainFragment.setArguments(bundle);
        return mainFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        getDataFromBundle();
        removeFirstNoteIfNewNoteAdded();
        setActionBar();
        return view;
    }

    private void getDataFromBundle() {
        newNote = getArguments().getBoolean(getActivity().getResources().getString(R.string.new_note_added_boolean_bundle));
        notes = getArguments().getParcelableArrayList(getActivity().getResources().getString(R.string.from_activity_to_main_fragment_data));
    }

    private void removeFirstNoteIfNewNoteAdded() {
        if (notes != null && notes.size()>0 && newNote) {
            noteToBeAdded = notes.remove(0);
        }
    }

    private void setActionBar() {
        android.support.v7.app.ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            supportActionBar.setDisplayShowHomeEnabled(false);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        removeHelpTextView();
        setRecyclerView();
        setFloatingActionButtonOnClick();
        animateNewNoteAdding();
        super.onViewCreated(view, savedInstanceState);
    }

    private void animateNewNoteAdding() {
        if (newNote && noteToBeAdded!=null) {
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    recyclerView.scrollToPosition(0);
                    notes.add(0, noteToBeAdded);
                    recyclerViewAdapter.notifyItemInserted(0);
                }
            };
            handler.postDelayed(runnable, 200);
            getArguments().putBoolean(getString(R.string.new_note_added_boolean_bundle), false);
        }
    }

    private void setFloatingActionButtonOnClick() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.scrollToPosition(0);
                AddNewNoteFragment addNewNoteFragment = AddNewNoteFragment.newInstance();
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.container, addNewNoteFragment, getString(R.string.add_note_fragment_tag))
                        .addToBackStack(getResources().getString(R.string.back_stack_tag)).commit();
            }
        });
    }

    private void removeHelpTextView() {
        if (notes != null && notes.size() > 1) {
            helpInfoTextView.setVisibility(View.GONE);
        }
    }

    private void setRecyclerView() {
        recyclerViewAdapter = new RecyclerViewAdapter(notes, getActivity()) {
            @Override
            public void onItemClicked(View background, View body, View date, int position) {
                itemDetailsFragment = ItemDetailsFragment.newInstance(notes.get(position), position, getActivity());
                setTransition(itemDetailsFragment);
                getActivity().getSupportFragmentManager().beginTransaction().
                        addSharedElement(body, "body" + position).addSharedElement(date, "date" + position).
                        addSharedElement(background, "background" + position).
                        replace(R.id.container, itemDetailsFragment, getString(R.string.details_fragment_tag))
                        .addToBackStack(getResources().getString(R.string.back_stack_tag)).commit();
            }
        };
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(6));
        registerForContextMenu(recyclerView);
    }

    private void setTransition(ItemDetailsFragment fragment) {
        sharedElementTransition = null;
        if (Build.VERSION.SDK_INT >= 21) {
            sharedElementTransition = new DetailsTransition();
        }
        fragment.setSharedElementEnterTransition(sharedElementTransition);
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = getRecyclerViewAdapter().getPosition();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }
        Note note = notes.remove(position);
        getRecyclerViewAdapter().notifyItemRemoved(position);
        EventBus.getDefault().post(new RemoveItemFromDatabaseEvent(note));
        return super.onContextItemSelected(item);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(AllItemsDeletedEvent event){
        notes.clear();
        getRecyclerViewAdapter().notifyDataSetChanged();
    }

    public RecyclerViewAdapter getRecyclerViewAdapter() {
        return recyclerViewAdapter;
    }
}
