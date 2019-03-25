package net.lzzy.filmanager.activities;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.style.cityjd.JDCityPicker;
import com.lljjcoder.style.citythreelist.ProvinceActivity;

import net.lzzy.filmanager.R;
import net.lzzy.filmanager.modeles.Cinema;
import net.lzzy.filmanager.modeles.CinemaFactory;
import net.lzzy.filmanager.utils.ViewUils;
import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;

import java.util.List;

/**
 *
 * @author lzzy_gxy
 * @date 2019/3/13
 * Description:
 */
public class CinemasActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_NEW_ORDER = "newOrder";
    private LinearLayout layoutMenu;
    private TextView tvTitle;
    private SearchView search;
    private EditText edtName;
    private ListView lv;
    private List<Cinema> cinemas;
    private CinemaFactory factory;
    private TextView tvArea;
    private GenericAdapter<Cinema> adapter;
    private LinearLayout layoutAddCinema;
    private String province="广西壮族自治区";
    private String city="柳州市";
    private String area="鱼峰区";
    private SearchView search1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** 去掉标题栏 **/
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cinemas);
        setTitleMenu();
        initViews();
        bindList();

        //region 无数据视图
        TextView tvNone=findViewById(R.id.activity_cinemas_tv_none);
        lv.setEmptyView(tvNone);
        //endregion

        boolean isNewCinema = getIntent().getBooleanExtra(MainActivity.EXTRA_NEW_CINEMA,false);
        if (isNewCinema){
            showDialog();


        }
        /**查询*/
        search.setOnQueryTextListener(new ViewUils.AbstractQueryHandler() {
            @Override
            public boolean handleQuery(String kw) {
                cinemas.clear();
                if (TextUtils.isEmpty(kw)){
                    cinemas.addAll(factory.get());
                }else {
                    cinemas.addAll(factory.searchCinemas(kw));
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });


    }

    //region 初始化视图组件

    private void initViews() {
        layoutAddCinema = findViewById(R.id.dialog_add_cinema_layout);
        lv =findViewById(R.id.activity_cinema_lv);
        tvArea =findViewById(R.id.dialog_add_tv_area);
        edtName =findViewById(R.id.dialog_add_cinema_edt_name);
        search1 = findViewById(R.id.main_sv_search);


    }
    //endregion

    //region  适配器and数据源

    private void bindList() {
        factory = CinemaFactory.getInstance();
        cinemas= factory.get();
        adapter = new GenericAdapter<Cinema>(this,R.layout.cinemas_item,cinemas) {
            @Override
            public void populate(ViewHolder viewHolder, Cinema cinema) {
                viewHolder.setTextView(R.id.cinemas_items_name,cinema.getName())
                        .setTextView(R.id.cinemas_items_location,cinema.getLocation());


            }

            @Override
            public boolean persistInsert(Cinema cinema) {
                return factory.addCinema(cinema);
            }

            @Override
            public boolean persistDelete(Cinema cinema) {
                return factory.deleteCinema(cinema);
            }

        };
        lv.setAdapter(adapter);


    }
    //endregion

    //region 选择地址and添加电影

    private void showDialog() {
        layoutAddCinema.setVisibility(View.VISIBLE);
        findViewById(R.id.dialog_add_cinema_btn_cancel)
                .setOnClickListener(v -> layoutAddCinema.setVisibility(View.GONE));

        findViewById(R.id.dialog_add_cinema_layout_area).setOnClickListener(v -> {
            JDCityPicker cityPicker = new JDCityPicker();
            cityPicker.init(CinemasActivity.this);
            cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
                @Override
                public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                        CinemasActivity.this.province=province.getName();
                        CinemasActivity.this.city=city.getName();
                        CinemasActivity.this.area=district.getName();
                        String loc=province.getName()+city.getName()+district.getName();
                        tvArea.setText(loc);
                }

                @Override
                public void onCancel() {
                }
            });
            cityPicker.showCityPicker();
        });
        findViewById(R.id.dialog_add_cinema_btn_save).setOnClickListener(v -> {
            String name=edtName.getText().toString();
                Cinema cinema=new Cinema();
                cinema.setName(name);
                cinema.setArea(area);
                cinema.setCity(city);
                cinema.setProvince(province);
                cinema.setLocation(tvArea.getText().toString());
                adapter.add(cinema);
                edtName.setText("");
                layoutAddCinema.setVisibility(View.GONE);
        });

    }
//endregion

    /** 标题栏 **/
    private void setTitleMenu() {
        layoutMenu = findViewById(R.id.bar_title_layout_menu);
        layoutMenu.setVisibility(View.GONE);
        findViewById(R.id.bar_title_img_menu).setOnClickListener(this);
        tvTitle = findViewById(R.id.bar_title_tv_title);
        tvTitle.setText(R.string.bar_title_menu_cinema);
        search = findViewById(R.id.main_sv_search);
        findViewById(R.id.bar_title_tv_add_cinema).setOnClickListener(this);
        findViewById(R.id.bar_title_tv_view_cinema).setOnClickListener(this);
        findViewById(R.id.bar_title_tv_add_order).setOnClickListener(this);
        findViewById(R.id.bar_title_tv_view_order).setOnClickListener(this);
        findViewById(R.id.bar_title_tv_exit).setOnClickListener(this);
    }


    /** 对标题栏的点击监听 **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_title_img_menu:
                int visible=layoutMenu.getVisibility()==View.VISIBLE ? View.GONE : View.VISIBLE;
                layoutMenu.setVisibility(visible);
                break;
            case R.id.bar_title_tv_exit:
                System.exit(0);
                break;
            case R.id.bar_title_tv_add_cinema:
                layoutMenu.setVisibility(View.GONE);
                layoutAddCinema.setVisibility(View.VISIBLE);
                showDialog();
                break;
            case R.id.bar_title_tv_view_cinema:
                layoutMenu.setVisibility(View.GONE);
                layoutAddCinema.setVisibility(View.GONE);
                break;
            case R.id.bar_title_tv_add_order:
                tvTitle.setText(R.string.bar_title_menu_add_orders);
                Intent intent=new Intent(this,MainActivity.class);
                intent.putExtra(EXTRA_NEW_ORDER,true);
                startActivity(intent);
                finish();
                break;
            case R.id.bar_title_tv_view_order:
                startActivity(new Intent(this,MainActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

}
