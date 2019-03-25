package net.lzzy.filmanager.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AndroidException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.lzzy.filmanager.R;
import net.lzzy.filmanager.modeles.Cinema;
import net.lzzy.filmanager.modeles.CinemaFactory;
import net.lzzy.filmanager.modeles.Order;
import net.lzzy.filmanager.modeles.OrderFactory;
import net.lzzy.filmanager.utils.AppUtils;
import net.lzzy.filmanager.utils.ViewUils;
import net.lzzy.simpledatepicker.CustomDatePicker;
import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Administrator
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int MIN_DISTANCE = 100;
    private LinearLayout layoutMenu;
    private TextView tvTitle;
    private SearchView search;
    public static final String EXTRA_NEW_CINEMA = "newCinema";
    private LinearLayout layoutAddmain;
    private ListView lv;
    private Spinner tvArea;
    private EditText edtName;
    private OrderFactory factory=OrderFactory.getInstance();
    private List<Order> orders;
    private EditText edtprice;
    private TextView tvDate;
    private long publishDate;
    private CustomDatePicker datePicker;
    private View dialog;
    private Spinner spCinema;
    private ImageView imgQRCode;
    private GenericAdapter<Order> adapter;
    private List<Cinema> cinemas;
    private SearchView search1;
    private Button but;
    private float touchX1;
    private float touchX2;
    private boolean isDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** 去掉标题栏 **/
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        setTitleMenu();
        initViews();
        showAndAddOrders();

        //region 无数据视图
        TextView tvNone=findViewById(R.id.activity_main_tv_none);
        lv.setEmptyView(tvNone);

        boolean isNewMain = getIntent().getBooleanExtra(CinemasActivity.EXTRA_NEW_ORDER,false);
        if (isNewMain){
            layoutAddmain.setVisibility(View.VISIBLE);
        }
        search.setOnQueryTextListener(new ViewUils.AbstractQueryHandler() {
            @Override
            public boolean handleQuery(String kw) {
                orders.clear();
                if (TextUtils.isEmpty(kw)){
                    orders.addAll(factory.get());
                }else {
                    orders.addAll(factory.searchOrder(kw));
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    /**适配器*/
    private void showAndAddOrders() {
        cinemas = CinemaFactory.getInstance().get();
        orders=factory.get();
        spCinema.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, cinemas));
        adapter = new GenericAdapter<Order>(this,R.layout.main_item, orders) {

            @Override
            public void populate(ViewHolder viewHolder, Order order) {
                String location= String.valueOf(CinemaFactory.getInstance()
                        .getById(order.getCinemaId().toString()));
                viewHolder.setTextView(R.id.main_item_movieName,order.getMovie())
                        .setTextView(R.id.main_item_area,location);
                but = viewHolder.getView(R.id.main_item_btn);
                but.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("删除确认")
                                .setMessage("要删除订单吗？")
                                .setNegativeButton("取消",null)
                                .setPositiveButton("确定", (dialog, which) -> adapter.remove(order)).show();
                    }
                });
                viewHolder.getConvertView().setOnTouchListener(new ViewUils.AbstractOnTouchListener() {
                    @Override
                    public boolean listenerTouch(View view, MotionEvent event) {
                        slideToDelete(event,order,but);
                        return true;
                    }
                });
            }

            @Override
            public boolean persistInsert(Order order) {
                return factory.addOrder(order);
            }

            @Override
            public boolean persistDelete(Order order) {
                return factory.deleteOrder(order);
            }
        };
        lv.setAdapter(adapter);
        initDatePicker();
        findViewById(R.id.activity_add_book_layout).setOnClickListener(v -> datePicker.show(tvDate.getText().toString()));
        addListener();
    }

    private void slideToDelete(MotionEvent event, Order order, Button but) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchX1=event.getX();

                break;
            case MotionEvent.ACTION_UP:
                touchX2 =event.getX();
                if (touchX1-touchX2> MIN_DISTANCE){
                    if (!isDelete){
                        but.setVisibility(View.VISIBLE);
                        isDelete=true;
                    }

                }else {
                    if (but.isShown()){
                        but.setVisibility(View.GONE);
                        isDelete=false;
                    }else {
                        clickOrder(order);
                    }
                }
                break;
                default:
                    break;
        }
    }

    private void clickOrder(Order order) {
        Cinema cinema=CinemaFactory.getInstance()
                .getById(order.getCinemaId().toString().toString());
        String content="["+order.getMovie()+"]"+order.getMovieTime()+"\n"+cinema+"票价"+order.getPrice()+"元";
        View view= LayoutInflater.from(this).inflate(R.layout.dialog_qrcode,null);
        ImageView img=view.findViewById(R.id.dialog_qrcode_img);
        img.setImageBitmap(AppUtils.createQRCodeBitmap(content,300,300));
        new AlertDialog.Builder(this)
                .setView(view).show();
    }


    /**添加数据*/
    private void addListener() {
        findViewById(R.id.dialog_add_main_btn_cancel)
                .setOnClickListener(v -> layoutAddmain.setVisibility(View.GONE));
        findViewById(R.id.dialog_add_main_btn_ok).setOnClickListener(v -> {
            String name=edtName.getText().toString();
            String strPrice=edtprice.getText().toString();
            if (TextUtils.isEmpty(name)||TextUtils.isEmpty(strPrice)){
                Toast.makeText(this,"信息不完整，请重新输入",Toast.LENGTH_SHORT).show();
                return;
            }
            float price;
            try{
                price=Float.parseFloat(strPrice);
            }catch (NumberFormatException e){
                Toast.makeText(this,"票价格式不正确，请重新输入",Toast.LENGTH_SHORT).show();
                return;
            }

            Order order=new Order();
            Cinema cinema=cinemas.get(spCinema.getSelectedItemPosition());
            order.setCinemaId(cinema.getId());
            order.setMovie(name);
            order.setPrice(price);
            order.setMovieTime(tvDate.getText().toString());
            adapter.add(order);
            edtName.setText("");
            edtprice.setText("");
            layoutAddmain.setVisibility(View.GONE);

        });
        findViewById(R.id.dialog_add_main_btn_qrCode).setOnClickListener(v -> {
            String name=edtName.getText().toString();
            String price=edtprice.getText().toString();
            String location=spCinema.getSelectedItem().toString();
            String time=tvDate.getText().toString();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price)) {
                Toast.makeText(this,"信息不完整",Toast.LENGTH_SHORT).show();
                return;
            }
            String content="["+name+"]"+time+"\n"+location+"票价"+price+"元";
            imgQRCode.setImageBitmap(AppUtils.createQRCodeBitmap(content,200,200));
        });
        imgQRCode.setOnLongClickListener(v -> {
            Bitmap bitmap=((BitmapDrawable)imgQRCode.getDrawable()).getBitmap();
            Toast.makeText(this,AppUtils.readQRCode(bitmap),Toast.LENGTH_SHORT).show();
            return true;
        });

    }


    //region 初始化视图组件

    private void initViews() {
        layoutAddmain = findViewById(R.id.dialog_add_main_layout);
        lv = findViewById(R.id.activity_main_lv);
        tvArea = findViewById(R.id.dialog_add_main_sp_area);
        edtName = findViewById(R.id.dialog_add_main_edt_name);
        edtprice = findViewById(R.id.dialog_add_main_edt_price);
        tvDate = findViewById(R.id.activity_add_book_tv_date);
        spCinema = findViewById(R.id.dialog_add_main_sp_area);
        imgQRCode = findViewById(R.id.dialog_add_main_imv);
        search1 = findViewById(R.id.main_sv_search);

    }
/**日期*/
    public void initDatePicker() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now=sdf.format(new Date());
        tvDate.setText(now);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH,1);
        String end=sdf.format(calendar.getTime());
        datePicker=new CustomDatePicker(this,s -> tvDate.setText(s),now,end);
        datePicker.showSpecificTime(true);
        datePicker.setIsLoop(true);

    }


    /** 标题栏 **/
    private void setTitleMenu() {
        layoutMenu = findViewById(R.id.bar_title_layout_menu);
        layoutMenu.setVisibility(View.GONE);
        findViewById(R.id.bar_title_img_menu).setOnClickListener(this);
        tvTitle = findViewById(R.id.bar_title_tv_title);
        tvTitle.setText(R.string.bar_title_menu_orders);
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
                Intent intent=new Intent(this,CinemasActivity.class);
                intent.putExtra(EXTRA_NEW_CINEMA,true);
                startActivity(intent);
                finish();
                break;

            case R.id.bar_title_tv_view_cinema:
                startActivity(new Intent(this,CinemasActivity.class));
                finish();
                break;
            case R.id.bar_title_tv_add_order:
                tvTitle.setText(R.string.bar_title_menu_add_orders);
                layoutMenu.setVisibility(View.GONE);
                layoutAddmain.setVisibility(View.VISIBLE);
                break;
            case R.id.bar_title_tv_view_order:
                layoutMenu.setVisibility(View.GONE);
                layoutAddmain.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }



}
