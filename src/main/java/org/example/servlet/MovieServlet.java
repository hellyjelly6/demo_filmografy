package org.example.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.exception.NotFoundException;
import org.example.service.MovieService;
import org.example.service.impl.MovieServiceImpl;
import org.example.servlet.dto.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = {"/movie/*"})
    public class MovieServlet extends HttpServlet {
    private MovieService movieService = new MovieServiceImpl();
    Gson gson = new Gson();

    public MovieServlet() {}

    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setHeaders(resp);

        String response = "";

        try{
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                List<MovieOutGoingDto> movie = movieService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                response = gson.toJson(movie);
            }
            else{
                String[] paths = pathInfo.split("/");
                Long id = Long.parseLong(paths[1]);
                MovieOutGoingDto movie = movieService.findById(id);
                resp.setStatus(HttpServletResponse.SC_OK);
                response = gson.toJson(movie);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response = gson.toJson(e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response = "Bad Request";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(response);
        printWriter.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setHeaders(resp);

        String response = "";
        try{
            MovieIncomingDto movie = gson.fromJson(req.getReader(), MovieIncomingDto.class);
            if(movie != null){
                MovieOutGoingDto savedMovie = movieService.save(movie);
                response = gson.toJson(savedMovie);
                resp.setStatus(HttpServletResponse.SC_CREATED);
            }
            else{
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response = "Illegal Request";
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response = "Bad Request: %s".formatted(e.getMessage());
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(response);
        printWriter.flush();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setHeaders(resp);

        String response = "";
        try {
            String[] paths = req.getPathInfo().split("/");
            Long id = Long.parseLong(paths[paths.length - 1]);

            if (paths.length == 2) {
                // /movie/{id} path
                MovieIncomingDto movie = gson.fromJson(req.getReader(), MovieIncomingDto.class);
                if (movie != null) {
                    MovieOutGoingDto updatedMovie = movieService.update(movie, id);
                    response = gson.toJson(updatedMovie);
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response = "Illegal Request";
                }
            } else if (paths.length == 3 && paths[1].equals("actors")) {
                // /movie/actors/{id} path
                ActorLimitedDto[] actors = gson.fromJson(req.getReader(), ActorLimitedDto[].class);
                if (actors != null && actors.length > 0) {
                    MovieOutGoingDto updatedActors = movieService.saveActorsForMovie(id, actors);
                    response = gson.toJson(updatedActors);
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response = "Illegal Request";
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response = "Not Found";
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response = "Bad Request: "+e.getMessage();
        }

        PrintWriter printWriter = resp.getWriter();
        printWriter.print(response);
        printWriter.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setHeaders(resp);

        String response = "";
        try{
            String[] paths = req.getPathInfo().split("/");
            Long id = Long.parseLong(paths[1]);
            boolean isDelete = movieService.delete(id);
            if(isDelete){
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response = gson.toJson(e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response = "Bad Request: "+e.getMessage();
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(response);
        printWriter.flush();
    }

    public void setHeaders(HttpServletResponse resp){
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }
}
