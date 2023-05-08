package com.juno.pacsserver.service;

import com.juno.pacsserver.common.exception.CustomException;
import com.juno.pacsserver.common.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.PDVInputStream;
import org.dcm4che3.net.pdu.PresentationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import static com.juno.pacsserver.common.contants.Constants.File.ROOT_PATH;
import static java.util.stream.Collectors.joining;

@Slf4j
@Service
public class FileService {

    /**
     * 파일 저장
     */
    public void saveFile(Association as, PresentationContext pc, Attributes rq, PDVInputStream data) throws IOException {
        File file = File.createTempFile("DICOM_" + DateUtil.getStringDatetime(),".dcm", getDirectory());
        try (DicomOutputStream out = new DicomOutputStream(file)) {
            out.writeFileMetaInformation(getFmi(as, pc, rq));
            data.copyTo(out);
        } catch (Exception e) {
            log.error("[FileService error] File 저장 실패", e);
        }
    }

    /**
     * Connection 별 PCID 명으로 폴더 분리
     */
    private File getDirectory() throws IOException {
        Path path = Paths.get(getDirectoryPath(DateUtil.getStringTime())).toAbsolutePath().normalize();
        Files.createDirectories(path);
        return path.toFile();
    }

    /**
     * 파일 저장 위치
     */
    private String getDirectoryPath(String... paths) {
        String[] pathArray = new String[paths.length + 1];
        pathArray[0] = getRootPath();
        System.arraycopy(paths, 0, pathArray, 1, paths.length);
        return StringUtils.arrayToDelimitedString(pathArray, File.separator);
    }

    /**
     * Root path : ./FILE/${DATE}
     */
    private String getRootPath() {
        return Stream.of(ROOT_PATH, DateUtil.getStringDate())
                .collect(joining(File.separator, "." + File.separator, File.separator));
    }

    /**
     * Dicom Header 생성
     */
    private Attributes getFmi(Association as, PresentationContext pc, Attributes rq) {
        return as.createFileMetaInformation(
                rq.getString(Tag.AffectedSOPInstanceUID),
                rq.getString(Tag.AffectedSOPClassUID),
                pc.getTransferSyntax()
        );
    }

    /**
     * 파일 저장
     */
    public File toFile(MultipartFile multipartFile) {
        try {
            File file = File.createTempFile(Objects.requireNonNull(multipartFile.getOriginalFilename()), "", getDirectory());
            multipartFile.transferTo(file);
            return file;
        } catch (IOException e) {
            throw new CustomException(e);
        }
    }
}
