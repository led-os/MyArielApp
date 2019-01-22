package com.qinggan.app.arielapp.minor.database.dao.common;

import java.util.List;

/**
 * Created by pateo on 18-11-13.
 */

public class QueryBuildInfo {
    private boolean isDistinct;
    private String groupBy;
    private String orderBy;

    private long limit;
    private long offset;

    private boolean isAsec;

    private List<QueryItem> queryItemList;

    public boolean isDistinct() {
        return isDistinct;
    }

    public void setDistinct(boolean distinct) {
        isDistinct = distinct;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public boolean isAsec() {
        return isAsec;
    }

    public void setAsec(boolean asec) {
        isAsec = asec;
    }

    public List<QueryItem> getQueryItemList() {
        return queryItemList;
    }

    public void setQueryItemList(List<QueryItem> queryItemList) {
        this.queryItemList = queryItemList;
    }

    @Override
    public String toString() {

        String str = "QueryBuildInfo{" +
                "isDistinct=" + isDistinct +
                ", groupBy='" + groupBy + '\'' +
                ", orderBy='" + orderBy + '\'' +
                ", limit=" + limit +
                ", offset=" + offset +
                ", isAsec=" + isAsec;
        for (QueryItem item : queryItemList) {
            str = str + ", QueryItem:" + item;
        }
        str = str + '}';
        return str;
    }
}
