package com.qinggan.app.arielapp.minor.database.dao.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pateo on 18-11-15.
 */

public class QueryItem {

    private String columnName;
    private String seachKey;
    //where condition is "or/and",default is and
    private boolean isAnd = true;
    //has been defined in conditionSymbolSupportList , only support eq/ne/gt/lt/ge/le/like,default is eq
    private String conditionSymbol = "eq";

    private List<String> conditionSymbolSupportList = Arrays.asList("eq", "ne", "gt", "lt", "ge", "le", "like");

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getSeachKey() {
        return seachKey;
    }

    public void setSeachKey(String seachKey) {
        this.seachKey = seachKey;
    }

    public boolean isAnd() {
        return isAnd;
    }

    public void setAnd(boolean and) {
        isAnd = and;
    }

    public String getConditionSymbol() {
        return conditionSymbol;
    }

    public void setConditionSymbol(String conditionSymbol) {
        if (conditionSymbolSupportList.contains(conditionSymbol)) {
            this.conditionSymbol = conditionSymbol;
        }
    }

    @Override
    public String toString() {
        return "QueryItem{" +
                "columnName='" + columnName + '\'' +
                ", seachKey='" + seachKey + '\'' +
                ", isAnd='" + isAnd + '\'' +
                ", conditionSymbol=" + conditionSymbol +
                '}';
    }

    public boolean isAvailable() {
        if ((columnName == null || columnName.equals("")) || (conditionSymbol == null || conditionSymbol.equals(""))) {
            return false;
        }
        return true;
    }
}
