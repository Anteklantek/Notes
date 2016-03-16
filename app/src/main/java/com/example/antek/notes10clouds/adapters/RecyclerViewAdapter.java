package com.example.antek.notes10clouds.adapters;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.antek.notes10clouds.R;
import com.example.antek.notes10clouds.models.Note;

import java.util.ArrayList;


public abstract class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Note> data;
    private LayoutInflater inflater;
    private int position;

    public RecyclerViewAdapter(ArrayList<Note> data, Context context) {
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_view_cell, parent, false);
        return new ViewHolder(view, new ViewHolder.myViewHolderClicks() {

            @Override
            public void onCell(View background,View body, View date, int position) {
                onItemClicked(background,body, date, position);
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
       holder.body.setText(data.get(position).getBody());
        holder.date.setText(data.get(position).getDate().toString());
        ViewCompat.setTransitionName(holder.body, "body" + position);
        ViewCompat.setTransitionName(holder.date,"date"+position);
        ViewCompat.setTransitionName(holder.cellContainer,"background"+position);
        holder.cellContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.cellContainer.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    public abstract void onItemClicked(View background,View body,View date,int position);

    @Override
    public int getItemCount() {
        return data.size();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {
        TextView body;
        TextView date;
        LinearLayout cellContainer;
        public myViewHolderClicks listener;

        public ViewHolder(View itemView, myViewHolderClicks listener) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            body = (TextView) itemView.findViewById(R.id.body);
            cellContainer = (LinearLayout) itemView.findViewById(R.id.cell_container);
            body.setMaxLines(1);
            this.listener = listener;
            cellContainer.setOnClickListener(this);
            cellContainer.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onCell(cellContainer,date,body, this.getAdapterPosition());
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(Menu.NONE, view.getId(),
                    Menu.NONE, "Delete");
        }

        public interface myViewHolderClicks {
            void onCell(View background,View body, View date, int position);
        }
    }
}
