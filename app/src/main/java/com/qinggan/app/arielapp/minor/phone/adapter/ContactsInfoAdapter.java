package com.qinggan.app.arielapp.minor.phone.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.phone.bean.ContactsInfo;
import com.qinggan.app.arielapp.minor.phone.utils.CallUtils;
import com.qinggan.app.arielapp.minor.phone.utils.ChineseToPY;

import java.util.List;

/**
 * Created by pateo on 18-10-31.
 */

public class ContactsInfoAdapter extends BaseAdapter {
    private static final String TAG = "ContactsInfoAdapter";
    private List<ContactsInfo> infos;
    private LayoutInflater inflater;
    private boolean shouldShowLetter;
    private int selectedItem = -1;
    private TextView firstVextView;
    AnimationDrawable firstTextVewAnonimation;

    public ContactsInfoAdapter(Context context, List<ContactsInfo> infos, boolean shouldShowLetter) {
        super();
        this.infos = infos;
        this.shouldShowLetter = shouldShowLetter;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (infos == null) {
            return 0;
        }
        return infos.size();
    }

    @Override
    public Object getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = inflater.inflate(R.layout.contacts_info_item, null);
            viewHolder  = new ViewHolder();
            viewHolder.tv_no = (TextView) view.findViewById(R.id.tv_no);
            viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tv_phone_number = (TextView) view.findViewById(R.id.tv_phone_number);
            view.setTag(viewHolder);
        }
        else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
//        View view = inflater.inflate(R.layout.contacts_info_item, null);
        CallUtils.logd(TAG, " there is no contacts ");

        ContactsInfo info = infos.get(position);

        viewHolder.tv_no.setText(position + 1 + "");
        viewHolder.tv_name.setText(info.getDisplayName());
        viewHolder.tv_phone_number.setText(info.getPhoneNum());

        if (this.shouldShowLetter && info.getDisplayName() != null && info.getDisplayName().length() > 0) {
            String firstPY = ChineseToPY.getPinYinFirstLetter(info.getDisplayName());
            viewHolder.tv_no.setBackgroundResource(ChineseToPY.getPinYinImageResource(false, firstPY));
            viewHolder.tv_no.setText("");
        }

        if (position == selectedItem) {
            view.setBackgroundResource(R.drawable.phone_listitem_bg_select);
        } else {
            view.setBackgroundResource(0);
        }

        if (position == 0) {
            firstVextView = viewHolder.tv_no;
        }
        String typeStr = null;
        int color = 0;
        /*switch (info.getType()) {
            case CallLog.Calls.INCOMING_TYPE:
                typeStr = "来电";
                color = Color.BLUE;

                break;
            case CallLog.Calls.OUTGOING_TYPE:
                typeStr = "去电";
                color = Color.GREEN;

                break;
            case CallLog.Calls.MISSED_TYPE:
                typeStr = "未接";
                color = Color.RED;

                break;

            default:
                break;
        }
        tv_type.setText(typeStr);
        tv_type.setTextColor(color);*/
        return view;
    }

    public void startDialLoading() {
        firstVextView.setBackgroundResource(R.drawable.dial_timeout_loading);
        firstTextVewAnonimation = (AnimationDrawable)firstVextView.getBackground();
        firstTextVewAnonimation.start();
    }

    private void stopDialLoaing() {
        if (firstTextVewAnonimation != null) {
            firstTextVewAnonimation.stop();
        }
    }

    public void setSelectedItem(int selectItem) {
        this.selectedItem = selectItem;
    }

    class ViewHolder{
        TextView tv_no;
        TextView tv_name;
        TextView tv_phone_number;
    }
}