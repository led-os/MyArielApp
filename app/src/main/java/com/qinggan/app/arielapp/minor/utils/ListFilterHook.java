package com.qinggan.app.arielapp.minor.utils;

/**
 * Created by pateo on 18-12-13.
 */

public interface ListFilterHook<T> {
    public boolean compare(T t);
}
