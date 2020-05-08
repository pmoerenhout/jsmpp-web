CREATE TABLE smout (
  id               BIGINT IDENTITY NOT NULL,
  timestamp        TIMESTAMP       NOT NULL,
  operationid      BINARY(16)      NOT NULL,
  user             VARCHAR(255)    NOT NULL,
  connectionid     VARCHAR(255)    NULL,
  servicetype      VARCHAR(5)      NOT NULL,
  source           VARCHAR(20)     NOT NULL,
  sourceton        TINYINT     DEFAULT NULL,
  sourcenpi        TINYINT     DEFAULT NULL,
  destination      VARCHAR(20)     NOT NULL,
  destinationton   TINYINT     DEFAULT NULL,
  destinationnpi   TINYINT     DEFAULT NULL,
  esmclass         TINYINT     DEFAULT NULL,
  dcs              TINYINT     DEFAULT NULL,
  pid              TINYINT     DEFAULT NULL,
  priority         TINYINT     DEFAULT NULL,
  scheduled        VARCHAR(17) DEFAULT NULL,
  validityperiod   VARCHAR(17) DEFAULT NULL,
  replaceifpresent TINYINT     DEFAULT NULL,
  shortmessage     BLOB            NOT NULL,
  messageid        VARCHAR(64) DEFAULT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE dr (
  id             BIGINT IDENTITY NOT NULL,
  connectionid   VARCHAR(255)    NOT NULL,
  timestamp      TIMESTAMP       NOT NULL,
  servicetype    VARCHAR(5)      NULL,
  source         VARCHAR(20)     NOT NULL,
  sourceton      TINYINT              DEFAULT NULL,
  sourcenpi      TINYINT              DEFAULT NULL,
  destination    VARCHAR(20)     NOT NULL,
  destinationton TINYINT              DEFAULT NULL,
  destinationnpi TINYINT              DEFAULT NULL,
  messageid      VARCHAR(64)          DEFAULT NULL,
  submitted      TINYINT              DEFAULT NULL,
  delivered      TINYINT              DEFAULT NULL,
  submitdate     TIMESTAMP       NULL DEFAULT NULL,
  donedate       TIMESTAMP       NULL DEFAULT NULL,
  state          TINYINT              DEFAULT NULL,
  error          VARCHAR(8)           DEFAULT NULL,
  text           VARBINARY(40)        DEFAULT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE smin (
  id               BIGINT IDENTITY NOT NULL,
  timestamp        TIMESTAMP       NOT NULL,
  connectionid     VARCHAR(255)    NULL,
  messageid        VARCHAR(64)     NULL,
  servicetype      VARCHAR(5)  DEFAULT NULL,
  source           VARCHAR(20) DEFAULT NULL,
  sourceton        TINYINT     DEFAULT NULL,
  sourcenpi        TINYINT     DEFAULT NULL,
  destination      VARCHAR(20)     NOT NULL,
  destinationton   TINYINT     DEFAULT NULL,
  destinationnpi   TINYINT     DEFAULT NULL,
  esmclass         TINYINT     DEFAULT NULL,
  dcs              TINYINT     DEFAULT NULL,
  pid              TINYINT     DEFAULT NULL,
  priority         TINYINT     DEFAULT NULL,
  scheduled        VARCHAR(17) DEFAULT NULL,
  validityperiod   VARCHAR(17) DEFAULT NULL,
  replaceifpresent TINYINT     DEFAULT NULL,
  shortmessage     BLOB            NOT NULL,
  smscaddress      VARCHAR(20) DEFAULT NULL,
  smscaddresston   TINYINT     DEFAULT NULL,
  smscaddressnpi   TINYINT     DEFAULT NULL,
  smsctimestamp    TIMESTAMP   DEFAULT NULL,
  mms              BOOLEAN     DEFAULT NULL,
  PRIMARY KEY (id)
);