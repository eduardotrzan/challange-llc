package com.challenge.llc.server;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.challenge.llc.controller.PayoutDistributionController;
import com.challenge.llc.server.testutils.AbstractServerIT;

public class SmokeTest extends AbstractServerIT {

    @Autowired
    private PayoutDistributionController controller;

    @Test
    public void contextLoads() throws Exception {
        assertThat(this.controller).isNotNull();
    }
}
