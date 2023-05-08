package com.juno.pacsserver.controller;

import com.juno.pacsserver.dto.ResponseDto;
import com.juno.pacsserver.module.pacs.scp.ServiceClassProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scp/v1")
@RequiredArgsConstructor
public class ScpController {
    private final ServiceClassProvider scp;

    @GetMapping("/status")
    public ResponseEntity<ResponseDto<String>> startup() {
        return ResponseEntity.ok(new ResponseDto<>("200", "ok"));
    }
}
