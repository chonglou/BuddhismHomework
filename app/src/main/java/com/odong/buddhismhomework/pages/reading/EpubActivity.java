package com.odong.buddhismhomework.pages.reading;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Book;
import com.odong.buddhismhomework.utils.WidgetHelper;

import java.io.IOException;

/**
 * Created by flamen on 15-3-4.
 */
public class EpubActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub);
        book = new Gson().fromJson(getIntent().getStringExtra("book"), Book.class);
        getActionBar().setIcon(R.drawable.ic_dzj);
        setTitle(book.getTitle());

        initBook();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_to_favorites:
                new WidgetHelper(this).showFavoriteDialog(book);
                break;
            case R.id.action_book_chapter:
                Intent chapter = new Intent(this, ChapterActivity.class);
                chapter.putExtra("book", new Gson().toJson(book));
                startActivity(chapter);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initBook() {
        try {
            nl.siegmann.epublib.domain.Book epub = book.toEpub(this);

            Log.d("标题", epub.getTitle());
            Log.d("作者", epub.getMetadata().getAuthors().toString());

            Bitmap bitmap = BitmapFactory.decodeStream(epub.getCoverImage().getInputStream());
            Log.d("封面", "大小 " + bitmap.getWidth() + "x" + bitmap.getHeight() + " pixels");

        } catch (IOException e) {
            Log.d("读取", "EPUB", e);

        }
    }


    private Book book;

}
