package com.juno.pacsserver.module.pacs.scp.impl;

import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.net.Connection;
import org.dcm4che3.net.ConnectionMonitor;

import java.net.Socket;

import static java.text.MessageFormat.format;

@Slf4j
public class ConnectionMonitorImpl implements ConnectionMonitor {
    @Override
    public void onConnectionEstablished(Connection conn, Connection remoteConn, Socket s) {
        log.info(format("ConnectionEstablished (ip: {0}, port: {1})", s.getInetAddress(), s.getPort()));
    }

    @Override
    public void onConnectionFailed(Connection conn, Connection remoteConn, Socket s, Throwable e) {
        log.info(format("ConnectionFailed (ip: {0}, port: {1})", s.getInetAddress(), s.getPort()));
    }

    @Override
    public void onConnectionRejectedBlacklisted(Connection conn, Socket s) {
        log.info(format("ConnectionRejectedBlacklisted (ip: {0}, port: {1})", s.getInetAddress(), s.getPort()));
    }

    @Override
    public void onConnectionRejected(Connection conn, Socket s, Throwable e) {
        log.info(format("ConnectionRejected (ip: {0}, port: {1})", s.getInetAddress(), s.getPort()));
    }

    @Override
    public void onConnectionAccepted(Connection conn, Socket s) {
        log.info(format("ConnectionAccepted (ip: {0}, port: {1})", s.getInetAddress(), s.getPort()));
    }
}
