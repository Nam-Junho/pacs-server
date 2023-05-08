package com.juno.pacsserver.module.pacs.scp;

import com.juno.pacsserver.common.exception.CustomException;
import com.juno.pacsserver.common.properties.PacsProperty;
import com.juno.pacsserver.module.pacs.scp.impl.AssociationMonitorImpl;
import com.juno.pacsserver.module.pacs.scp.impl.ConnectionMonitorImpl;
import com.juno.pacsserver.module.pacs.scp.impl.CustomCStoreSCP;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.net.ApplicationEntity;
import org.dcm4che3.net.Connection;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.TransferCapability;
import org.dcm4che3.net.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executors;

import static com.juno.pacsserver.common.contants.Constants.Pacs.*;

@Slf4j
@Getter
@Component
public class ServiceClassProvider {
    private final Device device;
    private Connection conn;
    private ApplicationEntity ae;

    ServiceClassProvider(@Autowired PacsProperty property) {
        device = new Device(DEVICE_NAME_SCP);
        device.addConnection(getConnection(property));
        device.addApplicationEntity(getApplicationEntity(property));
        device.setAssociationMonitor(new AssociationMonitorImpl());
        device.setConnectionMonitor(new ConnectionMonitorImpl());
        device.setDimseRQHandler(createServiceRegistry());
        device.setExecutor(Executors.newCachedThreadPool());
        device.setScheduledExecutor(Executors.newSingleThreadScheduledExecutor());
        start();
    }

    private Connection getConnection(PacsProperty property) {
        conn = new Connection(null, LOCAL_HOST, property.getPort());
        conn.setBindAddress(property.getBindAddress());
        conn.setMaxOpsInvoked(0);
        conn.setMaxOpsPerformed(0);
        return conn;
    }

    private ApplicationEntity getApplicationEntity(PacsProperty property) {
        ae = new ApplicationEntity(property.getAet());
        ae.addConnection(conn);
        ae.addTransferCapability(new TransferCapability(null, SCP_CLASS, TransferCapability.Role.SCP, TRANSFER_SYNTAX));
        return ae;
    }

    private DicomServiceRegistry createServiceRegistry() {
        DicomServiceRegistry serviceRegistry = new DicomServiceRegistry();
        serviceRegistry.addDicomService(new BasicCEchoSCP());
        serviceRegistry.addDicomService(new BasicCFindSCP());
        serviceRegistry.addDicomService(new BasicCMoveSCP());
        serviceRegistry.addDicomService(new CustomCStoreSCP());
        return serviceRegistry;
    }

    private void start() {
        try {
            device.bindConnections();
        } catch (IOException | GeneralSecurityException e) {
            throw new CustomException(e);
        }
    }
}
