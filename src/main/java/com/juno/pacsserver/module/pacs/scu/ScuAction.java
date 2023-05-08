package com.juno.pacsserver.module.pacs.scu;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.DimseRSP;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ScuAction {
    DimseRSP cEcho() throws IOException, InterruptedException;
    void cMove();
    void cFind(Attributes request);
    void cStore(List<MultipartFile> multipartFiles);
    void cGet();
}
