package org.example.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.exception.NotFoundException;
import org.example.service.GenreService;
import org.example.servlet.dto.GenreIncomingDto;
import org.example.servlet.dto.GenreOutGoingDto;
import org.example.servlet.dto.MovieLimitedDto;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenreServletTest {
    @Mock
    private static GenreService mockGenreService;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @InjectMocks
    private static GenreServlet genreServlet;

    private StringWriter stringWriter;
    private PrintWriter writer;

    private Gson gson;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws IOException {
        closeable = MockitoAnnotations.openMocks(this);
        genreServlet = new GenreServlet();
        genreServlet.setGenreService(mockGenreService);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        gson = new Gson();
        when(mockResponse.getWriter()).thenReturn(writer);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();  // Закрытие моков
    }

    @Test
    void doGetAllGenres() throws IOException {
        // Подготовка данных
        List<GenreOutGoingDto> mockGenreList = Arrays.asList(
                new GenreOutGoingDto(1L, "Боевик", Collections.singletonList(new MovieLimitedDto(1L, "Матрица"))),
                new GenreOutGoingDto(2L, "Драма", Collections.singletonList(new MovieLimitedDto(2L, "Крёстный отец"))),
                new GenreOutGoingDto(12L, "История", Collections.singletonList(new MovieLimitedDto(7L, "Гладиатор")))
        );

        // Настройка поведения мока
        when(mockGenreService.findAll()).thenReturn(mockGenreList);
        when(mockRequest.getPathInfo()).thenReturn(null);

        // Выполнение doGet
        genreServlet.doGet(mockRequest, mockResponse);

        // Проверка
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockGenreService).findAll();

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = gson.toJson(mockGenreList);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void doGetGenreById() throws IOException, NotFoundException {

        Long genreId = 5L;
        GenreOutGoingDto mockGenre = new GenreOutGoingDto(5L, "Боевик", Collections.singletonList(new MovieLimitedDto(1L, "Матрица")));

        when(mockRequest.getPathInfo()).thenReturn("/" + genreId);
        when(mockGenreService.findById(genreId)).thenReturn(mockGenre);

        genreServlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockGenreService).findById(genreId);

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = gson.toJson(mockGenre);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void doGetGenreByIdNotFound() throws IOException, NotFoundException {

        Long genreId = 5L;

        when(mockRequest.getPathInfo()).thenReturn("/" + genreId);
        when(mockGenreService.findById(genreId)).thenThrow(new NotFoundException("Genre not found"));

        genreServlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(mockGenreService).findById(genreId);

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = gson.toJson("Genre not found");

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void doGetBadRequest() throws IOException, NotFoundException {

        String invalidPathInfo = "/invalid"; // Симуляция неправильного пути

        // Настройка поведения мока для выброса исключения
        when(mockRequest.getPathInfo()).thenReturn(invalidPathInfo);
        // Эмуляция выброса исключения RuntimeException
        when(mockGenreService.findById(anyLong())).thenThrow(new RuntimeException("Unexpected error"));

        genreServlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST); // Проверка что установлен статус 400

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = "Bad Request";

        assertEquals(expectedResponse, actualResponse); // Проверка, что ответ содержит сообщение "Bad Request"
    }

    @Test
    void doPost() throws IOException {

        GenreIncomingDto genreIncomingDto = new GenreIncomingDto("Комедия");
        GenreOutGoingDto savedGenre = new GenreOutGoingDto(3L, "Комедия", List.of());

        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(genreIncomingDto))));
        when(mockGenreService.save(any(GenreIncomingDto.class))).thenReturn(savedGenre);

        genreServlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_CREATED);
        verify(mockGenreService).save(any(GenreIncomingDto.class));

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = gson.toJson(savedGenre);

        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    void doPostIllegalRequest() throws IOException {

        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(""))); // Пустое тело запроса

        genreServlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = "Illegal Request";

        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    void doPostBadRequest() throws IOException {

        GenreIncomingDto genreIncomingDto = new GenreIncomingDto("Комедия");

        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(genreIncomingDto))));
        when(mockGenreService.save(any(GenreIncomingDto.class))).thenThrow(new RuntimeException("Unexpected error"));

        genreServlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = "Bad Request";

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void doPut() throws IOException, NotFoundException {

        GenreIncomingDto genreIncomingDto = new GenreIncomingDto("Триллер");
        GenreOutGoingDto updatedGenre = new GenreOutGoingDto(3L, "Триллер", List.of());

        when(mockRequest.getPathInfo()).thenReturn("/3");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(genreIncomingDto))));
        when(mockGenreService.update(any(GenreIncomingDto.class), eq(3L))).thenReturn(updatedGenre);

        genreServlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockGenreService).update(any(GenreIncomingDto.class), eq(3L));

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = gson.toJson(updatedGenre);

        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    void doPutNotFound() throws IOException, NotFoundException {

        GenreIncomingDto genreIncomingDto = new GenreIncomingDto("Триллер");

        // Настройка поведения мока для выброса NotFoundException
        Long invalidId = 3L;
        when(mockRequest.getPathInfo()).thenReturn("/" + invalidId);
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(genreIncomingDto))));
        when(mockGenreService.update(any(GenreIncomingDto.class), eq(invalidId))).thenThrow(new NotFoundException("Genre not found"));

        genreServlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = "Not Found: Genre not found";

        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    void doPutBadRequest() throws IOException, NotFoundException {

        when(mockRequest.getPathInfo()).thenReturn("/3");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(new GenreIncomingDto("Триллер")))));

        when(mockGenreService.update(any(GenreIncomingDto.class), eq(3L))).thenThrow(new RuntimeException("Unexpected error"));

        genreServlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = "Bad Request: Unexpected error";

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void doDelete() throws IOException, NotFoundException {

        when(mockRequest.getPathInfo()).thenReturn("/3");
        when(mockGenreService.delete(3L)).thenReturn(true);

        genreServlet.doDelete(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
        verify(mockGenreService).delete(3L);

        writer.flush();
        String actualResponse = stringWriter.toString();

        // Поскольку ответ не возвращает тело, он должен быть пустым
        assertEquals("", actualResponse);
    }
    @Test
    void doDeleteNotFound() throws IOException, NotFoundException {

        Long genreId = 3L;

        when(mockRequest.getPathInfo()).thenReturn("/" + genreId);
        when(mockGenreService.delete(genreId)).thenThrow(new NotFoundException("Genre not found"));

        genreServlet.doDelete(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = gson.toJson("Genre not found");

        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    void doDeleteBadRequest() throws IOException, NotFoundException {

        Long genreId = 3L;

        // Настройка мока для симуляции исключения
        when(mockRequest.getPathInfo()).thenReturn("/" + genreId);
        when(mockGenreService.delete(genreId)).thenThrow(new RuntimeException("Unexpected error"));

        // Вызов метода doDelete
        genreServlet.doDelete(mockRequest, mockResponse);

        // Проверка что установлен статус 400
        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        writer.flush();
        String actualResponse = stringWriter.toString();
        String expectedResponse = "Bad Request: Unexpected error";

        // Проверка что тело ответа содержит сообщение об ошибке
        assertEquals(expectedResponse, actualResponse);
    }
}