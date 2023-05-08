package com.juno.pacsserver.controller;

import com.juno.pacsserver.dto.ResponseDto;
import com.juno.pacsserver.dto.ScuDto;
import com.juno.pacsserver.module.pacs.scu.ServiceClassUser;
import lombok.RequiredArgsConstructor;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.DimseRSP;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scu/v1")
@RequiredArgsConstructor
public class ScuController {
    @PostMapping("/echo")
    public ResponseEntity<ResponseDto<DimseRSP>> echo(@RequestBody ScuDto scuDto) {
        return ResponseEntity.ok(new ResponseDto<>(
                "200",
                "ok",
                new ServiceClassUser(scuDto).cEcho()
        ));
    }

    @PostMapping("/store")
    public ResponseEntity<ResponseDto<Association>> store(ScuDto scuDto) {
        new ServiceClassUser(scuDto).cStore(scuDto.files());
        return ResponseEntity.ok(new ResponseDto<>(
                "200",
                "ok"
        ));
    }
}
