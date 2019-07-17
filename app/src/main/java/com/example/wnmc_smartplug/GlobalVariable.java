package com.example.wnmc_smartplug;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import java.util.List;

public class GlobalVariable extends Application {




    public List<BluetoothDevice> mBluetoothDeviceListByUserselect;

    public DeviceList.MyAdapter mAdapterByUserselect;
    public boolean selectedflag=false;
    public boolean connectflag=false;

   public List<Integer> year;
   public List<Integer> month;
   public List<Double> date;
   public List<String>schedulex;
   public boolean schedulesetflag;
   public String schedulesetOK;

public byte[] schedulebyte;

}
