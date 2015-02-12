package com.odong.buddhismhomework.utils;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Clock;
import com.odong.buddhismhomework.models.Homework;

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

    public List<Clock> getClockList() {
        List<Clock> clocks = new ArrayList<Clock>();
        try {
            XmlResourceParser xrp = context.getResources().getXml(R.xml.clocks);


            for (int et = xrp.getEventType(); et != XmlPullParser.END_DOCUMENT; et = xrp.next()) {

                switch (et) {
                    case XmlPullParser.START_TAG:
                        if (xrp.getName().equals("entry")) {
                            Clock c = new Clock();
                            c.setMinutes(xrp.getAttributeIntValue(null, "key", 0));
                            c.setName(readText(xrp));
                            clocks.add(c);
                        }
                        break;

                }
            }
        } catch (XmlPullParserException e) {
            Log.e("XML", "HOMEWORK", e);
        } catch (IOException e) {
            Log.e("XML", "HOMEWORK", e);
        }

        return clocks;
    }

    public List<Homework> getHomeworkList() {
        List<Homework> homework = new ArrayList<Homework>();
        try {
            XmlResourceParser xrp = context.getResources().getXml(R.xml.homework);

            Homework hw = null;
            for (int et = xrp.getEventType(); et != XmlPullParser.END_DOCUMENT; et = xrp.next()) {

                switch (et) {
                    case XmlPullParser.START_TAG:
                        if (xrp.getName().equals("entry")) {
                            hw = new Homework();
                        } else if (xrp.getName().equals("id")) {
                            hw.setId(readText(xrp));
                        } else if (xrp.getName().equals("name")) {
                            hw.setName(readText(xrp));
                        } else if (xrp.getName().equals("type")) {
                            hw.setType(readText(xrp));
                        } else if (xrp.getName().equals("incantation")) {
                            String file = readText(xrp);
                            int rid = context.getResources().getIdentifier(file, "raw", context.getPackageName());
                            if (rid == 0) {
                                Log.e("XML Helper", "资源" + file + "不存在");
                            }
                            hw.getIncantations().add(rid);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (xrp.getName().equals("entry")) {
                            homework.add(hw);
                        }
                        break;

                }
            }
        } catch (XmlPullParserException e) {
            Log.e("XML", "HOMEWORK", e);
        } catch (IOException e) {
            Log.e("XML", "HOMEWORK", e);
        }

        return homework;
    }

    private String readText(XmlResourceParser xrp) throws IOException, XmlPullParserException {
        String result = null;
        if (xrp.next() == XmlPullParser.TEXT) {
            result = xrp.getText();
            xrp.nextTag();
        }
        return result;
    }

    private Context context;
}
