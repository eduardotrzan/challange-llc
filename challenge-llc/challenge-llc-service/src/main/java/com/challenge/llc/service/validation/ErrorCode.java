package com.challenge.llc.service.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.challenge.generic.exceptions.IAppError;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements IAppError {

    DISTRIBUTION_PERSON_GROUP("Cannot distribute the amount={} as there are nobody for that group."),
    NO_COMPANY("The company does not exist."),
    MAPPING("Error while mapping data."),
    NO_EQUITY("The company does not exist."),
    UNBALANCED_CHUNK_DISTRIBUTION("The chunk payout distribution resulted in an imbalance of {} to be distributed."),
    BUILD_DISTRIBUTION("Could not build payout distribution.");

    private final String description;
}
