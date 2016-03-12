package com.lauraeyal.taskmanager.bl;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lauraeyal.taskmanager.MyItemClickListener;
import com.lauraeyal.taskmanager.MyItemLongClickListener;
import com.lauraeyal.taskmanager.R;
import com.lauraeyal.taskmanager.common.User;

import java.util.List;

/**
 * Created by Eyal on 11/7/2015.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
{
    private List<User> users;
    private MyItemClickListener mItemClickListener;
    private MyItemLongClickListener mItemLongClickListener;
    public UserAdapter(List<User> users){
        this.users = users;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.userslist_view_card, parent, false);

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

        User usr = users.get(position);
        holder.mTvDescription.setText(usr.getUserName());
    }

    public void UpdateDataSource(List<User> user)
    {
        if(user ==null) return; //TODO Decide how to deal with it (Maybe an exception??)
        this.users= user;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        //Each item is a view in the card.
        private TextView mTvDescription;
        private MyItemClickListener mListener;
        private MyItemLongClickListener mLongListener;
        public ViewHolder(View parentView, MyItemClickListener mItemClickListener, MyItemLongClickListener mItemLongClickListener) {
            super(parentView);
            mTvDescription = (TextView) parentView.findViewById(R.id.carduser_usermail);
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
