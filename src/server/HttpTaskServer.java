package server;

import com.sun.net.httpserver.HttpServer;
import file.ManagerSaveException;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final int PORT = 8080;
    protected final HttpServer httpServer;
    protected final TaskManager manager;

    public HttpTaskServer(TaskManager manager) {
        try {
            this.manager = manager;
        } catch (ManagerSaveException exp) {
            throw new ManagerSaveException("Ошибка чтения сохранённых в менеджер данных.");
        }

        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TasksHandler(manager));
            httpServer.createContext("/subtasks", new SubtasksHandler(manager));
            httpServer.createContext("/epics", new EpicsHandler(manager));
            httpServer.createContext("/history", new HistoryHandler(manager));
            httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
        } catch (IOException exp) {
            throw new RuntimeException("Ошибка запуска сервера", exp);
        }
    }

    public void start() {
        try {
            System.out.println("HTTP-сервер запущен на порту:" + PORT);
            this.httpServer.start();
        } catch (Exception exp) {
            throw new RuntimeException("Ошибка запуска сервера.", exp);
        }
    }

    public void stop() {
        try {
            this.httpServer.stop(0);
            System.out.println("HTTP-сервер остановлен.");
        } catch (Exception exp) {
            throw new RuntimeException("Ошибка остановки сервера.", exp);
        }
    }

    public static void main(String[] args) {
        TaskManager tm = Managers.getDefault();

        HttpTaskServer server = new HttpTaskServer(tm);
        server.start();

        Task task1 = tm.addTasks(new Task("задача-1", "описание зд-1",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(30)));
        Task task2 = tm.addTasks(new Task("задача-2", "описание зд-2",
                LocalDateTime.of(2025, 1, 1, 12, 40), Duration.ofMinutes(30)));

        Epic epic1 = tm.addEpics(new Epic("эпик-1", "описание эпика-1"));
        Epic epic2 = tm.addEpics(new Epic("эпик-2", "описание эпика-2"));

        Subtask subtask1 = tm.addSubtasks(new Subtask("подзадача-1", "описание пзд-1",
                LocalDateTime.of(2025, 1, 1, 14, 0, 0, 0), Duration.ofMinutes(30), epic1.getId()));
        Subtask subtask2 = tm.addSubtasks(new Subtask("подзадача-2", "описание пзд-2",
                LocalDateTime.of(2025, 1, 2, 12, 0, 0, 0), Duration.ofMinutes(30), epic1.getId()));
        Subtask subtask3 = tm.addSubtasks(new Subtask("подзадача-3", "описание пзд-3",
                LocalDateTime.of(2025, 2, 1, 9, 0, 0, 0), Duration.ofMinutes(30), epic1.getId()));

        System.out.println(tm.getPrioritizedTasks());
    }
}
