package net.lzzy.filmanager.modeles;


import net.lzzy.sqllib.Ignored;
import net.lzzy.sqllib.Sqlitable;
import net.lzzy.sqllib.Table;


import java.util.UUID;

/**
 * Created by lzzy_gxy on 2019/3/11.
 * Description:
 */
@Table(name = "Orders")
public class Order extends BastEntity implements Sqlitable {
    @Ignored
    public static final String COL_MOVIE ="movie";
    @Ignored
    public static final String COL_MOVIE_TIME ="movieTime";
    @Ignored
    public static final String COL_PRICE ="price";
    @Ignored
    public static final String COL_CINEMA_ID ="cinemaId";
    /**电影名称*/
    private String movie;
    private String movieTime;
    private float price;
    private UUID cinemaId;

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public String getMovieTime() {
        return movieTime;
    }

    public void setMovieTime(String movieTime) {
        this.movieTime = movieTime;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public UUID getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(UUID cinemaId) {
        this.cinemaId = cinemaId;
    }

    @Override
    public boolean needUpdate() {
        return false;
    }
}
