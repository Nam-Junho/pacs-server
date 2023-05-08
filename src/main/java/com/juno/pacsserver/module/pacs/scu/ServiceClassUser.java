package com.juno.pacsserver.module.pacs.scu;

import com.juno.pacsserver.common.exception.CustomException;
import com.juno.pacsserver.common.util.ContextUtils;
import com.juno.pacsserver.dto.ScuDto;
import com.juno.pacsserver.module.pacs.scu.impl.DataWriterImpl;
import com.juno.pacsserver.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.UID;
import org.dcm4che3.net.*;
import org.dcm4che3.net.pdu.AAssociateRQ;
import org.dcm4che3.net.pdu.PresentationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.juno.pacsserver.common.contants.Constants.Pacs.DEVICE_NAME_SCU;
import static com.juno.pacsserver.common.contants.Constants.Pacs.SCU_BIND_ADDRESS;
import static java.nio.file.Files.delete;

@Slf4j
@RequiredArgsConstructor
public class ServiceClassUser implements ScuAction {
    private ScuDto scu;
    private final Device device;
    private Connection conn;
    private ApplicationEntity ae;
    private Association as;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private final List<String> presentationContextList = List.of(
            UID.Verification,
            UID.PatientRootQueryRetrieveInformationModelFind,
            UID.StudyRootQueryRetrieveInformationModelMove,
            UID.MRImageStorage,
            UID.CTImageStorage,
            UID.SecondaryCaptureImageStorage
    );

    public ServiceClassUser(ScuDto scuDto) {
        this(scuDto.hostName(), scuDto.port(), scuDto.callingAET(), scuDto.calledAET(), scuDto.files());
    }

    public ServiceClassUser(String hostName, int port, String callingAET, String calledAET, List<MultipartFile> files) {
        scu = new ScuDto(hostName, port, callingAET, calledAET, files);
        device = new Device(DEVICE_NAME_SCU);
        device.addConnection(getConnection());
        device.addApplicationEntity(getApplicationEntity());
        device.setExecutor(executor);
        device.setScheduledExecutor(scheduledExecutor);
    }

    private Connection getConnection() {
        conn = new Connection(null, scu.hostName(), scu.port());
        conn.setClientBindAddress(SCU_BIND_ADDRESS);
        conn.setTlsCipherSuites();
        return conn;
    }

    private ApplicationEntity getApplicationEntity() {
        ae = new ApplicationEntity(scu.callingAET());
        ae.addConnection(conn);
        return ae;
    }

    private AAssociateRQ getAAssociateRQ() {
        AAssociateRQ rq = new AAssociateRQ();
        rq.setCallingAET(ae.getAETitle());
        rq.setCalledAET(scu.calledAET());
        for (int idx = 0; idx < presentationContextList.size(); idx++) {
            rq.addPresentationContext(new PresentationContext(idx + 1, presentationContextList.get(idx), UID.ImplicitVRLittleEndian));
        }
        return rq;
    }

    private AAssociateRQ getAAssociateRQ(List<DataWriterImpl> files) {
        AAssociateRQ rq = new AAssociateRQ();
        rq.setCallingAET(ae.getAETitle());
        rq.setCalledAET(scu.calledAET());
        files.stream()
                .filter(f -> !rq.containsPresentationContextFor(f.getCuid(), f.getTsuid()))
                .forEach(f -> rq.addPresentationContext(new PresentationContext(rq.getNumberOfPresentationContexts() * 2 + 1, f.getCuid(), f.getTsuid())));
        return rq;
    }

    @Override
    public DimseRSP cEcho() {
        try {
            as = ae.connect(conn, getAAssociateRQ());
            return as.cecho();
        } catch (Exception e) {
            log.error("SCU cEcho Failed", e);
            throw new CustomException(e);
        } finally {
            close(true);
        }
    }

    @Override
    public void cMove() {

    }

    @Override
    public void cFind(Attributes request) {
//        Association as = ae.connect(mkConnection(hostName, port), mkAARQ(calledAET));
//        List<Attributes> resultList = new ArrayList<>();
//        try {
//            as.cfind(UID.PatientRootQueryRetrieveInformationModelFind, Priority.NORMAL, request, UID.ImplicitVRLittleEndian, getRSPHandler(as, resultList));
//        } catch (IOException e) {
//            log.error("Failed to invoke C-FIND-RQ to {}", calledAET, e);
//        }
//        as.waitForOutstandingRSP();
//        try {
//            as.release();
//        } catch (IOException ignored) {
//
//        }
//
//        return resultList;
    }

    @Override
    public void cStore(List<MultipartFile> multipartFiles) {
        List<DataWriterImpl> files = multipartFiles.stream()
                .map(ContextUtils.getBean(FileService.class)::toFile)
                .map(DataWriterImpl::new)
                .toList();

        try {
            as = ae.connect(conn, getAAssociateRQ(files));
            files.forEach(this::cStoreEach);
        } catch (Exception e) {
            log.error("SCU cStore Failed", e);
            throw new CustomException(e);
        } finally {
            close(false);
            deleteFiles(files);
        }
    }

    private void deleteFiles(List<DataWriterImpl> files) {
        files.forEach(f -> {
            try {
                delete(f.getFile().toPath());
            } catch (IOException e) {
                log.warn("Not exist file.");
            }
        });
    }

    private void cStoreEach(DataWriterImpl file) {
        try {
            as.cstore(file.getCuid(), file.getIuid(), Priority.NORMAL, file, file.getTsuid(), new DimseRSPHandler(as.nextMessageID()));
        } catch (IOException e) {
            throw new CustomException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(e);
        }
    }

    @Override
    public void cGet() {

    }

    public void close(boolean isEcho)  {
        if (as != null && as.isReadyForDataTransfer()) {
            associationClose(isEcho);
        }
        executor.shutdown();
        scheduledExecutor.shutdown();
    }

    private void associationClose(boolean isEcho) {
        if(!isEcho) {
            closeRsp();
        }

        release();
    }

    public void closeRsp() {
        try {
            as.waitForOutstandingRSP();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("SCU RSP unable close.", e);
        }
    }

    public void release() {
        try {
            as.release();
        } catch (IOException e) {
            log.info("SCU RSP unable release.", e);
        }
    }
}
