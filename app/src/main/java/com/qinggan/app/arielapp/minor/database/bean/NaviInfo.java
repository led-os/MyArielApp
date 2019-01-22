package com.qinggan.app.arielapp.minor.database.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;
import com.qinggan.app.arielapp.minor.main.utils.MapUtils;

import java.util.Date;

/**
 * Created by pateo on 18-11-13.
 */
@DatabaseTable(tableName = "navi_info")
public class NaviInfo extends BasicInfo {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "name")
    private String name;

    @DatabaseField(columnName = "display_name")
    private String displayName;

    @DatabaseField(columnName = "address")
    private String address;

    @DatabaseField(columnName = "poi_lat")
    private String poiLat;

    @DatabaseField(columnName = "poi_lon")
    private String poiLno;

    @DatabaseField(columnName = "create_date", dataType = DataType.DATE_STRING)
    private Date createDate;

    @DatabaseField(columnName = "last_modified_date", dataType = DataType.DATE_STRING)
    private Date lastModifiedDate;

    @DatabaseField(columnName = "is_preset")
    private String isPreset;

    @DatabaseField(columnName = "is_favour")
    private String isFavour;

    @DatabaseField(columnName = "address_type")
    private String addressType;

    @DatabaseField(columnName = "sid")
    private String sid;

    @DatabaseField(columnName = "uid")
    private String uid;

    @DatabaseField(columnName = "sync_flag")
    private int syncFlag = MapUtils.NAVIINFO_SYNC_FLAG_NORMAL;

    public NaviInfo() {
    }

    protected NaviInfo(Parcel in) {
        id = in.readInt();
        name = in.readString();
        displayName = in.readString();
        address = in.readString();
        poiLat = in.readString();
        poiLno = in.readString();
        createDate = new Date(in.readLong());
        lastModifiedDate = new Date(in.readLong());
        isPreset = in.readString();
        isFavour = in.readString();
        addressType = in.readString();
        sid = in.readString();
        uid = in.readString();
        syncFlag = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(displayName);
        dest.writeString(address);
        dest.writeString(poiLat);
        dest.writeString(poiLno);
        dest.writeLong(createDate.getTime());
        dest.writeLong(lastModifiedDate.getTime());
        dest.writeString(isPreset);
        dest.writeString(isFavour);
        dest.writeString(addressType);
        dest.writeString(sid);
        dest.writeString(uid);
        dest.writeInt(syncFlag);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NaviInfo> CREATOR = new Creator<NaviInfo>() {
        @Override
        public NaviInfo createFromParcel(Parcel in) {
            return new NaviInfo(in);
        }

        @Override
        public NaviInfo[] newArray(int size) {
            return new NaviInfo[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getIsPreset() {
        return isPreset;
    }

    public void setIsPreset(String isPreset) {
        this.isPreset = isPreset;
    }

    public String getIsFavour() {
        return isFavour;
    }

    public void setIsFavour(String isFavour) {
        this.isFavour = isFavour;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getSyncFlag() {
        return syncFlag;
    }

    public void setSyncFlag(int syncFlag) {
        this.syncFlag = syncFlag;
    }

    @Override
    public String toString() {
        return "NaviInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", address='" + address + '\'' +
                ", poiLat='" + poiLat + '\'' +
                ", poiLno='" + poiLno + '\'' +
                ", createDate=" + createDate +
                ", lastModifiedDate=" + lastModifiedDate +
                ", isPreset='" + isPreset + '\'' +
                ", isFavour='" + isFavour + '\'' +
                ", addressType='" + addressType + '\'' +
                ", sid='" + sid + '\'' +
                ", uid='" + uid + '\'' +
                ", syncFlag='" + syncFlag + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NaviInfo naviInfo = (NaviInfo) o;

        if (name != null ? !name.equals(naviInfo.name) : naviInfo.name != null) return false;
        if (displayName != null ? !displayName.equals(naviInfo.displayName) : naviInfo.displayName != null)
            return false;
        return uid != null ? uid.equals(naviInfo.uid) : naviInfo.uid == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        return result;
    }
}
