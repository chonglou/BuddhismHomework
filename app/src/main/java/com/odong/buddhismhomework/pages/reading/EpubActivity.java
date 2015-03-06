package com.odong.buddhismhomework.pages.reading;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;

import com.google.gson.Gson;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Book;
import com.odong.buddhismhomework.pages.MainActivity;
import com.odong.buddhismhomework.utils.KvHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;

/**
 * Created by flamen on 15-3-4.
 */
public class EpubActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        book = new Gson().fromJson(getIntent().getStringExtra("book"), Book.class);

        getActionBar().setIcon(R.drawable.ic_dzj);
        setTitle(book.getTitle());

        try {
            unzip();
        } catch (IOException e) {
            Log.d("读取", "EPUB", e);
            new WidgetHelper(this).toast(getString(R.string.lbl_error_book_format), false);
            book.toCacheFile(this).delete();
            return;
        }

        String link = getIntent().getStringExtra("link");
        if (link == null) {
            curPage = getIntent().getIntExtra("page", 0);
            Log.d("EPUB", "第" + curPage + "页");
            showBookByPage();
        } else {
            Log.d("EPUB", "地址: " + link);
            showBookByLink(link);
        }

        new WidgetHelper(this).setWebViewFont(R.id.wv_content);

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
                curPage++;
                showBookByPage();
                break;
            case R.id.action_home:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.action_zoom_in:
                wh.zoomWebView(R.id.wv_content, false);
                break;
            case R.id.action_zoom_out:
                wh.zoomWebView(R.id.wv_content, true);
                break;
            case R.id.action_page_goto:
                AlertDialog.Builder adbG = new AlertDialog.Builder(this);
                adbG.setTitle(getString(R.string.lbl_goto_page, pageSize));

                final EditText pg = new EditText(this);
                pg.setInputType(InputType.TYPE_CLASS_NUMBER);
                pg.setHint(R.string.lbl_hint_goto);
                adbG.setView(pg);
                adbG.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            int p = Integer.parseInt(pg.getText().toString().trim());
                            if (p < 1 || p > pageSize) {
                                wh.toast(getString(R.string.lbl_error_page_not_valid), false);
                                return;
                            }
                            curPage = p - 1;
                            showBookByPage();
                        } catch (NumberFormatException e) {
                            wh.toast(getString(R.string.lbl_error_page_not_valid), false);
                        }
                    }
                });
                adbG.setNegativeButton(android.R.string.no, null);
                adbG.create().show();
                break;
            case R.id.action_page_previous:
                curPage--;
                showBookByPage();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView wv = (WebView) findViewById(R.id.wv_content);
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
            wv.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
            if (!f.getParentFile().exists()) {
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

    private void showBookByLink(String link) {
        try {
            nl.siegmann.epublib.domain.Book epub = book.toEpub(this);
            Spine spine = epub.getSpine();
            List<SpineReference> srs = spine.getSpineReferences();
            pageSize = srs.size();
            for (int i = 0; i < pageSize; i++) {
                if (link.startsWith(srs.get(i).getResource().getHref())) {
                    curPage = i;
                    break;
                }
            }

            ((WebView) findViewById(R.id.wv_content)).loadUrl(book.toBaseUrl(this) + link);

        } catch (IOException e) {
            Log.d("读取", "EPUB", e);
            new WidgetHelper(this).toast(getString(R.string.lbl_error_book_format), false);
        }
    }

    private void showBookByPage() {
        WidgetHelper wh = new WidgetHelper(this);

        try {
            nl.siegmann.epublib.domain.Book epub = book.toEpub(this);
            Spine spine = epub.getSpine();
            List<SpineReference> srs = spine.getSpineReferences();
            pageSize = srs.size();


            if (curPage < 0) {
                curPage = 0;
                wh.toast(getString(R.string.lbl_error_first_page), false);
                return;
            }
            if (curPage >= pageSize) {
                curPage = pageSize - 1;
                wh.toast(getString(R.string.lbl_error_last_page), false);
                return;
            }
            wh.toast(getString(R.string.lbl_cur_page, curPage + 1, pageSize), false);

            ((WebView) findViewById(R.id.wv_content)).loadUrl(book.toBaseUrl(this) + srs.get(curPage).getResource().getHref());

        } catch (IOException e) {
            Log.d("读取", "EPUB", e);
            wh.toast(getString(R.string.lbl_error_book_format), false);
        }

    }


    private Book book;
    private int curPage;
    private int pageSize;

}
