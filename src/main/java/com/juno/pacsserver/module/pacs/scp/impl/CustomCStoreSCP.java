package com.juno.pacsserver.module.pacs.scp.impl;

import com.juno.pacsserver.common.util.ContextUtils;
import com.juno.pacsserver.service.FileService;
import com.juno.pacsserver.common.contants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.PDVInputStream;
import org.dcm4che3.net.pdu.PresentationContext;
import org.dcm4che3.net.service.BasicCStoreSCP;

import java.io.IOException;

@Slf4j
public class CustomCStoreSCP extends BasicCStoreSCP {
    public CustomCStoreSCP() {
        super(Constants.Pacs.SCP_CLASS);
    }

    @Override
    protected void store(Association as, PresentationContext pc, Attributes rq,
                         PDVInputStream data, Attributes rsp) throws IOException {
        ContextUtils.getBean(FileService.class).saveFile(as, pc, rq, data);
    }
}
