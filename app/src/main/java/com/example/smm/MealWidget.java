package com.example.smm;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MealWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            @SuppressLint("RemoteViewLayout")
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            String today = new SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(new Date());
            String scCode = "B10";
            String schoolCode = "7010537";
            String mealApiUrl = "https://open.neis.go.kr/hub/mealServiceDietInfo?Type=json&ATPT_OFCDC_SC_CODE="
                    + scCode + "&SD_SCHUL_CODE=" + schoolCode + "&MLSV_FROM_YMD=" + today + "&MLSV_TO_YMD=" + today;
            new Thread(() -> {
                String mealInfo = fetchMealData(mealApiUrl);
                views.setTextViewText(R.id.titleText, "[" + today.substring(4, 6) + "/" + today.substring(6, 8) + "] 급식");
                views.setTextViewText(R.id.widgetText, mealInfo);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }).start();
        }
    }

    private String fetchMealData(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            JSONObject response = new JSONObject(result.toString());
            JSONArray mealArray = response.getJSONArray("mealServiceDietInfo").getJSONObject(1).getJSONArray("row");
            String meal = mealArray.getJSONObject(0).getString("DDISH_NM");
            return meal.replaceAll("<br/>", "\n").replaceAll("\\((\\d*\\.*?)*\\)", "");

        } catch (Exception e) {
            Log.e("Error", String.valueOf(e));
            return "-";
        }
    }
}
