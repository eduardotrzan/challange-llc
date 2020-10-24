package com.challenge.llc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

import javax.annotation.security.PermitAll;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.llc.dto.request.DistributePayoutDto;
import com.challenge.llc.dto.response.PayoutDistributionDto;
import com.challenge.llc.service.mediator.DistributionMediator;

@Api(value = "Payout Distribution")
@RestController
@RequestMapping({ "/v1" })
@RequiredArgsConstructor
public class PayoutDistributionController {

    private final DistributionMediator distributionMediator;

    @ApiOperation(value = "Distributes a payout.", response = PayoutDistributionDto.class)
    @ApiResponse(code = 200, message = "Successfully distributes a payout.")
    @PermitAll
    @PostMapping(
            value = "/distribution/payout",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public PayoutDistributionDto distribute(@Validated @RequestBody DistributePayoutDto request) {
        return this.distributionMediator.distribute(request.getPayout(), null);
    }

    @ApiOperation(value = "Distributes a payout.", response = PayoutDistributionDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully distributes a payout for a Company."),
            @ApiResponse(code = 401, message = "You are not authorized to distribute.")
    })
    @PreAuthorize("@payoutDistributionAuthEvaluator.canDistribute(#companyUuid)")
    @PostMapping(
            value = "/{companyUuid}/distribution/payout",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus
    public PayoutDistributionDto distribute(
            @PathVariable(value = "companyUuid") UUID companyUuid,
            @Validated @RequestBody DistributePayoutDto request) {
        return this.distributionMediator.distribute(request.getPayout(), companyUuid);
    }
}
