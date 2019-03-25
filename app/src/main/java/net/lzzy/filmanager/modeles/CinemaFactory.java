package net.lzzy.filmanager.modeles;



import net.lzzy.filmanager.constants.DbConstants;
import net.lzzy.filmanager.utils.AppUtils;
import net.lzzy.sqllib.SqlRepository;

import java.util.ArrayList;
import java.util.List;


/**

 */

public class CinemaFactory {

    private static final CinemaFactory OUR_INSTANCE=new CinemaFactory();
    private static SqlRepository<Cinema> repository;

    public static CinemaFactory getInstance(){
        return OUR_INSTANCE;
    }

    private CinemaFactory(){
        repository=new SqlRepository<>(AppUtils.getContext(),Cinema.class, DbConstants.packager);
    }

    public List<Cinema> get(){
        return repository.get();
    }

    public Cinema getById(String id){
        return repository.getById(id);
    }

    /** 根据影院名称，地址来查询影院 **/

    public List<Cinema> searchCinemas(String kw){
        List<Cinema> result=new ArrayList<>();
        List<Cinema> all=get();
        for(Cinema cinema:all) {
            if (cinema.toString().contains(kw)) {
                result.add(cinema);
            }
        }
        return result;
    }


    private boolean isCinemaExists(Cinema cinema){
        return searchCinemas(cinema.toString()).size()>0;

    }

    public boolean addCinema(Cinema cinema){
        try {
            if (!isCinemaExists(cinema)) {
                repository.insert(cinema);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }

   public boolean deleteCinema(Cinema cinema){
        try {
            repository.delete(cinema);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public void update(Cinema cinema){
         repository.update(cinema);
    }


}
