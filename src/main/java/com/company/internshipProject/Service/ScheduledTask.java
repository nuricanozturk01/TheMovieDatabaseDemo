package com.company.internshipProject.Service;

import com.company.internshipProject.Entity.JSONParser.MovieObject;
import com.company.internshipProject.Entity.JSONParser.TV.ResultOfTVSeries;
import com.company.internshipProject.Service.MovieAPIService.MovieAPIService;
import com.company.internshipProject.Service.TVSeriesAPIService.TVSeriesAPIService;
import com.company.internshipProject.Service.UserService.IUserService;
import com.company.internshipProject.util.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@EnableScheduling
@Component
public class ScheduledTask
{

    @Autowired
    private MovieAPIService movieService;

    @Autowired
    private TVSeriesAPIService tvService;

    @Autowired
    private IUserService userService;

    public ScheduledTask(MovieAPIService movieService, TVSeriesAPIService tvService, IUserService userService) {
        this.movieService = movieService;
        this.tvService = tvService;
        this.userService = userService;
    }

    private String getPopularMoviesWithTitle(List<MovieObject> movies)
    {
        List<String> popularMovies = new ArrayList<>();

        movies
                .forEach(movie -> popularMovies.add("Title: " + movie.getTitle() + "       Avg. Vote: " + movie.getVote_average() + "\n"));


        StringBuilder str = new StringBuilder();

        popularMovies.stream().map(movie -> movie + "\n").forEach(str::append);

        return str.toString();
    }

    private String getPopularTvSeriesWithTitle(List<ResultOfTVSeries> tvSeries)
    {
        List<String> popularTvSeries = new ArrayList<>();

        tvSeries.
                forEach(tv -> popularTvSeries.add("Title: " + tv.getOriginal_name() + "       Avg. Vote: " + tv.getVote_average() + "\n"));

        StringBuilder str = new StringBuilder();

        popularTvSeries.stream().map(tv -> tv + "\n").forEach(str::append);

        return str.toString();
    }

















    @Scheduled(cron="10 59 09 * * *")
    private void sendPopularMoviesToMembers()
    {
       if (userService.getAllUsers() == null)
           return;

       List<MovieObject> movies = movieService.getPopularMovies(1);

       String str = getPopularMoviesWithTitle(movies);

       userService.getAllUsers().forEach(user -> Mail.sendMessage(user.getEmail(), "Popular Movies", str));
    }

    @Scheduled(cron="10 00 10 * * *")
    private void sendPopularTvShowsToMembers()
    {
        if (userService.getAllUsers() == null)
            return;

        List<ResultOfTVSeries> tvSeries = tvService.getPopularTvSeriesPageByPage(1);

        String str = getPopularTvSeriesWithTitle(tvSeries);

        userService.getAllUsers().forEach(user -> Mail.sendMessage(user.getEmail(), "Popular Tv Series", str));
    }
}
