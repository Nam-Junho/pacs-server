package com.juno.pacsserver.module.pacs.scp.impl;

import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.AssociationListener;
import org.dcm4che3.net.AssociationMonitor;
import org.dcm4che3.net.pdu.AAssociateRJ;

import static java.text.MessageFormat.format;

@Slf4j
public class AssociationMonitorImpl implements AssociationMonitor, AssociationListener {
    @Override
    public void onAssociationEstablished(Association as) {
        log.info(format("AssociationEstablished (aet: {0}, hostName: {1})", as.getRemoteAET(), as.getRemoteHostName()));
    }

    @Override
    public void onAssociationFailed(Association as, Throwable e) {
        log.info(format("AssociationFailed (aet: {0}, hostName: {1})", as.getRemoteAET(), as.getRemoteHostName()));
    }

    @Override
    public void onAssociationRejected(Association as, AAssociateRJ aarj) {
        log.info(format("AssociationRejected (aet: {0}, hostName: {1})", as.getRemoteAET(), as.getRemoteHostName()));
    }

    @Override
    public void onAssociationAccepted(Association as) {
        log.info(format("AssociationAccepted (aet: {0}, hostName: {1})", as.getRemoteAET(), as.getRemoteHostName()));
    }

    @Override
    public void onClose(Association as) {
        log.info(format("Close (aet: {0}, hostName: {1})", as.getRemoteAET(), as.getRemoteHostName()));
    }
}
