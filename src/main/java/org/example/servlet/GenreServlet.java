package org.example.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.exception.NotFoundException;
import org.example.service.GenreService;
import org.example.service.impl.GenreServiceImpl;
import org.example.servlet.dto.GenreIncomingDto;
import org.example.servlet.dto.GenreOutGoingDto;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = {"/genre/*"})
public class GenreServlet extends HttpServlet {
    private transient Gson gson = new Gson();
    private transient GenreService genreService;

    public GenreServlet() {}

    public GenreServlet(GenreService genreService) {
        this.genreService = genreService;
    }

    @Override
    public void init() throws ServletException {
        if (genreService == null) {
            this.genreService = new GenreServiceImpl();  // Only initialize if not already set
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setHeaders(resp);

        String response = "";
        try{
            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                List<GenreOutGoingDto> genreOutGoingDtoList = genreService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                response = gson.toJson(genreOutGoingDtoList);
            }
            else{
                String[] paths = pathInfo.split("/");
                Long id = Long.parseLong(paths[1]);
                GenreOutGoingDto genreOutGoingDto = genreService.findById(id);
                resp.setStatus(HttpServletResponse.SC_OK);
                response = gson.toJson(genreOutGoingDto);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response = gson.toJson(e.getMessage());
        }
        catch (Exception e){
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
            GenreIncomingDto genreIncomingDto = gson.fromJson(req.getReader(), GenreIncomingDto.class);
            if(genreIncomingDto != null){
                response = gson.toJson(genreService.save(genreIncomingDto));
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
        try{
            String[] paths = req.getPathInfo().split("/");
            Long id = Long.parseLong(paths[1]);
            GenreIncomingDto genreIncomingDto = gson.fromJson(req.getReader(), GenreIncomingDto.class);
            response = gson.toJson(genreIncomingDto);
            if(genreIncomingDto != null){
                resp.setStatus(HttpServletResponse.SC_OK);
                response = gson.toJson(genreService.update(genreIncomingDto, id));
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response = "Not Found: "+e.getMessage();
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
            boolean isDelete = genreService.delete(id);
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

    public void setHeaders(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }
}

