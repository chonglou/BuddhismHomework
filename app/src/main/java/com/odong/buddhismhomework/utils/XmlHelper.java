package com.odong.buddhismhomework.utils;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.odong.buddhismhomework.models.Clock;
import com.odong.buddhismhomework.models.Homework;
import com.odong.buddhismhomework.models.Music;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by flamen on 15-2-12.
 */
public class XmlHelper {
    public XmlHelper(Context context) {
        this.context = context;
    }

    public List<Music> getMusicList(String name) {
        final List<Music> musics = new ArrayList<Music>();
        read(name, new Callback() {
            @Override
            public void run(XmlResourceParser xrp) throws IOException, XmlPullParserException {
                Music b = new Music();
                b.setName(xrp.getAttributeValue(null, "name"));
                b.setAuthor(xrp.getAttributeValue(null, "author"));
                b.setMp3(xrp.getAttributeValue(null, "mp3"));
                for (String name : readText(xrp).split("\\n")) {
                    b.getFiles().add(name2rid("raw", name));
                }
                musics.add(b);
            }
        });
        return musics;
    }

    public List<Clock> getClockList() {
        final List<Clock> clocks = new ArrayList<Clock>();
        read("clocks", new Callback() {
            @Override
            public void run(XmlResourceParser xrp) throws IOException, XmlPullParserException {
                Clock c = new Clock();
                c.setMinutes(xrp.getAttributeIntValue(null, "key", 0));
                c.setName(readText(xrp));
                clocks.add(c);
            }
        });

        return clocks;
    }

    public List<Homework> getHomeworkList(String name) {
        final List<Homework> homework = new ArrayList<Homework>();
        read(name, new Callback() {
            @Override
            public void run(XmlResourceParser xrp) throws IOException, XmlPullParserException {

                Homework h = new Homework();
                h.setName(xrp.getAttributeValue(null, "name"));
                h.setVid(xrp.getAttributeValue(null, "vid"));
                for (String name : readText(xrp).split("\\n")) {
                    h.getIncantations().add(name2rid("raw", name));
                }
                homework.add(h);
            }
        });

        return homework;
    }

    interface Callback {
        void run(XmlResourceParser xrp) throws IOException, XmlPullParserException;
    }

    private void read(String name, Callback cb) {

        try {
            XmlResourceParser xrp = context.getResources().getXml(name2rid("xml", name));


            for (int et = xrp.getEventType(); et != XmlPullParser.END_DOCUMENT; et = xrp.next()) {
                switch (et) {
                    case XmlPullParser.START_TAG:
                        if (xrp.getName().equals("entry")) {
                            cb.run(xrp);
                        }
                        break;

                }
            }
        } catch (XmlPullParserException e) {
            Log.e("XML", "PARSER", e);
        } catch (IOException e) {
            Log.e("XML", "IO", e);
        }

    }

    private String readText(XmlResourceParser xrp) throws IOException, XmlPullParserException {
        String result = null;
        if (xrp.next() == XmlPullParser.TEXT) {
            result = xrp.getText();
            xrp.nextTag();
        }
        return result;
    }

    private int name2rid(String type, String name) {
        name = name.trim();
        int rid = context.getResources().getIdentifier(name, type, context.getPackageName());
        if (rid == 0) {
            Log.d("XML Helper", "资源[" + type + "," + name + "]不存在");
        }
        return rid;
    }

    private Context context;
}
