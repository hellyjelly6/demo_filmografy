package org.example.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.config.SqlDateDeserializer;
import org.example.config.SqlDateSerializer;
import org.example.exception.NotFoundException;
import org.example.service.ActorService;
import org.example.service.impl.ActorServiceImpl;
import org.example.servlet.dto.ActorIncomingDto;
import org.example.servlet.dto.ActorOutGoingDto;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = {"/actor/*"})
public class ActorServlet extends HttpServlet {
    transient ActorService actorService = new ActorServiceImpl();
    transient Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.sql.Date.class, new SqlDateDeserializer()) // Десериализация
            .registerTypeAdapter(java.sql.Date.class, new SqlDateSerializer())   // Сериализация
            .create();


    public void setActorService(ActorService actorService) {
        this.actorService = actorService;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        setHeaders(resp);

        String response = "";
        try{
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                List<ActorOutGoingDto> actorList = actorService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                response = gson.toJson(actorList);
            }
            else{
                String[] paths = req.getPathInfo().split("/");
                Long id = Long.parseLong(paths[1]);
                ActorOutGoingDto actor = actorService.findById(id);
                resp.setStatus(HttpServletResponse.SC_OK);
                response = gson.toJson(actor);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response = gson.toJson(e.getMessage());
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response = "Bad Request";
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
            String[] pathInfo = req.getPathInfo().split("/");
            Long id = Long.parseLong(pathInfo[1]);
            boolean isDelete = actorService.delete(id);
            if(isDelete){
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                response = "";
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response = "Bad Request: %s".formatted(e.getMessage());
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
            ActorIncomingDto actor = gson.fromJson(req.getReader(), ActorIncomingDto.class);
            if(actor != null){
                response = gson.toJson(actorService.save(actor));
                resp.setStatus(HttpServletResponse.SC_CREATED);
            }
            else{
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response = "Illegal Request";
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response = "Bad Request: " + e.getMessage();
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
            Long id = Long.parseLong(paths[paths.length - 1]);
            ActorIncomingDto actor = gson.fromJson(req.getReader(), ActorIncomingDto.class);
            if(actor != null){
                response = gson.toJson(actorService.update(actor, id));
                resp.setStatus(HttpServletResponse.SC_OK);
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

    public void setHeaders(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }
}
