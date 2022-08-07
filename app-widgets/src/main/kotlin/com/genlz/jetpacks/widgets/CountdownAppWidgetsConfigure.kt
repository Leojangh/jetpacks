package com.genlz.jetpacks.widgets

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

class CountdownAppWidgetsConfigure : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        // step 1
//        val appWidgetId = intent?.extras?.getInt(
//            AppWidgetManager.EXTRA_APPWIDGET_ID,
//            AppWidgetManager.INVALID_APPWIDGET_ID
//        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // step 2:do configure
        // TODO

//        // step 3
//        val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(this)
//        // step 4
//        RemoteViews(packageName, R.layout.countdown_app_widgets).run {
//            appWidgetManager.updateAppWidget(appWidgetId, this)
//        }
//
//        // step 5
//        setResult(RESULT_OK, Intent().apply {
//            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
//        })
//        finish()
    }
}