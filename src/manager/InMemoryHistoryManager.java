package manager;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> mapOfHistory = new HashMap<>();
    Node head;
    Node tail;

    @Override
    public void add(Task task) {
        if (Objects.isNull(task)) {
            return;
        }

        if (mapOfHistory.containsKey(task.getId())) {
            remove(task.getId());
        }
        Node newTail = linkLast(task.getOldTask());
        mapOfHistory.put(task.getId(), newTail);
    }

    @Override
    public void remove(int id) {
        if (!mapOfHistory.containsKey(id)) {
            return;
        }
        Node node = mapOfHistory.remove(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public Node linkLast(Task task) {
        if (head == null) {
            head = new Node(null, task, null);
            return head;
        } else if (tail == null) {
            tail = new Node(head, task, null);
            head.next = tail;
            return tail;
        }
        Node newTail = new Node(tail, task, null);
        tail.next = newTail;
        tail = newTail;
        return tail;
    }

    public void removeNode(Node node) {
        Node followNode = node.next;
        Node earlyNode = node.prev;

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
        Node node = head;

        while (Objects.nonNull(node)) {
            historyList.add(node.task);
            node = node.next;
        }
        return historyList;
    }

    public static class Node {
        Task task;
        Node next;
        Node prev;

        public Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }
}
