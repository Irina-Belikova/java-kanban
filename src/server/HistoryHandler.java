package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            List<Task> tasks = manager.getHistory();
            String jsonString = gson.toJson(tasks);
            sendText(exchange, jsonString);
        } else {
            sendError(exchange, "Метод " + method + " не поддерживается.");
        }
    }
}
