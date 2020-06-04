package com.example.jiyeonhyun.ma02_20161016.Parser;

import android.util.Log;

import com.example.jiyeonhyun.ma02_20161016.DTO.MovieDTO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class MovieParser {

    public enum TagType { NONE, TITLE, PUBDATE, DIRECTOR };
    public static final String TAG = "MovieActivity";

    public MovieParser() {
    }

    public ArrayList<MovieDTO> parse(String xml) {

        ArrayList<MovieDTO> resultList = new ArrayList();
        MovieDTO dto = null;

        TagType tagType = TagType.NONE;

        /* 네이버 영화 API 받아오기 -> 제목, 개봉일, 감독 갖고 오기 : DTO 객체 */
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
                        if (parser.getName().equals("item")) {
                            dto = new MovieDTO();
                        } else if (parser.getName().equals("title")) { // 태그 이름과 비교
                            if (dto != null) tagType = TagType.TITLE; // 미리 태그 타입을 설정해야 컨텐츠에서 원하는 값을 가지고 올 때 태그 타입을 보고 가져올 수 있음.
                        } else if (parser.getName().equals("pubDate")) {
                            if (dto != null) tagType = TagType.PUBDATE;
                        } else if (parser.getName().equals("director")) {
                            if (dto != null) tagType = TagType.DIRECTOR;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("item")) {
                            resultList.add(dto);
                            dto = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType) {
                            case TITLE:
                                dto.setTitle(parser.getText());
                                break;
                            case DIRECTOR:
                                dto.setDirector(parser.getText());
                                break;
                            case PUBDATE:
                                dto.setPubDate(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
