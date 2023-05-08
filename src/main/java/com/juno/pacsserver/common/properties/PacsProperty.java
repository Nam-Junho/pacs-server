package com.juno.pacsserver.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "pacs")
@Configuration
public class PacsProperty {

    private String aet;

    private String bindAddress;

    private int port;
}
