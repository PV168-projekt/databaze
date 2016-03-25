CREATE TABLE "MISSION" (
    "ID" BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "NAME" VARCHAR(255) NOT NULL,
    "LOCATION" VARCHAR(255) NOT NULL,
);

CREATE TABLE "AGENT" (  
    "ID" BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "MISSIONID" BIGINT REFERENCES MISSION (ID),
    "NAME" VARCHAR(255) NOT NULL,
    "SALARY" INTEGER NOT NULL,
);