package org.example.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SqlDateSerializer implements JsonSerializer<java.sql.Date> {
    // Явно задаем формат для сериализации даты
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    @Override
    public JsonElement serialize(java.sql.Date src, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
        // Преобразование java.sql.Date в строку формата "yyyy-MM-dd"
        return new JsonPrimitive(dateFormat.format(src));
    }
}