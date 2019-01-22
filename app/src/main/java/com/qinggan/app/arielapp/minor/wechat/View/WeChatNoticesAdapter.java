package com.qinggan.app.arielapp.minor.wechat.View;


import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.wechat.NotificationBean;

import java.util.List;

public class WeChatNoticesAdapter extends RecyclerView.Adapter<WeChatNoticesAdapter.NoticesViewHolder> {
    private String TAG = WeChatNoticesAdapter.class.getSimpleName();
    private Context mContext;
    private List<NotificationBean> mNotiList;
    private TextWatcher mWatcher;
    private boolean isLastMsg;
    private InputMethodManager imm;

    public WeChatNoticesAdapter(Context context, List<NotificationBean> notiList) {
        this.mContext = context;
        mNotiList = notiList;
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public WeChatNoticesAdapter(Context context, List<NotificationBean> notiList, TextWatcher watcher) {
        this.mContext = context;
        mNotiList = notiList;
        mWatcher = watcher;
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public NoticesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.wechat_notices_item, parent, false);
        NoticesViewHolder holder = new NoticesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final NoticesViewHolder holder, int position) {
        System.out.println("---alvin----position--" + position);
        boolean isReply = mNotiList.get(position).isRelpy();
        holder.mMsgTxt.setVisibility(isReply ? View.GONE : View.VISIBLE);
        holder.mRemindTxt.setVisibility(isReply ? View.GONE : View.VISIBLE);
        holder.mReplyMsgTxt.setVisibility(isReply ? View.VISIBLE : View.GONE);

        holder.mContactsTxt.setText(mNotiList.get(position).getSender());
        System.out.println("---alvin----isReply--" + isReply);
        if (isReply) {
            holder.mContactsTxt.setText(mNotiList.get(position).getSender());
            holder.mReplyMsgTxt.addTextChangedListener(mWatcher);
            Log.d(TAG, "input " + mNotiList.get(position).getInput());
            if (mNotiList.get(position).getInput() != null) {
                holder.mReplyMsgTxt.setText(mNotiList.get(position).getInput());
            } else{
                holder.mReplyMsgTxt.setText("");
            }
        } else {
            holder.mMsgTxt.setText(mNotiList.get(position).getMsg());
            holder.mRemindTxt.setText(mNotiList.size() > 1 ? R.string.wechat_incomes_remind_str : R.string.wechat_income_remind_str);
            holder.mReplyMsgTxt.removeTextChangedListener(mWatcher);
            holder.mReplyMsgTxt.setText("");
        }

        if (isLastMsg) {
            holder.mRemindTxt.setText(R.string.wechat_income_remind_str);
        }
    }

    @Override
    public int getItemCount() {
        return mNotiList.size();
    }

    public void addNotice(NotificationBean bean) {
        if (bean != null) {
            mNotiList.add(bean);
        }
    }

    public void remove(int index) {
        mNotiList.remove(index);
    }

    /**
     * 这个是用来控制当滑动删除一条消息时，更新语音提醒语句
     * 判断是否是最后一条语句，来控制显示的子串
     * @param lastMsg
     */
    public void setLastMsg(boolean lastMsg) {
        isLastMsg = lastMsg;
    }

    public class NoticesViewHolder extends RecyclerView.ViewHolder {
        TextView mContactsTxt;
        TextView mMsgTxt;
        TextView mRemindTxt;
        EditText mReplyMsgTxt;

        NoticesViewHolder(View itemView) {
            super(itemView);
            mContactsTxt = (TextView) itemView.findViewById(R.id.income_msg_contact_txt);
            mMsgTxt = (TextView) itemView.findViewById(R.id.income_msg_txt);
            mRemindTxt = (TextView) itemView.findViewById(R.id.reply_msg_remind_txt);
            mReplyMsgTxt = (EditText) itemView.findViewById(R.id.reply_msg_txt);
        }
    }
}
