package com.lauraeyal.taskmanager.bl;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lauraeyal.taskmanager.MyItemClickListener;
import com.lauraeyal.taskmanager.MyItemLongClickListener;
import com.lauraeyal.taskmanager.R;
import com.lauraeyal.taskmanager.common.*;

import java.util.List;

/**
 * Created by Eyal on 11/7/2015.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>
{
    private List<TaskItem> taskItems;
    private MyItemClickListener mItemClickListener;
    private MyItemLongClickListener mItemLongClickListener;
    public  TaskAdapter(List<TaskItem> taskItems){
        this.taskItems = taskItems;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tasklist_view_card, parent, false);

        ViewHolder vh = new ViewHolder(v,mItemClickListener,mItemLongClickListener);
        return vh;
    }

    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(MyItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TaskItem item = taskItems.get(position);
        String Duetime =item.GetDueTime();
        String[] separated = Duetime.split("\\s+");
        holder.taskDescription.setText(item.GetDescription());
        holder.taskPriority.setText(item.GetPriority());
        holder.taskLocation.setText(item.GetLocation());
        holder.taskCategory.setText(item.getCategory());
        holder.taskStatus.setText(item.GetTaskStatus());
        holder.taskDate.setText(separated[0]);
        holder.taskeHour.setText(separated[1]);
        if(item.GetTaskApprovle() == -1)
           holder.itemView.findViewById(R.id.tcard_view_layout).setBackgroundColor(Color.RED);
        if (item.GetTaskStatus().equals("Done"))
            holder.itemView.findViewById(R.id.tcard_view_layout).setBackgroundColor(Color.GREEN);
    }

    public void UpdateDataSource(List<TaskItem> items)
    {
        if(items ==null) return; //TODO Decide how to deal with it (Maybe an exception??)
        this.taskItems= items;
    }

    @Override
    public int getItemCount() {
        return taskItems.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        //Each item is a view in the card.
        private TextView taskDescription,taskPriority,taskLocation,taskStatus,taskDate,taskeHour,taskCategory;
        private MyItemClickListener mListener;
        private MyItemLongClickListener mLongListener;

        public ViewHolder(View parentView, MyItemClickListener mItemClickListener, MyItemLongClickListener mItemLongClickListener) {
            super(parentView);
            taskDescription = (TextView) parentView.findViewById(R.id.cardtask_description);
            taskPriority = (TextView) parentView.findViewById(R.id.cardtask_priorty);
            taskLocation = (TextView) parentView.findViewById(R.id.cardtask_location);
            taskStatus = (TextView) parentView.findViewById(R.id.cardtask_status);
            taskDate = (TextView) parentView.findViewById(R.id.cardtask_date);
            taskeHour = (TextView) parentView.findViewById(R.id.cardtask_hour);
            taskCategory = (TextView) parentView.findViewById(R.id.cardtask_category);
            this.mListener = mItemClickListener;
            this.mLongListener = mItemLongClickListener;
            parentView.setOnClickListener(this);
            parentView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onItemClick(v,getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(mLongListener != null){
                mLongListener.onItemLongClick(v, getPosition());
            }
            return true;
        }
    }


}
