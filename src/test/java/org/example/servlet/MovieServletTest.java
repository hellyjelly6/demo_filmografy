package org.example.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.exception.NotFoundException;
import org.example.model.GenreEntity;
import org.example.service.MovieService;
import org.example.servlet.dto.ActorLimitedDto;
import org.example.servlet.dto.MovieIncomingDto;
import org.example.servlet.dto.MovieOutGoingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MovieServletTest {
    private static MovieService mockMovieService;
    private static HttpServletRequest mockRequest;
    private static HttpServletResponse mockResponse;
    private static MovieServlet movieServlet;

    private StringWriter stringWriter;
    private PrintWriter writer;

    private Gson gson;

    @BeforeEach
    void setUp() throws IOException {
        mockMovieService = Mockito.mock(MovieService.class);
        movieServlet = new MovieServlet(mockMovieService);
        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockResponse = Mockito.mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        gson = new Gson();
        when(mockResponse.getWriter()).thenReturn(writer);
    }

    @Test
    void doGetAll() throws IOException {
        // Подготовка данных
        List<MovieOutGoingDto> mockMovieList = Arrays.asList(
                new MovieOutGoingDto(1L, "Титаник", 1997, new GenreEntity(), List.of()),
                new MovieOutGoingDto(2L, "Интерстеллар", 2014, new GenreEntity(), List.of())
        );

        // Настройка поведения мока
        when(mockMovieService.findAll()).thenReturn(mockMovieList);
        when(mockRequest.getPathInfo()).thenReturn(null);

        // Выполнение doGet
        movieServlet.doGet(mockRequest, mockResponse);

        // Проверка
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockMovieService).findAll();

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = gson.toJson(mockMovieList);

        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    void doGetById() throws IOException, NotFoundException {

        Long id = 5L;
        MovieOutGoingDto mockMovie = new MovieOutGoingDto(1L, "Титаник", 1997, new GenreEntity(), List.of());

        when(mockRequest.getPathInfo()).thenReturn("/" + id);
        when(mockMovieService.findById(id)).thenReturn(mockMovie);

        movieServlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockMovieService).findById(id);

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = gson.toJson(mockMovie);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void doGetByIdNotFound() throws IOException, NotFoundException {

        Long id = 5L;

        when(mockRequest.getPathInfo()).thenReturn("/" + id);
        when(mockMovieService.findById(id)).thenThrow(new NotFoundException("Movie not found"));

        movieServlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(mockMovieService).findById(id);

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = gson.toJson("Movie not found");

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void doGetBadRequest() throws IOException, NotFoundException {

        String invalidPathInfo = "/invalid"; // Симуляция неправильного пути

        // Настройка поведения мока для выброса исключения
        when(mockRequest.getPathInfo()).thenReturn(invalidPathInfo);
        // Эмуляция выброса исключения RuntimeException
        when(mockMovieService.findById(anyLong())).thenThrow(new RuntimeException("Unexpected error"));

        movieServlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST); // Проверка что установлен статус 400

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = "Bad Request";

        assertEquals(expectedResponse, actualResponse); // Проверка, что ответ содержит сообщение "Bad Request"
    }

    @Test
    void doPost() throws IOException {

        MovieIncomingDto movieIncomingDto = new MovieIncomingDto("Титаник", 1997, new GenreEntity());
        MovieOutGoingDto savedMovie = new MovieOutGoingDto(1L, "Титаник", 1997, new GenreEntity(), List.of());

        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(movieIncomingDto))));
        when(mockMovieService.save(any(MovieIncomingDto.class))).thenReturn(savedMovie);

        movieServlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_CREATED);
        verify(mockMovieService).save(any(MovieIncomingDto.class));

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = gson.toJson(savedMovie);

        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    void doPostIllegalRequest() throws IOException {

        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(""))); // Пустое тело запроса

        movieServlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = "Illegal Request, movie is null";

        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    void doPostBadRequest() throws IOException {

        MovieIncomingDto movieIncomingDto = new MovieIncomingDto("Титаник", 1997, new GenreEntity());

        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(movieIncomingDto))));
        when(mockMovieService.save(any(MovieIncomingDto.class))).thenThrow(new RuntimeException("Unexpected error"));

        movieServlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = "Bad Request: Unexpected error";

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void doPut() throws IOException, NotFoundException {

        MovieIncomingDto movieIncomingDto = new MovieIncomingDto("Титаник", 1997, new GenreEntity());
        MovieOutGoingDto updatedMovie = new MovieOutGoingDto(3L, "Титаник", 1997, new GenreEntity(), List.of());

        when(mockRequest.getPathInfo()).thenReturn("/3");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(movieIncomingDto))));
        when(mockMovieService.update(any(MovieIncomingDto.class), eq(3L))).thenReturn(updatedMovie);

        movieServlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockMovieService).update(any(MovieIncomingDto.class), eq(3L));

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = gson.toJson(updatedMovie);

        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    void doPutNotFound() throws IOException, NotFoundException {

        MovieIncomingDto movieIncomingDto = new MovieIncomingDto("Титаник", 1997, new GenreEntity());

        // Настройка поведения мока для выброса NotFoundException
        Long invalidId = 3L;
        when(mockRequest.getPathInfo()).thenReturn("/" + invalidId);
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(movieIncomingDto))));
        when(mockMovieService.update(any(MovieIncomingDto.class), eq(invalidId))).thenThrow(new NotFoundException("Movie not found"));

        movieServlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = "Movie not found";

        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    void doPutBadRequest() throws IOException, NotFoundException {

        MovieIncomingDto movieIncomingDto = new MovieIncomingDto("Титаник", 1997, new GenreEntity());

        when(mockRequest.getPathInfo()).thenReturn("/3");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(movieIncomingDto))));

        when(mockMovieService.update(any(MovieIncomingDto.class), eq(3L))).thenThrow(new RuntimeException("Unexpected error"));

        movieServlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = "Bad Request: Unexpected error";

        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    void doPutIllegalRequest() throws IOException {
        when(mockRequest.getPathInfo()).thenReturn("/3");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(""))); // Пустое тело запроса

        movieServlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = "Illegal Request";

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void doPutSaveActors() throws IOException, NotFoundException {
        Long id = 3L;
        ActorLimitedDto[] actors = {
                new ActorLimitedDto(1L, "Леонардо", "ДиКаприо"),
                new ActorLimitedDto(2L, "Кейт", "Винслет")
        };
        MovieOutGoingDto updatedMovie = new MovieOutGoingDto(id, "Титаник", 1997, new GenreEntity(), Arrays.asList(actors));

        when(mockRequest.getPathInfo()).thenReturn("/actors/" + id);
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(actors))));
        when(mockMovieService.saveActorsForMovie(eq(id), any(ActorLimitedDto[].class))).thenReturn(updatedMovie);

        movieServlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockMovieService).saveActorsForMovie(eq(id), any(ActorLimitedDto[].class));

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = gson.toJson(updatedMovie);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void doPutSaveActorsEmpty() throws IOException {
        Long id = 3L;
        ActorLimitedDto[] actors = {};

        when(mockRequest.getPathInfo()).thenReturn("/actors/" + id);
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(actors))));

        movieServlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = "Illegal Request";
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void doPutInvalidPath() throws IOException {
        Long movieId = 3L;

        when(mockRequest.getPathInfo()).thenReturn("/invalidpath/" + movieId);

        movieServlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = "Not Found";

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void doPutSaveActorsBadRequest() throws IOException, NotFoundException {
        Long id = 3L;
        ActorLimitedDto[] actors = {
                new ActorLimitedDto(1L, "Леонардо", "ДиКаприо"),
                new ActorLimitedDto(2L, "Кейт", "Винслет")
        };

        when(mockRequest.getPathInfo()).thenReturn("/actors/" + id);
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(actors))));
        when(mockMovieService.saveActorsForMovie(eq(id), any(ActorLimitedDto[].class))).thenThrow(new RuntimeException("Unexpected error"));

        movieServlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = "Bad Request: Unexpected error";
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void doDelete() throws IOException, NotFoundException {

        when(mockRequest.getPathInfo()).thenReturn("/3");
        when(mockMovieService.delete(3L)).thenReturn(true);

        movieServlet.doDelete(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
        verify(mockMovieService).delete(3L);

        writer.flush();
        String actualResponse = stringWriter.toString();

        // Поскольку ответ не возвращает тело, он должен быть пустым
        assertEquals("", actualResponse);
    }


    @Test
    void doDeleteNotFound() throws IOException, NotFoundException {

        Long genreId = 3L;

        when(mockRequest.getPathInfo()).thenReturn("/" + genreId);
        when(mockMovieService.delete(genreId)).thenThrow(new NotFoundException("Movie not found"));

        movieServlet.doDelete(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = gson.toJson("Movie not found");

        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    void doDeleteBadRequest() throws IOException, NotFoundException {

        Long genreId = 3L;

        // Настройка мока для симуляции исключения
        when(mockRequest.getPathInfo()).thenReturn("/" + genreId);
        when(mockMovieService.delete(genreId)).thenThrow(new RuntimeException("Unexpected error"));

        // Вызов метода doDelete
        movieServlet.doDelete(mockRequest, mockResponse);

        // Проверка что установлен статус 400
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = "Bad Request: Unexpected error";

        // Проверка что тело ответа содержит сообщение об ошибке
        assertEquals(expectedResponse, actualResponse);
    }

}