package com.qinggan.app.arielapp.minor.database.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;

import java.util.Date;

/**
 * Created by pateo on 18-11-13.
 */
@DatabaseTable(tableName = "navi_search_history")
public class NaviSearchHistory extends BasicInfo {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "contents")
    private String contents;

    @DatabaseField(columnName = "name")
    private String name;

    @DatabaseField(columnName = "address")
    private String address;

    @DatabaseField(columnName = "poi_lat")
    private String poiLat;

    @DatabaseField(columnName = "poi_lon")
    private String poiLno;

    @DatabaseField(columnName = "type")
    private String type;

    @DatabaseField(columnName = "create_date", dataType = DataType.DATE_STRING)
    private Date createDate;

    @DatabaseField(columnName = "last_modified_date", dataType = DataType.DATE_STRING)
    private Date lastModifiedDate;

    @DatabaseField(columnName = "uid")
    private String uid;

    public NaviSearchHistory() {
    }


    protected NaviSearchHistory(Parcel in) {
        id = in.readInt();
        contents = in.readString();
        name = in.readString();
        address = in.readString();
        poiLat = in.readString();
        poiLno = in.readString();
        type = in.readString();
        createDate = new Date(in.readLong());
        lastModifiedDate = new Date(in.readLong());
        uid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(contents);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(poiLat);
        dest.writeString(poiLno);
        dest.writeString(type);
        dest.writeLong(createDate.getTime());
        dest.writeLong(lastModifiedDate.getTime());
        dest.writeString(uid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NaviSearchHistory> CREATOR = new Creator<NaviSearchHistory>() {
        @Override
        public NaviSearchHistory createFromParcel(Parcel in) {
            return new NaviSearchHistory(in);
        }

        @Override
        public NaviSearchHistory[] newArray(int size) {
            return new NaviSearchHistory[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPoiLat() {
        return poiLat;
    }

    public void setPoiLat(String poiLat) {
        this.poiLat = poiLat;
    }

    public String getPoiLno() {
        return poiLno;
    }

    public void setPoiLno(String poiLno) {
        this.poiLno = poiLno;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "NaviSearchHistory{" +
                "id=" + id +
                ", contents='" + contents + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", poiLat='" + poiLat + '\'' +
                ", poiLno='" + poiLno + '\'' +
                ", type='" + type + '\'' +
                ", createDate=" + createDate +
                ", lastModifiedDate=" + lastModifiedDate +
                ", uid=" + uid +
                '}';
    }

}
