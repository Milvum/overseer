package com.milvum.overseer.models;

/**
 * .
 */

public class Notification {
    private boolean success = false;

    public Notification(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
