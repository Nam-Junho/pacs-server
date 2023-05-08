package com.juno.pacsserver.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ScuDto(String hostName, int port, String callingAET, String calledAET, List<MultipartFile> files) {
}
