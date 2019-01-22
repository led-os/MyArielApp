package com.qinggan.app.arielapp.minor.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pateo on 18-12-13.
 */

public class ListUtils {

    public static <T> List<T> filter(List<T> list, ListFilterHook<T> hook) {
        ArrayList<T> result = new ArrayList<T>();
        for (T object : list) {
            if (hook.compare(object)) {
                result.add(object);
            }
        }
        result.trimToSize();
        return result;
    }
}
