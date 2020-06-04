package com.example.jiyeonhyun.ma02_20161016.Parser;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class WeatherParser {

    public enum TagType { NONE, CATEGORY, OBSRVALUE};
    public static final String TAG = "WeatherActivity";

    public WeatherParser() {
    }

    public String parse(String xml) {

        WeatherParser.TagType tagType = WeatherParser.TagType.NONE;
        String result = "";
        int flag = 0;

        /* 기상청 API 받아오기 -> 기온값 받아오기 */
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("category")) { // category에 따라 풍향, 풍속, 기온값 코드가 달라지기 때문에 flag 값을 줘서 기온값일 때만 받아오기
                            tagType = TagType.CATEGORY;
                            flag = 0;
                        }
                        if (parser.getName().equals("obsrValue")) {
                            tagType = TagType.OBSRVALUE;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType) {
                            case CATEGORY:
                                if (parser.getText().equals("T1H")) { // 기온값 받아오기
                                    flag = 1;
                                }
                                break;
                            case OBSRVALUE:
                                if (flag == 1) {
                                    result = parser.getText();
                                }
                                break;
                        }
                        tagType = WeatherParser.TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
