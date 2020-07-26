--liquibase formatted sql

--changeset jakubdybczak:1595803180059-1
CREATE TABLE address (id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL, city VARCHAR(255), country VARCHAR(255), number VARCHAR(255), street VARCHAR(255), CONSTRAINT "addressPK" PRIMARY KEY (id));

--changeset jakubdybczak:1595803180059-2
CREATE TABLE client (id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL, refresh_token VARCHAR(255), CONSTRAINT "clientPK" PRIMARY KEY (id));

--changeset jakubdybczak:1595803180059-3
CREATE TABLE client_role (id BIGINT NOT NULL, roles VARCHAR(255));

--changeset jakubdybczak:1595803180059-4
CREATE TABLE measurement (id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL, timestamp TIMESTAMP WITHOUT TIME ZONE, value FLOAT8 NOT NULL, sensor_db_id BIGINT, CONSTRAINT "measurementPK" PRIMARY KEY (id));

--changeset jakubdybczak:1595803180059-5
CREATE TABLE sensor (db_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL, id VARCHAR(255), type VARCHAR(255), latest_measurement_id BIGINT, station_id BIGINT, CONSTRAINT "sensorPK" PRIMARY KEY (db_id));

--changeset jakubdybczak:1595803180059-6
CREATE TABLE station (id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL, latitude FLOAT8 NOT NULL, longitude FLOAT8 NOT NULL, mac_address VARCHAR(255), name VARCHAR(255), address_id BIGINT, owner_id BIGINT, station_client_id BIGINT, CONSTRAINT "stationPK" PRIMARY KEY (id));

--changeset jakubdybczak:1595803180059-7
CREATE TABLE station_client (id BIGINT NOT NULL, CONSTRAINT "station_clientPK" PRIMARY KEY (id));

--changeset jakubdybczak:1595803180059-8
CREATE TABLE user_client (email VARCHAR(255), password_hash VARCHAR(255), station_registration_token VARCHAR(255), id BIGINT NOT NULL, CONSTRAINT "user_clientPK" PRIMARY KEY (id));

--changeset jakubdybczak:1595803180059-9
CREATE TABLE user_client_stub (id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL, activate_string VARCHAR(255), email VARCHAR(255), password_hash VARCHAR(255), CONSTRAINT "user_client_stubPK" PRIMARY KEY (id));

--changeset jakubdybczak:1595803180059-10
ALTER TABLE client ADD CONSTRAINT UC_CLIENTREFRESH_TOKEN_COL UNIQUE (refresh_token);

--changeset jakubdybczak:1595803180059-11
ALTER TABLE user_client ADD CONSTRAINT UC_USER_CLIENTEMAIL_COL UNIQUE (email);

--changeset jakubdybczak:1595803180059-12
ALTER TABLE user_client_stub ADD CONSTRAINT UC_USER_CLIENT_STUBEMAIL_COL UNIQUE (email);

--changeset jakubdybczak:1595803180059-13
CREATE INDEX INDX_0 ON measurement(timestamp);

--changeset jakubdybczak:1595803180059-14
ALTER TABLE sensor ADD CONSTRAINT "FK3fg8m16ebjoqc9kn2hc6phjwh" FOREIGN KEY (station_id) REFERENCES station (id);

--changeset jakubdybczak:1595803180059-15
ALTER TABLE station ADD CONSTRAINT "FK4qcdqbyfa4u1tpxhhskj6cvek" FOREIGN KEY (station_client_id) REFERENCES station_client (id);

--changeset jakubdybczak:1595803180059-16
ALTER TABLE station ADD CONSTRAINT "FK52qw87l9ttkr3nf32phsvqe6f" FOREIGN KEY (address_id) REFERENCES address (id);

--changeset jakubdybczak:1595803180059-17
ALTER TABLE sensor ADD CONSTRAINT "FK88wiltqsbte20tvhvd60siapm" FOREIGN KEY (latest_measurement_id) REFERENCES measurement (id);

--changeset jakubdybczak:1595803180059-18
ALTER TABLE measurement ADD CONSTRAINT "FKa5ehlq2tq9buy8sjxtuk7el85" FOREIGN KEY (sensor_db_id) REFERENCES sensor (db_id);

--changeset jakubdybczak:1595803180059-19
ALTER TABLE station_client ADD CONSTRAINT "FKbkmmakw2c5qmjuh3qh22lrjsy" FOREIGN KEY (id) REFERENCES client (id);

--changeset jakubdybczak:1595803180059-20
ALTER TABLE station ADD CONSTRAINT "FKck6b3rbj8cq68osm122c726ns" FOREIGN KEY (owner_id) REFERENCES user_client (id);

--changeset jakubdybczak:1595803180059-21
ALTER TABLE user_client ADD CONSTRAINT "FKgdrjsb0tj0tk2yb6iibukfh4e" FOREIGN KEY (id) REFERENCES client (id);

--changeset jakubdybczak:1595803180059-22
ALTER TABLE client_role ADD CONSTRAINT "FKlc5c40lwdi77xfj5jlrwpt4uc" FOREIGN KEY (id) REFERENCES client (id);

