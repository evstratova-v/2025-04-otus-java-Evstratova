package ru.otus.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import ru.otus.crm.dto.ClientDto;
import ru.otus.crm.service.DBServiceClient;

@SuppressWarnings({"java:S1989"})
public class ClientsApiServlet extends HttpServlet {

    private final transient DBServiceClient dbServiceClient;
    private final transient Gson gson;

    public ClientsApiServlet(DBServiceClient dbServiceClient, Gson gson) {
        this.dbServiceClient = dbServiceClient;
        this.gson = gson;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClientDto clientDto = gson.fromJson(request.getReader(), ClientDto.class);
        ClientDto savedClient = dbServiceClient.saveClient(clientDto);
        response.setContentType("application/json");
        response.getOutputStream().print(gson.toJson(savedClient));
    }
}
