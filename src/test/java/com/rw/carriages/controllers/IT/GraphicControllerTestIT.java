package com.rw.carriages.controllers.IT;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.client.MockMvcClientHttpRequestFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public class GraphicControllerTestIT {
    @Value("${service.version}")
    String version;

    @Autowired
    private WebApplicationContext wac;

    private RestTemplate restTemplate;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(this.wac).build();
        MockMvcClientHttpRequestFactory requestFactory = new MockMvcClientHttpRequestFactory(mockMvc);
        restTemplate = new RestTemplate(requestFactory);
    }

    @Test
    public void testGetCarriagesGraphic() {

    }

}
