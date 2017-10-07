package cback;

import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.entities.TvShowResultsPage;
import com.uwetrottmann.tmdb2.services.SearchService;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.*;
import com.uwetrottmann.trakt5.enums.Extended;
import com.uwetrottmann.trakt5.enums.Type;
import retrofit2.Call;
import retrofit2.Response;

import java.util.List;
import java.util.Optional;

public class TraktManager {

    private TraktV2 trakt;
    private Tmdb tmdb;
    private TestBot bot;

    public TraktManager(TestBot bot) {
        this.bot = bot;

        Optional<String> traktToken = bot.getConfigManager().getTokenValue("traktToken");
        if (!traktToken.isPresent()) {
            System.out.println("-------------------------------------");
            System.out.println("Insert your Trakt token in the config.");
            System.out.println("Exiting......");
            System.out.println("-------------------------------------");
            System.exit(0);
            return;
        }
        trakt = new TraktV2(traktToken.get());

        Optional<String> tmdbToken = bot.getConfigManager().getTokenValue("tmdbToken");
        if (!tmdbToken.isPresent()) {
            System.out.println("-------------------------------------");
            System.out.println("Insert your tmdb token in the config.");
            System.out.println("Exiting......");
            System.out.println("-------------------------------------");
            System.exit(0);
            return;
        }
        tmdb = new Tmdb(tmdbToken.get());
        System.out.println(tmdb.apiKey());
    }

    public Show showSummaryFromName(String showName) {
        try {
            //Response<List<SearchResult>> search = trakt.search().textQuery(showName, Type.SHOW, null, 1, 1).execute();
            Response<List<SearchResult>> search = trakt.search().textQuery(Type.SHOW, showName, null, null, null, null, null, null, Extended.FULL, 1, 1).execute();
            if (search.isSuccessful() && !search.body().isEmpty()) {
                Response<Show> show = trakt.shows().summary(search.body().get(0).show.ids.imdb, Extended.FULL).execute();
                if (show.isSuccessful()) {
                    return show.body();
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public Movie movieSummaryFromName(String movieName) {
        try {
            Response<List<SearchResult>> search = trakt.search().textQuery(Type.MOVIE, movieName, null, null, null, null, null, null, Extended.FULL, 1, 1).execute();
            if (search.isSuccessful() && !search.body().isEmpty()) {
                Response<Movie> movie = trakt.movies().summary(search.body().get(0).movie.ids.imdb, Extended.FULL).execute();
                if (movie.isSuccessful()) {
                    return movie.body();
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public String searchTmdbMovie(String movieName) {
        try {
            SearchService service = tmdb.searchService();
            Response<MovieResultsPage> search = service.movie(movieName, null, null, true, null, null, null).execute();
            if (search.isSuccessful()) {
                Response<com.uwetrottmann.tmdb2.entities.Movie> movie = tmdb.moviesService().summary(search.body().results.get(0).id).execute();
                if (movie.isSuccessful()) {
                    return movie.body().imdb_id;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }
    public String searchTmdbShow(String showName) {
        try {
            SearchService service = tmdb.searchService();
            Response<TvShowResultsPage> search = service.tv(showName, null, null, null, null).execute();
            if (search.isSuccessful()) {
                Response<com.uwetrottmann.tmdb2.entities.TvShow> show = tmdb.tvService().tv(search.body().results.get(0).id).execute();
                if (show.isSuccessful()) {
                    return show.body().external_ids.imdb_id;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

}