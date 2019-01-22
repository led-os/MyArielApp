package com.qinggan.app.arielapp.minor.phone.bean;

import com.qinggan.app.arielapp.minor.phone.utils.CallUtils;

/**
 * Created by pateo on 18-10-30.
 */

public class CallRecord {

    public static final int ITEM_TYPE_DATA = 0;
    public static final int ITEM_TYPE_FAVOUR = 1;
    public static final int ITEM_TYPE_RECENT = 2;

    private long date;
    private String formatted_number;
    private String matched_number;
    private String name;
    private String type;
    private String location;
    private long duration;
    private String number;
    private int missCount;
    private int count;

    private int itemType = ITEM_TYPE_DATA;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getFormatted_number() {
        return formatted_number;
    }

    public void setFormatted_number(String formatted_number) {
        this.formatted_number = formatted_number;
    }

    public String getMatched_number() {
        return matched_number;
    }

    public void setMatched_number(String matched_number) {
        this.matched_number = matched_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getMissCount() {
        return missCount;
    }

    public void setMissCount(int missCount) {
        this.missCount = missCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public String toString() {
        return "CallRecord{" +
                "date=" + date +
                ", formatted_number='" + formatted_number + '\'' +
                ", matched_number='" + matched_number + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                ", duration=" + duration +
                ", number='" + number + '\'' +
                ", missCount=" + missCount +
                ", count=" + count +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallRecord that = (CallRecord) o;

        return number.equals(that.number);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }
}
