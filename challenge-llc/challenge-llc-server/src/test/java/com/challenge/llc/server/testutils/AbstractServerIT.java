package com.challenge.llc.server.testutils;

import lombok.Getter;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.challenge.llc.server.Application;

@Getter
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                classes = { Application.class })
@ActiveProfiles("llc-server-junit")
public abstract class AbstractServerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;


}
