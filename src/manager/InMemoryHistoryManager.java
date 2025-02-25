package manager;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> mapOfHistory = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void add(Task task) {
        if (Objects.isNull(task)) {
            return;
        }

        if (mapOfHistory.containsKey(task.getId())) {
            remove(task.getId());
        }
        Node<Task> newTail = linkLast(task.getOldTask());
        mapOfHistory.put(task.getId(), newTail);
    }

    @Override
    public void remove(int id) {
        if (!mapOfHistory.containsKey(id)) {
            return;
        }
        Node<Task> node = mapOfHistory.remove(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private Node<Task> linkLast(Task task) {
        if (head == null) {
            head = new Node<>(null, task, null);
            return head;
        } else if (tail == null) {
            tail = new Node<>(head, task, null);
            head.next = tail;
            return tail;
        }
        Node<Task> newTail = new Node<>(tail, task, null);
        tail.next = newTail;
        tail = newTail;
        return tail;
    }

    private void removeNode(Node<Task> node) {
        Node<Task> followNode = node.next;
        Node<Task> earlyNode = node.prev;

        if (earlyNode == null && followNode == null) {
            head = null;
        } else if (earlyNode == null) {
            followNode.prev = null;
            head = followNode;
        } else if (followNode == null) {
            earlyNode.next = null;
            tail = earlyNode;
        } else {
            earlyNode.next = followNode;
            followNode.prev = earlyNode;
        }
    }

    public List<Task> getTasks() {
        final List<Task> historyList = new ArrayList<>();
        Node<Task> node = head;

        while (Objects.nonNull(node)) {
            historyList.add(node.task);
            node = node.next;
        }
        return historyList;
    }

    private static class Node<T> {
        Task task;
        Node<T> next;
        Node<T> prev;

        public Node(Node<T> prev, Task task, Node<T> next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }
}
