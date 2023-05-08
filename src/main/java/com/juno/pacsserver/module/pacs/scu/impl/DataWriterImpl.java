package com.juno.pacsserver.module.pacs.scu.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.net.DataWriter;
import org.dcm4che3.net.PDVOutputStream;
import org.dcm4che3.util.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.Predicate;

@Slf4j
@Getter
public class DataWriterImpl implements DataWriter {
    final File file;
    private String iuid;
    private String cuid;
    private String tsuid;
    long fmiLength;

    public DataWriterImpl(File file) {
        this.file = file;
        try (DicomInputStream dis = new DicomInputStream(file)) {
            Attributes fmi = dis.readFileMetaInformation();
            if (fmi != null) { // DICOM Part 10 File
                fmiLength = dis.getPosition();
                cuid = fmi.getString(Tag.MediaStorageSOPClassUID);
                iuid = fmi.getString(Tag.MediaStorageSOPInstanceUID);
                tsuid = fmi.getString(Tag.TransferSyntaxUID);
            } else { // bare DICOM Data set without File Meta Information
                Attributes attrs = dis.readDataset(-1, findSopInstanceUID());
                attrs.setBytes(Tag.SOPInstanceUID, VR.UI, dis.readValue());
                cuid = attrs.getString(Tag.SOPClassUID);
                iuid = attrs.getString(Tag.SOPInstanceUID);
                tsuid = dis.getTransferSyntax();
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }

    private Predicate<DicomInputStream> findSopInstanceUID() {
        return o -> Integer.compareUnsigned(o.tag(), Tag.SOPInstanceUID) >= 0;
    }

    @Override
    @SuppressWarnings("java:S2674")
    public void writeTo(PDVOutputStream out, String tsuid) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            in.skip(fmiLength);
            StreamUtils.copy(in, out);
        }  catch (IOException e) {
            log.error("", e);
        }
    }
}