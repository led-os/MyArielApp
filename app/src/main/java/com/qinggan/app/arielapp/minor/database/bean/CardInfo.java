package com.qinggan.app.arielapp.minor.database.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.qinggan.app.arielapp.minor.controller.CardController;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;

import java.util.Arrays;

/**
 * Created by pateo on 18-11-3.
 */

@DatabaseTable(tableName = "card_info")
public class CardInfo extends BasicInfo {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "card_id")
    private String cardId;

    @DatabaseField(columnName = "type")
    private String type;

    @DatabaseField(columnName = "title")
    private String title;

    @DatabaseField(columnName = "sub_title")
    private String subTitle;

    @DatabaseField(columnName = "content")
    private String content;

    @DatabaseField(columnName = "sub_content")
    private String subContent;

    @DatabaseField(columnName = "message")
    private String message;

    @DatabaseField(columnName = "number")
    private int number;

    @DatabaseField(columnName = "data1", columnDefinition = "LONGBLOB", dataType = DataType.BYTE_ARRAY)
    private byte[] data1;//Reserved column , could save file etc.

    @DatabaseField(columnName = "data2")
    private String data2;//Reserved column

    @DatabaseField(columnName = "data3")
    private String data3;//Reserved column

    private int bgImg; //卡片背景
    private int rightIconImg;//右侧icon
    private int leftIconImg;//左侧icon

    private boolean mOnPlay = false;
    private String imageCacheKey;

    public void setImageCacheKey(String key){
        imageCacheKey = key;
    }

    public String getImageCacheKey(){
        return imageCacheKey;
    }

    public void setPlayOn(boolean onPlay){
        mOnPlay = onPlay;
    }

    public boolean getPlayOn(){
        return mOnPlay;
    }

    public CardInfo() {
    }

    protected CardInfo(Parcel in) {
        id = in.readInt();
        cardId = in.readString();
        type = in.readString();
        title = in.readString();
        subTitle = in.readString();
        content = in.readString();
        subContent = in.readString();
        message = in.readString();
        number = in.readInt();
        data1 = in.createByteArray();
        data2 = in.readString();
        data3 = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(cardId);
        dest.writeString(type);
        dest.writeString(title);
        dest.writeString(subTitle);
        dest.writeString(content);
        dest.writeString(subContent);
        dest.writeString(message);
        dest.writeInt(number);
        dest.writeByteArray(data1);
        dest.writeString(data2);
        dest.writeString(data3);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CardInfo> CREATOR = new Creator<CardInfo>() {
        @Override
        public CardInfo createFromParcel(Parcel in) {
            return new CardInfo(in);
        }

        @Override
        public CardInfo[] newArray(int size) {
            return new CardInfo[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubContent() {
        return subContent;
    }

    public void setSubContent(String subContent) {
        this.subContent = subContent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public byte[] getData1() {
        return data1;
    }

    public void setData1(byte[] data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getData3() {
        return data3;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBgImg() {
        return bgImg;
    }

    public void setBgImg(int bgImg) {
        this.bgImg = bgImg;
    }

    public int getRightIconImg() {
        return rightIconImg;
    }

    public void setRightIconImg(int rightIconImg) {
        this.rightIconImg = rightIconImg;
    }

    public int getLeftIconImg() {
        return leftIconImg;
    }

    public void setLeftIconImg(int leftIconImg) {
        this.leftIconImg = leftIconImg;
    }

    @Override
    public String toString() {
        return "CardInfo{" +
                "id=" + id +
                ", cardId='" + cardId + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", content='" + content + '\'' +
                ", subContent='" + subContent + '\'' +
                ", message='" + message + '\'' +
                ", number=" + number +
                ", data2='" + data2 + '\'' +
                ", data3='" + data3 + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o instanceof CardInfo) {
            CardInfo oInfo = (CardInfo)o;
            if (oInfo.getCardId().equalsIgnoreCase(this.getCardId())) {
                return true;
            }
        }

        return false;
    }
}