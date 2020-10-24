CREATE SEQUENCE person_id_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;
END;

CREATE TABLE person (
    id           BIGINT DEFAULT nextval('person_id_seq' :: REGCLASS) NOT NULL,
    uuid         UUID                                                NOT NULL DEFAULT uuid_generate_v4(),
    create_date  TIMESTAMP WITH TIME ZONE                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date  TIMESTAMP WITH TIME ZONE                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version      BIGINT                                              NOT NULL,
    name         VARCHAR(200)                                        NOT NULL,

    PRIMARY KEY (id)
);
END;

CREATE UNIQUE INDEX person_idx01
    ON person (uuid);


---


CREATE SEQUENCE company_id_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;
END;

CREATE TABLE company (
    id           BIGINT DEFAULT nextval('company_id_seq' :: REGCLASS) NOT NULL,
    uuid         UUID                                                NOT NULL DEFAULT uuid_generate_v4(),
    create_date  TIMESTAMP WITH TIME ZONE                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date  TIMESTAMP WITH TIME ZONE                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version      BIGINT                                              NOT NULL,
    name         VARCHAR(200)                                        NOT NULL,

    PRIMARY KEY (id)
);
END;

CREATE UNIQUE INDEX company_idx01
    ON company (uuid);


---


CREATE SEQUENCE equity_id_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;
END;

CREATE TABLE equity (
    id             BIGINT DEFAULT nextval('equity_id_seq' :: REGCLASS) NOT NULL,
    uuid           UUID                                                NOT NULL DEFAULT uuid_generate_v4(),
    create_date    TIMESTAMP WITH TIME ZONE                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date    TIMESTAMP WITH TIME ZONE                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version        BIGINT                                              NOT NULL,
    type           VARCHAR(70)                                         NOT NULL,
    quantity       BIGINT                                              NOT NULL,
    person_id      BIGINT                                              NOT NULL,
    company_id     BIGINT                                              NOT NULL,

    PRIMARY KEY (id)
);
END;

CREATE UNIQUE INDEX equity_idx01
    ON equity (uuid);

CREATE INDEX equity_idx02
    ON equity (person_id);

CREATE INDEX equity_idx03
    ON equity (company_id);


---


CREATE SEQUENCE equity_split_rule_id_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;
END;

CREATE TABLE equity_split_rule (
    id                   BIGINT DEFAULT nextval('equity_split_rule_id_seq' :: REGCLASS) NOT NULL,
    uuid                 UUID                                                NOT NULL DEFAULT uuid_generate_v4(),
    create_date          TIMESTAMP WITH TIME ZONE                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date          TIMESTAMP WITH TIME ZONE                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version              BIGINT                                              NOT NULL,
    company_id           BIGINT                                              NOT NULL,
    execution_order      BIGINT                                              NOT NULL,
    chunk_amount         DECIMAL(24, 12)                                     NULL,

    PRIMARY KEY (id)
);
END;

CREATE UNIQUE INDEX equity_split_rule_idx01
    ON equity_split_rule (uuid);

CREATE INDEX equity_split_rule_idx02
    ON equity_split_rule (company_id);


---


CREATE SEQUENCE split_rule_id_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;
END;

CREATE TABLE split_rule (
    id                   BIGINT DEFAULT nextval('split_rule_id_seq' :: REGCLASS) NOT NULL,
    uuid                 UUID                                                NOT NULL DEFAULT uuid_generate_v4(),
    create_date          TIMESTAMP WITH TIME ZONE                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date          TIMESTAMP WITH TIME ZONE                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version              BIGINT                                              NOT NULL,
    equity_split_rule_id BIGINT                                              NOT NULL,
    equity_type          VARCHAR(70)                                         NULL,
    percentage_allocated DECIMAL(4, 2)                                       NOT NULL,

    PRIMARY KEY (id)
);
END;

CREATE UNIQUE INDEX split_rule_idx01
    ON split_rule (uuid);

CREATE INDEX split_rule_idx02
    ON split_rule (equity_split_rule_id);


---


CREATE SEQUENCE equity_payout_id_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;
END;

CREATE TABLE equity_payout (
    id             BIGINT DEFAULT nextval('equity_payout_id_seq' :: REGCLASS) NOT NULL,
    uuid           UUID                                                NOT NULL DEFAULT uuid_generate_v4(),
    create_date    TIMESTAMP WITH TIME ZONE                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date    TIMESTAMP WITH TIME ZONE                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version        BIGINT                                              NOT NULL,
    payout         DECIMAL(24, 12)                                     NOT NULL,
    equity_id      BIGINT                                              NOT NULL,

    PRIMARY KEY (id)
);
END;

CREATE UNIQUE INDEX equity_payout_idx01
    ON equity_payout (uuid);

CREATE INDEX equity_payout_idx02
    ON equity_payout (equity_id);