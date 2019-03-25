package net.lzzy.filmanager.utils;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

/**
 * Created by lzzy_gxy on 2019/3/22.
 * Description:
 */
public class ViewUils {

    public static abstract class AbstractQueryHandler implements SearchView.OnQueryTextListener{
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String kw) {

            return handleQuery(kw);
        }

        public abstract boolean handleQuery(String kw);
    }

    public static abstract class AbstractOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return listenerTouch(v,event);
        }
        public abstract boolean listenerTouch(View view, MotionEvent event);
    }
}
