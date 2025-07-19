package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import ru.otus.model.Measurement;

public class ResourcesFileLoader implements Loader {

    private final String fileName;

    private final ObjectMapper mapper;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
        this.mapper = new JsonMapper();
    }

    @Override
    public List<Measurement> load() {
        // читает файл, парсит и возвращает результат
        try (var inputStream = ResourcesFileLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new FileProcessException("Файл '%s' не найден".formatted(fileName));
            }
            byte[] bytes = inputStream.readAllBytes();
            var measurementArrays = mapper.readValue(bytes, Measurement[].class);
            return Arrays.asList(measurementArrays);
        } catch (IOException e) {
            throw new FileProcessException(e);
        }
    }
}
