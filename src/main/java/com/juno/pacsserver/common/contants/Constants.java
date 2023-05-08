package com.juno.pacsserver.common.contants;

import lombok.experimental.UtilityClass;
import org.dcm4che3.data.UID;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class Constants {
    @UtilityClass
    public static class Pacs {
        public static final String LOCAL_HOST = "127.0.0.1";
        public static final String DEVICE_NAME_SCP = "hd-scp";
        public static final String DEVICE_NAME_SCU = "hd-scu";
        public static final String SCP_CLASS = "*";
        @SuppressWarnings("java:S2386")
        public static final String[] TRANSFER_SYNTAX = { UID.ImplicitVRLittleEndian };
        public static final String SCU_BIND_ADDRESS = "0.0.0.0";
    }

    @UtilityClass
    public static class File {
        public static final String ROOT_PATH = "files";
    }

    @UtilityClass
    public static class Date {
        public static final DateTimeFormatter FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");
        public static final DateTimeFormatter FORMATTER_YYYYMMDD_HHMMSS_SSSSSS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSSSSS");
        public static final DateTimeFormatter FORMATTER_HH = DateTimeFormatter.ofPattern("HH");
    }
}
