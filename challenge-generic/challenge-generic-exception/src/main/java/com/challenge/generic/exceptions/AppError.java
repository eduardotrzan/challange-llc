package com.challenge.generic.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(of = { "description", "details"})
public class AppError extends RuntimeException implements IAppError {

    @Getter
    private final String description;

    @Getter
    private final String details;

    public static AppErrorBuilder builder() {
        return new AppErrorBuilder();
    }

    public static class AppErrorBuilder {

        private String details;

        private String description;

        public AppErrorBuilder description(final String description) {
            this.description = description;
            return this;
        }

        public AppErrorBuilder details(final String details) {
            this.details = details;
            return this;
        }

        public AppError build(String... args) {
            String formattedString = args == null || args.length == 0
                    ? this.description
                    : String.format(this.description, args);
            return new AppError(formattedString, this.details);
        }
    }
}
