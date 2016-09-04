package com.sam_chordas.android.stockhawk.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;



/**
 * Implementation of App Widget functionality.
 */
public class Stockwidget extends AppWidgetProvider {
    String Tag_widget="StockWidget";
    public static String WIDGET_BUTTON1 = "android.appwidget.action.WIDGET_BUTTON1";
    public static String WIDGET_BUTTON2 = "android.appwidget.action.WIDGET_BUTTON2";
    public static int[] appwidid;
    public int id;
    public static AppWidgetManager widgetManager;
    public static int pos=1;
    public int length;


    @Override
    public void onUpdate(Context context, AppWidgetManager
            appWidgetManager,int[] appWidgetIds) {

        appwidid=appWidgetIds;
        widgetManager=appWidgetManager;
         int N = appWidgetIds.length;
        Log.e(Tag_widget,"initiated");
        for (int i = 0; i < N; ++i) {
            RemoteViews remoteViews = updateWidgetView(context,
                    appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i],
                    remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetView(Context context,
                                             int appWidgetId) {
        Log.e(Tag_widget,"initiated "+pos);
        id=appWidgetId;


        android.support.v4.content.CursorLoader cursorLoader=new android.support.v4.content.CursorLoader(context, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CREATED,QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns._ID +" = ?",
                new String[]{""+pos},
                null);
        Cursor cursor =cursorLoader.loadInBackground();

        cursor.moveToFirst();
        RemoteViews remoteViews = new RemoteViews(
                context.getPackageName(),R.layout.stockwidget);
        remoteViews.setTextViewText(R.id.Widget_Head,cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL)));
        remoteViews.setTextViewText(R.id.Widget_Bidprice,cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE)));
        remoteViews.setTextViewText(R.id.Widget_change,cursor.getString(cursor.getColumnIndex(QuoteColumns.CHANGE)));
        remoteViews.setTextViewText(R.id.Widget_Pchange,cursor.getString(cursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));


        Intent intent_but1 = new Intent(WIDGET_BUTTON1);
        intent_but1.putExtra("cursor+pos",pos);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent_but1, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.Wid_button1, pendingIntent );


        Intent intent_but2 = new Intent(WIDGET_BUTTON2);
        intent_but1.putExtra("cursor+pos",pos);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent_but2, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.Wid_button2, pendingIntent1 );
        cursor.close();


        return remoteViews;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        android.support.v4.content.CursorLoader cursorLoader=new android.support.v4.content.CursorLoader(context, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CREATED,QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT +" = ?",
                new String[]{"1"},
                null);
        Cursor cursor =cursorLoader.loadInBackground();
        length=cursor.getCount();
        cursor.close();
        Log.e(Tag_widget,length+"");

        if (WIDGET_BUTTON2.equals(intent.getAction())) {
            if(pos<length){
                pos++;

            }
            onUpdate(context,widgetManager,appwidid);

        }
        if (WIDGET_BUTTON1.equals(intent.getAction())) {

            if(pos>=2){
                pos--;
            }
            onUpdate(context,widgetManager,appwidid);
        }
    }

}

