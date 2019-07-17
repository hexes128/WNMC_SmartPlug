package com.example.wnmc_smartplug;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class powerdraw extends AppCompatActivity {
    private LineChart chart;
    GlobalVariable gv;
    private List<Integer> year;
    private List<Integer> month;
    private List<Double> date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_powerdraw);
        gv = (GlobalVariable) getApplicationContext();
        year = gv.year;
        month = gv.month;
        date = gv.date;
        chart = findViewById(R.id.chart);
        chart.setNoDataText("請選擇時間");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        for (int i = 0; i < year.size(); i++) {
            menu.add(Menu.NONE, i, Menu.NONE, year.get(i) + "年" + month.get(i) + "月");
        }

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case (0): {
                chooseyearmonth(0);
                break;
            }
            case (1): {
                chooseyearmonth(1);
                break;
            }
            case (2): {
                chooseyearmonth(2);
                break;
            }
            case (3): {
                chooseyearmonth(3);
                break;
            }
            case (4): {
                chooseyearmonth(4);
                break;
            }
            case (5): {
                chooseyearmonth(5);
                break;
            }
            case (6): {
                chooseyearmonth(6);
                break;
            }
            case (7): {
                chooseyearmonth(7);
                break;
            }
            case (8): {
                chooseyearmonth(8);
                break;
            }
            case (9): {
                chooseyearmonth(9);
                break;
            }
            case (10): {
                chooseyearmonth(10);
                break;
            }
            case (11): {
                chooseyearmonth(11);
                break;
            }
            case (12): {
                chooseyearmonth(12);
                break;
            }
            case (13): {
                chooseyearmonth(13);
                break;
            }
            case (14): {
                chooseyearmonth(14);
                break;
            }
            case (15): {
                chooseyearmonth(15);
                break;
            }

        }

        return true;
    }

    public void chooseyearmonth(int index) {
        ArrayList<Entry> yValues = new ArrayList<>();

        for (int i = 31 * index; i <= 31 * (index + 1) - 1; i++) {

            yValues.add(new Entry(i , new Float(date.get(i))));
        }
        LineDataSet set = new LineDataSet(yValues, "Data Set 1");

        set.setFillAlpha(110);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        LineData data = new LineData(dataSets);

        chart.setData(data);

        String[] date = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(date));
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        chart.invalidate();
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {
        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }


        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }
}
