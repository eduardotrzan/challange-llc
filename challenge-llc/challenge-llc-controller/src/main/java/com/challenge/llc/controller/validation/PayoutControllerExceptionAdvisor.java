package com.challenge.llc.controller.validation;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.challenge.generic.controller.exceptions.error.ErrorLevel;
import com.challenge.generic.controller.exceptions.http.BadRequestError;
import com.challenge.generic.exceptions.IAppError;
import com.challenge.llc.service.validation.ErrorCode;

@Aspect
@Component
@Slf4j
public class PayoutControllerExceptionAdvisor {

    @Pointcut("within(com.challenge.llc.controller.PayoutDistributionController)")
    public void controller() { }

    @AfterThrowing(value = "controller() && execution(* *distribute(..))", throwing = "e")
    public void handleCreateException(JoinPoint joinPoint, Throwable e) {
        if (!this.isAppError(joinPoint, e)) {
            log.debug("Not an IAppError exception. Letting propagate for 'handleCreateException'.");
            return;
        }

        IAppError appError = (IAppError) e;

        if (this.isAppErrorOfTypes(appError, ErrorCode.DISTRIBUTION_PERSON_GROUP)) {
            throw new BadRequestError()
                    .withDescription(appError.getDescription())
                    .withErrorLevel(ErrorLevel.WARN);
        }
    }

    private boolean isAppError(JoinPoint joinPoint, Throwable e) {
        final String className = joinPoint.getSignature().getDeclaringType().getName();
        final String methodName = joinPoint.getSignature().getName();
        final Object[] arguments = joinPoint.getArgs();
        log.debug("Entering 'handleCreateException' for className={}, methodName={} with arguments={}.",
                className, methodName, arguments);

        return e instanceof IAppError;
    }

    private boolean isAppErrorOfTypes(IAppError appError, IAppError... appErrors) {
        return Arrays.asList(appErrors).contains(appError);
    }
}
