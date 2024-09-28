package org.example.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.config.SqlDateDeserializer;
import org.example.config.SqlDateSerializer;
import org.example.exception.NotFoundException;
import org.example.service.ActorService;
import org.example.servlet.dto.ActorIncomingDto;
import org.example.servlet.dto.ActorOutGoingDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActorServletTest {
    @Mock
    private ActorService mockActorService;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @InjectMocks
    private ActorServlet actorServlet;

    private StringWriter stringWriter;
    private PrintWriter printWriter;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.sql.Date.class, new SqlDateDeserializer()) // Десериализация
            .registerTypeAdapter(java.sql.Date.class, new SqlDateSerializer())   // Сериализация
            .create();
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws IOException {
        closeable = MockitoAnnotations.openMocks(this);
        actorServlet = new ActorServlet();
        actorServlet.setActorService(mockActorService);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(mockResponse.getWriter()).thenReturn(printWriter);
    }
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();  // Закрытие моков
    }

    @Test
    void doGetAllActors() throws IOException {
        List<ActorOutGoingDto> mockActorsList = Arrays.asList(
                new ActorOutGoingDto(1L, "Эмилия", "Кларк", java.sql.Date.valueOf("1986-10-23"), List.of()),
                new ActorOutGoingDto(2L, "Алфи", "Аллен", java.sql.Date.valueOf("1986-09-12"), List.of())
        );

        when(mockActorService.findAll()).thenReturn(mockActorsList);
        when(mockRequest.getPathInfo()).thenReturn(null);

        actorServlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockActorService).findAll();

        printWriter.flush();
        String actualResponce = stringWriter.toString();
        String expectedResponce = gson.toJson(mockActorsList);

        assertEquals(expectedResponce, actualResponce);
    }

    @Test
    void doGetActorById() throws IOException, NotFoundException {
        Long id = 7L;
        ActorOutGoingDto mockActor = new ActorOutGoingDto(7L, "Алфи", "Аллен", java.sql.Date.valueOf("1986-09-12"), List.of());

        when(mockRequest.getPathInfo()).thenReturn("/" + id);
        when(mockActorService.findById(id)).thenReturn(mockActor);

        actorServlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockActorService).findById(id);

        printWriter.flush();
        String actualResponce = stringWriter.toString();
        String expectedResponce = gson.toJson(mockActor);
        assertEquals(expectedResponce, actualResponce);
    }

    @Test
    void doGetByIdNotFound() throws IOException, NotFoundException {
        Long id = 8L;

        when(mockRequest.getPathInfo()).thenReturn("/" + id);
        when(mockActorService.findById(id)).thenThrow(new NotFoundException("Actor not found"));

        actorServlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(mockActorService).findById(id);

        printWriter.flush();
        String actualResponce = stringWriter.toString();
        String expectedResponce = gson.toJson("Actor not found");

        assertEquals(expectedResponce, actualResponce);
    }

    @Test
    void doGetBadRequest() throws IOException, NotFoundException {
        String invalidPathInfo = "/invalid";

        when(mockRequest.getPathInfo()).thenReturn(invalidPathInfo);
        when(mockActorService.findById(anyLong())).thenThrow(new RuntimeException("Unexpected error"));

        actorServlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        printWriter.flush();
        String actualResponce = stringWriter.toString();
        String expectedResponce = "Bad Request";

        assertEquals(expectedResponce, actualResponce);
    }

    @Test
    void doDelete() throws NotFoundException, IOException {
        when(mockRequest.getPathInfo()).thenReturn("/33");
        when(mockActorService.delete(33L)).thenReturn(true);

        actorServlet.doDelete(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
        verify(mockActorService).delete(33L);

        printWriter.flush();
        String actualResponce = stringWriter.toString();
        assertEquals("", actualResponce);
    }

    @Test
    void doDeleteNotFound() throws IOException, NotFoundException {
        Long id = 8L;
        when(mockRequest.getPathInfo()).thenReturn("/" + id);
        when(mockActorService.delete(id)).thenThrow(new NotFoundException("Actor not found"));

        actorServlet.doDelete(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);

        verify(mockActorService).delete(id);
        printWriter.flush();
        String actualResponce = stringWriter.toString();
        String expectedResponce = "Actor not found";

        assertEquals(expectedResponce, actualResponce);
    }

    @Test
    void doDeleteBadRequest() throws IOException, NotFoundException {
        Long id = 8L;

        when(mockRequest.getPathInfo()).thenReturn("/" + id);
        when(mockActorService.delete(id)).thenThrow(new RuntimeException("Unexpected error"));

        actorServlet.doDelete(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        stringWriter.flush();
        String actualResponce = stringWriter.toString();
        String expectedResponce = "Bad Request: Unexpected error";
        assertEquals(expectedResponce, actualResponce);
    }

    @Test
    void doPost() throws IOException {
        ActorIncomingDto actorIncomingDto = new ActorIncomingDto("Эмилия", "Кларк", java.sql.Date.valueOf("1986-10-23"));
        ActorOutGoingDto savedActor = new ActorOutGoingDto(1L, "Эмилия", "Кларк", java.sql.Date.valueOf("1986-10-23"), List.of());

        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(actorIncomingDto))));
        when(mockActorService.save(any(ActorIncomingDto.class))).thenReturn(savedActor);

        actorServlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_CREATED);
        verify(mockActorService).save(any(ActorIncomingDto.class));

        printWriter.flush();
        String actualResponce = stringWriter.toString();
        String expectedResponce = gson.toJson(savedActor);
        assertEquals(expectedResponce, actualResponce);
    }

    @Test
    void doPostIllegalRequest() throws IOException {
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(""))); // Пустое тело запроса

        actorServlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        printWriter.flush();
        String actualResponce = stringWriter.toString();
        String expectedResponce = "Illegal Request";
        assertEquals(expectedResponce, actualResponce);
    }

    @Test
    void doPostBadRequest() throws IOException {
        ActorIncomingDto actorIncomingDto = new ActorIncomingDto("Эмилия", "Кларк", java.sql.Date.valueOf("1986-10-23"));

        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(actorIncomingDto))));
        when(mockActorService.save(any(ActorIncomingDto.class))).thenThrow(new RuntimeException("Unexpected error"));

        actorServlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        printWriter.flush();
        String actualResponce = stringWriter.toString();
        String expectedResponce = "Bad Request: Unexpected error";
        assertEquals(expectedResponce, actualResponce);
    }

    @Test
    void doPut() throws IOException, NotFoundException {
        ActorIncomingDto actorIncomingDto = new ActorIncomingDto("Эмилия", "Кларк", java.sql.Date.valueOf("1986-10-23"));
        ActorOutGoingDto updatedActor = new ActorOutGoingDto(1L, "Эмилия", "Кларк", java.sql.Date.valueOf("1986-10-23"), List.of());

        when(mockRequest.getPathInfo()).thenReturn("/1");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(actorIncomingDto))));
        when(mockActorService.update(any(ActorIncomingDto.class), eq(1L))).thenReturn(updatedActor);

        actorServlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockActorService).update(any(ActorIncomingDto.class), eq(1L));

        printWriter.flush();
        String actualResponce = stringWriter.toString();
        String expectedResponce = gson.toJson(updatedActor);
        assertEquals(expectedResponce, actualResponce);
    }

    @Test
    void doPutNotFound() throws IOException, NotFoundException {
        ActorIncomingDto actorIncomingDto = new ActorIncomingDto("Эмилия", "Кларк", java.sql.Date.valueOf("1986-10-23"));

        Long invalidId = 333L;
        when(mockRequest.getPathInfo()).thenReturn("/"+invalidId);
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(actorIncomingDto))));
        when(mockActorService.update(any(ActorIncomingDto.class), eq(invalidId))).thenThrow(new NotFoundException("Actor not found"));

        actorServlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);

        printWriter.flush();
        String actualResponce = stringWriter.toString();
        String expectedResponse = "Actor not found";
        assertEquals(expectedResponse, actualResponce);
    }

    @Test
    void doPutBadRequest() throws IOException, NotFoundException {
        ActorIncomingDto actorIncomingDto = new ActorIncomingDto("Эмилия", "Кларк", java.sql.Date.valueOf("1986-10-23"));

        when(mockRequest.getPathInfo()).thenReturn("/1");
        when(mockRequest.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(actorIncomingDto))));
        when(mockActorService.update(any(ActorIncomingDto.class), eq(1L))).thenThrow(new RuntimeException("Unexpected error"));

        actorServlet.doPut(mockRequest, mockResponse);

        verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        printWriter.flush();
        String actualResponce = stringWriter.toString();
        String expectedResponce = "Bad Request: Unexpected error";
        assertEquals(expectedResponce, actualResponce);
    }
}