package com.qinggan.app.arielapp.minor.core;

import java.util.ArrayList;

public interface FMStatusListener {

    public void onFrequencyListResponse(String s);

    public void onCurrentBandResponse(int band);

    public void onCurrentFrequencyResponse(float frequency);

    public void onRadioStatusResponse(int status);

    public void onFmFrequencyListResponse(ArrayList<String> fmList);

    public void onAmFrequencyListResponse(ArrayList<String> amList);
}
