package com.odong.buddhismhomework.pages.reading;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Book;
import com.odong.buddhismhomework.utils.WidgetHelper;

import java.io.IOException;

import nl.siegmann.epublib.domain.Metadata;

/**
 * Created by flamen on 15-3-6.
 */
public class InfoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        Book book = new Gson().fromJson(getIntent().getStringExtra("book"), Book.class);
        getActionBar().setIcon(R.drawable.ic_dzj);
        setTitle(getString(R.string.action_book_info) + ": " + book.getTitle());


        try {

            nl.siegmann.epublib.domain.Book epub = book.toEpub(this);
            Metadata md = epub.getMetadata();
            TextView tv = (TextView) findViewById(R.id.tv_book_info);
            tv.setText(
                    getString(R.string.tv_book_info,
                            md.getTitles(),
                            md.getAuthors(),
                            md.getPublishers(),
                            md.getDates(),
                            md.getLanguage(),
                            md.getDescriptions()
                    ));

            Bitmap bp = BitmapFactory.decodeStream(epub.getCoverImage().getInputStream());

            tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, new BitmapDrawable(getResources(), bp));
            //((ImageView)findViewById(R.id.iv_book_cover)).setImageBitmap(bitmap);

        } catch (IOException e) {
            Log.e("EPUB", "目录", e);
            new WidgetHelper(this).toast(getString(R.string.lbl_error_book_format), false);
        }
    }
}
