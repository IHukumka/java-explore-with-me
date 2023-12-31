DROP TABLE IF EXISTS Hit;

CREATE TABLE IF NOT EXISTS hits
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app   VARCHAR(255)                                    ,
    uri   VARCHAR(50)									  ,
    ip    VARCHAR(15)									  ,
    timestamp TIMESTAMP WITHOUT TIME ZONE
);