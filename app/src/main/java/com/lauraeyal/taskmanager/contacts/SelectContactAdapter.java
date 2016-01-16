package com.lauraeyal.taskmanager.contacts;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lauraeyal.taskmanager.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Eyal on 12/26/2015.
 */
public class SelectContactAdapter extends BaseAdapter {

    public List<SelectContact> _data;
    private ArrayList<SelectContact> arraylist;
    Context _c;
    ViewHolder v;
    RoundImage roundedImage;

    public SelectContactAdapter(List<SelectContact> selectContacts, Context context) {
        _data = selectContacts;
        _c = context;
        this.arraylist = new ArrayList<SelectContact>();
        this.arraylist.addAll(_data);
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int i) {
        return _data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.contact_info, null);
            Log.e("Inside", "here--------------------------- In view1");
        } else {
            view = convertView;
            Log.e("Inside", "here--------------------------- In view2");
        }

        v = new ViewHolder();

        v.title = (TextView) view.findViewById(R.id.name);
        v.check = (CheckBox) view.findViewById(R.id.check);
        v.phone = (TextView) view.findViewById(R.id.no);
       // v.imageView = (ImageView) view.findViewById(R.id.pic);
        v.email = (TextView) view.findViewById(R.id.mail);
       // v.doneBtn = (Button) view.findViewById(R.id.doneBtn);

        final SelectContact data = (SelectContact) _data.get(i);
        v.title.setText(data.getName());
        v.check.setChecked(data.getCheckedBox());
        v.phone.setText(data.getPhone());
        v.email.setText(data.getEmail());
        // Set image if exists
       /* try {

            if (data.getThumb() != null) {
                v.imageView.setImageBitmap(data.getThumb());
            } else {
                //.imageView.setImageResource(R.drawable.image);
            }
            // Seting round image
            //itmap bm = BitmapFactory.decodeResource(view.getResources(), R.drawable.image); // Load default image
            //roundedImage = new RoundImage(bm);
            v.imageView.setImageDrawable(roundedImage);
        } catch (OutOfMemoryError e) {
            // Add default picture
            // v.imageView.setImageDrawable(this._c.getDrawable(R.drawable.image));
            e.printStackTrace();
        }

        Log.e("Image Thumb", "--------------" + data.getThumb());
*/
        // Set check box listener android
        v.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    data.setCheckedBox(true);
                } else {
                    data.setCheckedBox(false);
                }
            }
        });

        view.setTag(data);
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        _data.clear();
        if (charText.length() == 0) {
            _data.addAll(arraylist);
        } else {
            for (SelectContact wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    _data.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
    static class ViewHolder {
       // ImageView imageView;
        TextView title, phone,email;
        CheckBox check;

       // Button doneBtn;
    }
}