package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals(GET_TASK)) {
            List<Task> tasks = manager.getHistory();
            String jsonString = gson.toJson(tasks);
            sendText(exchange, jsonString);
        } else {
            sendError(exchange, "Метод " + method + " не поддерживается.");
        }
    }
}
