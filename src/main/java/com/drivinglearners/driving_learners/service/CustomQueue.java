package com.drivinglearners.driving_learners.service;

import com.drivinglearners.driving_learners.model.RenewalRequest;

public class CustomQueue {
    private RenewalRequest[] queue;
    private int front;
    private int rear;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public CustomQueue() {
        queue = new RenewalRequest[DEFAULT_CAPACITY];
        front = 0;
        rear = -1;
        size = 0;
    }

    public void add(RenewalRequest request) {
        if (size == queue.length) {
            resize();
        }
        rear = (rear + 1) % queue.length;
        queue[rear] = request;
        size++;
    }

    public RenewalRequest remove() {
        if (isEmpty()) {
            return null;
        }
        RenewalRequest request = queue[front];
        queue[front] = null;
        front = (front + 1) % queue.length;
        size--;
        return request;
    }

    public RenewalRequest peek() {
        if (isEmpty()) {
            return null;
        }
        return queue[front];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void resize() {
        RenewalRequest[] newQueue = new RenewalRequest[queue.length * 2];
        for (int i = 0; i < size; i++) {
            newQueue[i] = queue[(front + i) % queue.length];
        }
        front = 0;
        rear = size - 1;
        queue = newQueue;
    }

    public int size() {
        return size;
    }


    public RenewalRequest[] toArray() {
        RenewalRequest[] result = new RenewalRequest[size];
        for (int i = 0; i < size; i++) {
            result[i] = queue[(front + i) % queue.length];
        }
        return result;
    }
}