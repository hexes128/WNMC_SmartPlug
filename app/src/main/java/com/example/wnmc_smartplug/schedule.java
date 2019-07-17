package com.example.wnmc_smartplug;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.List;

public class schedule extends AppCompatActivity {

    private RecyclerView schedulerecycleview;
    GlobalVariable gv;
    final Context context = this;
    private MyAdapter scheduleadapter;
    private BroadcastReceiver br;
    private IntentFilter filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        gv = (GlobalVariable) getApplicationContext();
        schedulerecycleview = findViewById(R.id.recyclerView);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        schedulerecycleview.setLayoutManager(layoutManager);
        scheduleadapter = new MyAdapter(gv.schedulex);
        schedulerecycleview.setAdapter(scheduleadapter);

        br = new MyBroadcastReceiver();
        filter = new IntentFilter(MainActivity.scheduletest);

        this.registerReceiver(br, filter);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(br);
        super.onDestroy();
    }


    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("有收到", "flag");
            scheduleadapter.notifyDataSetChanged();

        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> mData;

        public MyAdapter(List<String> data) {
            mData = data;

        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.scheduleX.setText(mData.get(position));
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Spinner chooseday;
                    final Spinner choosehour;
                    final Spinner choosemin;
                    final ToggleButton setonoff;
                    Button confirm;
                    Button cancel;
                    ArrayAdapter<CharSequence> day = ArrayAdapter.createFromResource(
                            getApplicationContext(), R.array.day, android.R.layout.simple_spinner_item);
                    ArrayAdapter<CharSequence> hour = ArrayAdapter.createFromResource(
                            getApplicationContext(), R.array.hour, android.R.layout.simple_spinner_item);
                    ArrayAdapter<CharSequence> min = ArrayAdapter.createFromResource(
                            getApplicationContext(), R.array.min, android.R.layout.simple_spinner_item);
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.usersetschedule);
                    chooseday = dialog.findViewById(R.id.day);
                    choosemin = dialog.findViewById(R.id.min);
                    choosehour = dialog.findViewById(R.id.hour);
                    confirm = dialog.findViewById(R.id.confirm);
                    cancel = dialog.findViewById(R.id.cancel);
                    setonoff = dialog.findViewById(R.id.onoff);
                    chooseday.setAdapter(day);
                    choosehour.setAdapter(hour);
                    choosemin.setAdapter(min);
                    dialog.show();
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            byte[] scheduledata = {-2, 0x07, (byte) position, (byte) choosemin.getSelectedItemPosition(), (byte) choosehour.getSelectedItemPosition(),
                                    (byte) (chooseday.getSelectedItemPosition() + 1), (byte) (setonoff.isChecked() ? 1 : 0), 0, 1};
                            gv.schedulebyte = scheduledata;
                            Log.e("完整byte", "" + scheduledata[0] + " " + scheduledata[1] + " " + scheduledata[2] + " " + scheduledata[3] + " " + scheduledata[4] + " " + scheduledata[5] + " " + scheduledata[6] + " " + scheduledata[7] + " " + scheduledata[8]);
                            gv.schedulesetflag = true;
                            dialog.dismiss();
                            mData.set(position, "day " + (chooseday.getSelectedItemPosition() + 1) + " " + choosehour.getSelectedItemPosition() + "時" + " " + choosemin.getSelectedItemPosition() + "分 " + (setonoff.isChecked() ? "ON" : "OFF"));
                        }

                    });


                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mData.get(position).equals("無排程")) {
                        return;
                    }
                    byte[] deleteschedule = {-2, 0x07, (byte) position, 0, 0, 0, 0, 0, 0};
                    gv.schedulebyte = deleteschedule;
                    gv.schedulesetflag = true;
                    mData.set(position, "無排程");

                }
            });


        }

        @Override
        public int getItemCount() {
            return mData.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView scheduleX;
            private ImageView edit;
            private ImageView delete;

            public ViewHolder(View v) {
                super(v);
                edit = v.findViewById(R.id.edit);
                scheduleX = v.findViewById(R.id.scheculeX);
                delete = v.findViewById(R.id.delete);
            }
        }


    }


}
