package com.example.smarthome.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.R;
import com.example.smarthome.model.Room;

import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    //current list of rooms of an user
    private ArrayList<Room> roomList = new ArrayList<>();
    //OnItemClicklistener to handle click events on the cards
    private OnItemClickListener itemListener;

    //handle these click events
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    //set the click listener
    public void setOnItemClickListener(OnItemClickListener listener){
        itemListener = listener;
    }

    //constructor
    public RoomAdapter(ArrayList<Room> roomList) {
        this.roomList = roomList;
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        //Textviews of an room
        public TextView roomName;
        public TextView roomEmail;
        public TextView roomCategory;
        public ImageView roomDashboard;

        //assigning the Textviews
        public RoomViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            roomName = itemView.findViewById(R.id.RoomItemName);
            roomEmail = itemView.findViewById(R.id.RoomItemOwner);
            roomCategory = itemView.findViewById(R.id.RoomItemCategory);

            roomDashboard = itemView.findViewById(R.id.RoomItemDashboard);

            //set clickevent on the dashboard icon
            roomDashboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    /**
     * creating the view to display the roomitem
     * @param parent current View of the mainactivity
     * @param viewType
     * @return Viewholder
     */
    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_item, parent, false);
        RoomViewHolder roomViewHolder = new RoomViewHolder(view, itemListener);
        return roomViewHolder;
    }

    /**
     * For each Room in the roomList at a certain position the information will be displayed
     * @param holder    Roomviewholder
     * @param position  Position of a room in the ArrayList
     */
    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room currentRoom = roomList.get(position);

        holder.roomName.setText(currentRoom.getName());
        holder.roomEmail.setText(currentRoom.getOwner());
        holder.roomCategory.setText(currentRoom.getRoomCategory().toString());
    }

    /**
     * amount of rooms in the list
     * @return size
     */
    @Override
    public int getItemCount() {
        return roomList.size();
    }

    /**
     * Updateds the current list
     * @param currentRoomList list that is provided to be update
     */
    public void updateRoomList(ArrayList<Room> currentRoomList){
        roomList = currentRoomList;
        notifyDataSetChanged();
    }
}
