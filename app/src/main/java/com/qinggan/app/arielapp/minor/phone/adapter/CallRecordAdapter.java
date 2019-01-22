package com.qinggan.app.arielapp.minor.phone.adapter;

import android.content.Context;
import android.graphics.Color;
import android.provider.CallLog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.phone.bean.CallRecord;
import com.qinggan.app.arielapp.minor.phone.utils.ChineseToPY;

import java.util.List;

/**
 * Created by pateo on 18-10-31.
 */

public class CallRecordAdapter extends BaseAdapter {

    private List<CallRecord> infos;
    private LayoutInflater inflater;
    int favourSize;
    private int selectItem = -1;

    public CallRecordAdapter(Context context, List<CallRecord> infos, int favourSize) {
        super();
        this.infos = infos;
        this.favourSize = favourSize;
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
        return position;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        if (null == infos || position < 0 || position > getCount()) {
            return true;
        }

        CallRecord item = infos.get(position);
        if (item.getItemType() != CallRecord.ITEM_TYPE_DATA) {
            return false;
        }

        return true;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (null == infos || position < 0 || position > getCount()) {
            return CallRecord.ITEM_TYPE_RECENT;
        }
        CallRecord callRecord = infos.get(position);
        return callRecord.getItemType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CallRecord info = infos.get(position);
        View view;
        ViewHolder viewHolder;
        switch (info.getItemType()) {
            case CallRecord.ITEM_TYPE_FAVOUR:
                if(convertView == null) {
                    view = inflater.inflate(R.layout.call_header_item, null);
                    viewHolder = new ViewHolder();
                    view.setTag(viewHolder);
                } else {
                    view = convertView;
                    viewHolder = (ViewHolder) view.getTag();
                }
                break;
            case CallRecord.ITEM_TYPE_RECENT:
                if(convertView == null) {
                    view = inflater.inflate(R.layout.call_header_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.tvHeader = (TextView) view.findViewById(R.id.tv_call_header_item);
                    view.setTag(viewHolder);
                } else {
                    view = convertView;
                    viewHolder = (ViewHolder) view.getTag();
                }
                viewHolder.tvHeader.setText(R.string.recent_call);
                break;
            default:
                if(convertView == null) {
                    view = inflater.inflate(R.layout.call_record_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.tvName = (TextView) view.findViewById(R.id.tv_contacts_name);
                    viewHolder.contactsImage = (ImageView) view.findViewById(R.id.contacts_image);
                    viewHolder.ivCallType = (ImageView) view.findViewById(R.id.iv_call_type);
                    viewHolder.tvMissCount = (TextView) view.findViewById(R.id.tv_miss_count);
                    view.setTag(viewHolder);
                } else {
                    view = convertView;
                    viewHolder = (ViewHolder) view.getTag();
                }
                if (selectItem == position) {
                    view.setBackgroundResource(R.drawable.phone_listitem_bg_select);
                } else {
                    view.setBackgroundResource(0);
                }

                boolean isFavour = false;
                String firstPY = "";
                if (info.getName() != null && !info.getName().equals("")) {
                    viewHolder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30);
                    viewHolder.tvName.setText(info.getName());
                    firstPY = ChineseToPY.getPinYinFirstLetter(info.getName());
                    if (favourSize > 0 && position < favourSize + 1) {
                        isFavour = true;
                    }

                } else {
                    viewHolder.tvName.setText(info.getNumber());
                    if (info.getNumber().length() > 11) {
                        viewHolder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    } else {
                        viewHolder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30);
                    }
                }
                setCallRecordType(info, viewHolder.ivCallType);
                //set contacts image by pinyin
                int contactsImageRes = ChineseToPY.getPinYinImageResource(isFavour, firstPY);
                viewHolder.contactsImage.setImageResource(contactsImageRes);

                viewHolder.tvMissCount.setText(info.getMissCount() + "");
                if (info.getMissCount() <= 0) {
                    viewHolder.tvMissCount.setVisibility(View.GONE);
                } else {
                    viewHolder.tvMissCount.setVisibility(View.VISIBLE);
                }
                break;
        }
        return view;
    }

    private void setCallRecordType(CallRecord info,ImageView ivCallType) {
        String typeStr = info.getType();
        int type = Integer.valueOf(typeStr);
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE:
                ivCallType.setImageResource(R.drawable.phone_icon_inbuttun);
                break;
            case CallLog.Calls.OUTGOING_TYPE:
                ivCallType.setImageResource(R.drawable.phone_icon_callicon);
                break;

            default:
                break;
        }
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    class ViewHolder{
        TextView tvName;
        ImageView contactsImage;
        ImageView ivCallType;
        TextView tvMissCount;

        TextView tvHeader;
    }
}