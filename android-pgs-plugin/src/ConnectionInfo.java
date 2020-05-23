package org.godotengine.godot;

import java.util.Objects;

public class ConnectionInfo {
    private boolean connected;
    private String accountId = "";
    private String token = "";

    ConnectionInfo(boolean connected, String accountId, String token) {
        this.connected = connected;
        this.accountId = accountId;
        this.token = token;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getToken() {
        return token;
    }

    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        ConnectionInfo that = (ConnectionInfo) object;
        return connected == that.connected &&
                java.util.Objects.equals(accountId, that.accountId) &&
                java.util.Objects.equals(token, that.token);
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), connected, accountId, token);
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "ConnectionInfo{" +
                "connected=" + connected +
                ", accountId='" + accountId + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}