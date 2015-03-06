package com.odong.buddhismhomework.pages.reading;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.EditText;

import com.google.gson.Gson;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Book;
import com.odong.buddhismhomework.utils.WidgetHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;

/**
 * Created by flamen on 15-3-4.
 */
public class EpubActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        book = new Gson().fromJson(getIntent().getStringExtra("book"), Book.class);
        current = getIntent().getIntExtra("page", 0);
        getActionBar().setIcon(R.drawable.ic_dzj);
        setTitle(book.getTitle());


        showBook();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_epub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final WidgetHelper wh = new WidgetHelper(this);
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
            case R.id.action_book_info:
                Intent info = new Intent(this, InfoActivity.class);
                info.putExtra("book", new Gson().toJson(book));
                startActivity(info);
                break;
            case R.id.action_page_next:
                current++;
                showBook();
                break;
            case R.id.action_page_goto:
                AlertDialog.Builder adbG = new AlertDialog.Builder(this);
                adbG.setTitle(getString(R.string.lbl_goto_page, size));

                final EditText pg = new EditText(this);
                pg.setInputType(InputType.TYPE_CLASS_NUMBER);
                pg.setHint(R.string.lbl_hint_goto);
                adbG.setView(pg);
                adbG.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            int p = Integer.parseInt(pg.getText().toString().trim());
                            if (p < 1 || p > size) {
                                wh.toast(getString(R.string.lbl_error_page_not_valid), false);
                                return;
                            }
                            current = p - 1;
                            showBook();
                        } catch (NumberFormatException e) {
                            wh.toast(getString(R.string.lbl_error_page_not_valid), false);
                        }
                    }
                });
                adbG.setNegativeButton(android.R.string.no, null);
                adbG.create().show();
                break;
            case R.id.action_page_previous:
                current--;
                showBook();
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

    private void unzip() throws IOException {
        File zipF = book.toFile(this);
        File dirF = book.toCacheFile(this);
        if (dirF.exists()) {
            return;
        }

        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipF)));
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
            File f = new File(dirF, ze.getName());
            if(!f.getParentFile().exists()){
                f.getParentFile().mkdirs();
            }
            if (ze.isDirectory()) {
                f.mkdirs();
                Log.d("创建目录", f.getAbsolutePath());
            } else {
                byte[] buf = new byte[1024];
                int count;
                Log.d("解压缩文件", f.getAbsolutePath());
                FileOutputStream fos = new FileOutputStream(f);
                while ((count = zis.read(buf)) != -1) {
                    fos.write(buf, 0, count);
                }
                fos.flush();
                fos.close();
            }
        }
    }

    private void showBook() {
        WidgetHelper wh = new WidgetHelper(this);

        try {
            unzip();

            nl.siegmann.epublib.domain.Book epub = book.toEpub(this);
            Spine spine = epub.getSpine();
            size = spine.getSpineReferences().size();

            if (current < 0) {
                current = 0;
                wh.toast(getString(R.string.lbl_error_first_page), false);
                return;
            }
            if (current >= size) {
                current = size - 1;
                wh.toast(getString(R.string.lbl_error_last_page), false);
                return;
            }
            wh.toast(getString(R.string.lbl_cur_page, current + 1, size), false);

            Resource res = spine.getResource(current);
            ((WebView) findViewById(R.id.wv_content)).loadDataWithBaseURL(
                    book.toBaseUrl(this),
                    new String(res.getData()),
                    "text/html",
                    "utf-8",
                    null
            );


        } catch (IOException e) {
            Log.d("读取", "EPUB", e);
            wh.toast(getString(R.string.lbl_error_book_format), false);
            book.toCacheFile(this).delete();
        }

    }


    private Book book;
    private int current;
    private int size;

}
