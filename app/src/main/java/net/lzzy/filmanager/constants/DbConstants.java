package net.lzzy.filmanager.constants;

import net.lzzy.filmanager.R;
import net.lzzy.filmanager.utils.AppUtils;
import net.lzzy.sqllib.DbPackager;

/**
 * Created by lzzy_gxy on 2019/3/11.
 * Description:
 */
public final class DbConstants {
    private DbConstants(){}
    private static final String DB_NAME="film.db";
    private static final int DB_VERSION=1;

    public static DbPackager packager;
    static {
        packager=DbPackager.getInstance(AppUtils.getContext()
                ,DB_NAME,DB_VERSION, R.raw.modeles);
    }


}
