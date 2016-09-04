package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;


public class DetailedActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    String currency = "";
    public static String Tag="DetailedActivity";
    public static final String TAG_STOCK_SYMBOL = "STOCK_SYMBOL";
    private static final int STOCKS_LOADER = 1;
    public LineChart chart;
    public TextView Bidprice,Change,Pchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        chart = (LineChart) findViewById(R.id.chart);
        Bidprice=(TextView)findViewById(R.id.Bidprice);
        Change = (TextView)findViewById(R.id.Change);
        Pchange=(TextView)findViewById(R.id.Percentagechange);

        currency = getIntent().getStringExtra(TAG_STOCK_SYMBOL);
        setTitle(currency);
        Log.d(TAG_STOCK_SYMBOL, currency);


        getSupportLoaderManager().initLoader(STOCKS_LOADER, null, this);

        CursorLoader cursorLoader=new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CREATED,QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.SYMBOL + " = ?",
                new String[]{currency},
                null);
        Cursor cursor=cursorLoader.loadInBackground();
        cursor.moveToLast();
        Log.e(Tag,cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL)));
        Log.e(Tag,cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE)));
        Log.e(Tag,cursor.getString(cursor.getColumnIndex(QuoteColumns.CHANGE)));
        Log.e(Tag,cursor.getString(cursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));


        Bidprice.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE)));
        Change.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.CHANGE)));
        Pchange.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));
        cursor.close();

    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case STOCKS_LOADER:
                return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP,QuoteColumns.CREATED},
                        QuoteColumns.SYMBOL + " = ?",
                        new String[]{currency},
                        null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //xaxis
        ArrayList<String> xAxis = new ArrayList<>();


        //dataset
        ArrayList<LineDataSet> dataSets = new ArrayList<>();

        Cursor Data=data;
        ArrayList<Entry> valueSet1 = new ArrayList<>();
        data.moveToNext();


        Log.d(TAG_STOCK_SYMBOL+" is current",data.getCount()+"");

        int k=0;
        for(Data.moveToLast();!Data.isBeforeFirst()&&k<10;Data.moveToPrevious(),k++) {

            Entry one=new Entry( Float.parseFloat(Data.getString(Data.getColumnIndex(QuoteColumns.BIDPRICE))),Data.getPosition());
            valueSet1.add(one);
            Log.e(TAG_STOCK_SYMBOL,Data.getString(Data.getColumnIndex(QuoteColumns.BIDPRICE)));
        }
        ArrayList<Entry> values=new ArrayList<>();
        for(int i=valueSet1.size()-1;i>=0;i--){
            values.add(valueSet1.get(i));
            xAxis.add("");

            Log.e(Tag,"in pos "+i );
        }

        LineDataSet barDataSet1 = new LineDataSet(values, currency);
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        dataSets.add(barDataSet1);


        LineData data1 = new LineData(xAxis, dataSets);

        chart.setData(data1);
        chart.setDescription("My Chart");
        chart.animateXY(2000, 3000);
        chart.invalidate();
        Data.close();


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
