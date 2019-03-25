package net.lzzy.filmanager.modeles;

import net.lzzy.filmanager.constants.DbConstants;
import net.lzzy.filmanager.utils.AppUtils;
import net.lzzy.sqllib.SqlRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by lzzy_gxy on 2019/3/11.
 * Description:
 */
public class OrderFactory {
    private static  OrderFactory instance;
    private static SqlRepository<Order> repository;

    private OrderFactory(){
        repository=new SqlRepository<>(AppUtils.getContext(),Order.class, DbConstants.packager);
    }

    public static OrderFactory getInstance(){
        if(instance==null){
            synchronized (OrderFactory.class){
                if (instance==null){
                    instance=new OrderFactory();
                }
            }
        }
        return instance;
    }

    public List<Order> get(){
        return repository.get();
    }

    public Order getById(String id){
        return repository.getById(id);
    }

    /** 根据电影名字，时间，价格，地址来查询订单**/
    public List<Order> searchOrder(String kw){
        try {
        List<Order> orders=repository.getByKeyword(kw
                ,new String[]{Order.COL_MOVIE,Order.COL_MOVIE_TIME
                        ,Order.COL_PRICE},false);
        List<Cinema> cinemas=CinemaFactory.getInstance().searchCinemas(kw);
        if(cinemas.size()>0){
            for(Cinema cinema:cinemas){
                List<Order> cOrders=repository.getByKeyword(cinema.getId().toString()
                        ,new String[]{Order.COL_CINEMA_ID},true);
                orders.addAll(cOrders);
            }
        }
            return orders;
        }catch (IllegalAccessException |InstantiationException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }



    public List<Order> getOrdersByCinema(String cinemaId){
        try {
            return repository.getByKeyword(cinemaId,new String[]{Order.COL_CINEMA_ID},true);
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    public boolean addOrder(Order order){
        repository.insert(order);
        return true;
    }

   public boolean deleteOrder(Order order){
        try {
            repository.delete(order);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public void update(Order order){
        repository.update(order);
    }

}
