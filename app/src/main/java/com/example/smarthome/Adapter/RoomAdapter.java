package com.example.smarthome.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.R;
import com.example.smarthome.model.Room;

import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    //current list of rooms of an user
    private ArrayList<Room> roomList = new ArrayList<>();

    public RoomAdapter(ArrayList<Room> roomList) {
        this.roomList = roomList;
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        //Textviews of an room
        public TextView roomName;
        public TextView roomEmail;
        public TextView roomCategory;

        //assigning the Textviews
        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.RoomItemName);
            roomEmail = itemView.findViewById(R.id.RoomItemOwner);
            roomCategory = itemView.findViewById(R.id.RoomItemCategory);
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
        RoomViewHolder roomViewHolder = new RoomViewHolder(view);
        return roomViewHolder;
    }

    /**
     * For each Room in the roomList at a certain position the information will be displayed
     * @param holder    Roomviewholder
     * @param position  Position of a room in the arraylist
     */
    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room currenRoom = roomList.get(position);

        holder.roomName.setText(currenRoom.getName());
        holder.roomEmail.setText(currenRoom.getOwner());
        holder.roomCategory.setText(currenRoom.getRoomCategory().toString());
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
