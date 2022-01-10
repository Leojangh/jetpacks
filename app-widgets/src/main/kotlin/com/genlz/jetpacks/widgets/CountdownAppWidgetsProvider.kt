package com.genlz.jetpacks.widgets

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.genlz.jetpacks.R
import com.genlz.share.util.appcompat.intent

/**
 * A broadcast receiver but named provider...
 * See follow files:
 *  * [CountdownAppWidgetsConfigure]
 *
 *  A configure activity
 *  * [layout/countdown_app_widgets]
 *
 *  The remote view used by our app widgets
 *  * [xml/countdown_widgets_info]
 *
 *  The app widgets info defined with xml.
 *
 */
class CountdownAppWidgetsProvider : AppWidgetProvider() {

    @SuppressLint("WrongConstant")
    override fun onUpdate(
        context: Context,//android.app.ReceiverRestrictedContext
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        Log.d(TAG, "onUpdate: ")
        context.startService(context.intent<CountdownService>())

        for (appWidgetId in appWidgetIds) {
            val pi = PendingIntent.getActivity(
                context,
                0,
                Intent().apply {
                    component = ComponentName(context, "com.genlz.jetpacks.ui.MainActivity")
                },
                PendingIntent.FLAG_ONE_SHOT or /*FLAG_IMMUTABLE*/0x4000000)//Because the flag is since API 23.
            val view = RemoteViews(context.packageName, R.layout.countdown_app_widgets).apply {
                setTextViewText(R.id.seconds_digit2, "8")
                setOnClickPendingIntent(R.id.plane, pi)
            }
            appWidgetManager.updateAppWidget(appWidgetId, view)
        }
    }

    companion object {
        private const val TAG = "MyAppWidgetsProvider"
    }
}