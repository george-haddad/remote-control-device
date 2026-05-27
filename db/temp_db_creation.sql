CREATE ROLE devicebackend WITH
	LOGIN
	NOSUPERUSER
	NOCREATEDB
	NOCREATEROLE
	INHERIT
	NOREPLICATION
	NOBYPASSRLS
	CONNECTION LIMIT -1
	ENCRYPTED PASSWORD 'SCRAM-SHA-256$4096:icMnG7OEcaevSiRJ9uZgXA==$4GlHgdFCJV7oEPqzlDF6QOTQrXJjfglTV8BjmBocHy8=:ZQol3d/hqzsMPAc1bWpvWoe2P6dwFxaV8Cmrnqn1UXU=';

COMMENT ON ROLE devicebackend IS 'User role for the device backend application.';

CREATE DATABASE "remote-control"
    WITH
    OWNER = devicebackend
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

\connect remote-control

CREATE SCHEMA IF NOT EXISTS app
    AUTHORIZATION devicebackend;

ALTER DEFAULT PRIVILEGES FOR ROLE postgres
GRANT EXECUTE ON FUNCTIONS TO devicebackend;

GRANT CREATE, CONNECT ON DATABASE "remote-control" TO devicebackend;

ALTER DEFAULT PRIVILEGES FOR ROLE postgres
GRANT ALL ON TABLES TO devicebackend;

ALTER DEFAULT PRIVILEGES FOR ROLE postgres
GRANT ALL ON SEQUENCES TO devicebackend;

ALTER DEFAULT PRIVILEGES FOR ROLE postgres
GRANT USAGE ON TYPES TO devicebackend;

CREATE TABLE app.devices
(
    device_id uuid DEFAULT uuidv7(),
    device_name varchar(100) NOT NULL,
    device_brand varchar(100) NOT NULL,
    device_state varchar(10) NOT NULL,
    device_creation_time timestamp DEFAULT now(),
    CONSTRAINT device_state CHECK(device_state = 'available' or device_state = 'in-use' or device_state = 'inactive'),
    PRIMARY KEY(device_id)
);

ALTER TABLE IF EXISTS app.devices
    OWNER to devicebackend;
