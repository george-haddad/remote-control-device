# remote-control-device

![vertx](https://img.shields.io/badge/vert.x-5.1.0-purple.svg?style=for-the-badge&logo=eclipsevertdotx&logoColor=white?style=for-the-badge)
![postgresql](https://img.shields.io/badge/PostgreSQL-18.4-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![docker](https://img.shields.io/badge/docker-29.4.3-316192?style=for-the-badge&logo=docker&logoColor=white)
![sqitch](https://img.shields.io/badge/sqitch-1.6.1-316192?style=for-the-badge&logoColor=white)
![java](https://img.shields.io/badge/java-25.0.3-orange?style=for-the-badge&logoColor=white)
![maven](https://img.shields.io/badge/maven-3.9.2-orange?style=for-the-badge&logoColor=white)

A backend service that manages remote control devices via a RESTful API

## Build System

- Java Temurin JDK 25 LTS - programming language
- Apache Maven 3.9.2 - build automation
- Docker 29.4.3 - containers (alpine)
- Sqitch 1.6.1 - database migrations
- Editorconfig - source formatting

## Platform

- Eclipse Vert.x 5.1.0
- PostgreSQL 18.4

## Quick Start

1. Clone the git repo

```shell script
git clone git@github.com:george-haddad/remote-control-device.git
```

2. Create a `.env` file in the base repo and put the following values

```plain
# API
NAME=remote-control-device-api
DB_HOST=db
DB_PORT=5432
DB_NAME=remote-control
DB_USER=devicebackend
DB_PASS=Tablet9-Saddling-Undocked-Glance
HTTP_PORT=8080

# DB
POSTGRES_USER=postgres
POSTGRES_PASSWORD=P@ssW0rd12345
POSTGRES_DB=postgres
```

The `DB_PASS` should not be changed as this value is needed to automatically setup the database. This is because the same password is pre-encrypted in the `db/temp_db_creation.sql` script. This is mainly for convenience, in production this wouldn't be the case.

3. Pull the database docker image

```shell script
docker pull postgres:18.4-alpine3.23
```

4. Build the backend

```shell script
./mvnw clean package
```

5. Build the backend docker image

```shell script
./mvnw jib:dockerBuild
```

6. Bring up both docker images

```shell script
docker compose up
```

This should run the backend `remotecontrol-api` and the database `remotecontrol-db` on the docker network.

7. Test the health end-points

Here we expect the backend to be in a degraded state.

```shell script
curl http://localhost:8080/health --header 'Accept: application/json'
```

The response will show the database is down due to an authentication failure.

```json
{
	"status": "DOWN",
	"checks": [
		{
			"id": "event-bus",
			"status": "UP",
			"checks": [
				{
					"id": "remotecontrol.devices",
					"status": "UP"
				}
			]
		},
		{
			"id": "database",
			"status": "DOWN",
			"data": {
				"cause": "FATAL: password authentication failed for user \"devicebackend\" (28P01)"
			}
		},
		{
			"id": "api",
			"status": "UP"
		}
	],
	"outcome": "DOWN"
}
```

8. Setup the database

This command will connect to the database as the admin user and setup the following:

- User named `devicebackend` with a pre-encrypted password (the one from the `.env` file in step **2.**)
- Database named `remote-control`
- Schema named `app` in the `remote-control` database
- Grant `devicebackend` privileges to modify the `app` schema in the `remote-control` database
- Table named `devices` in the `app` schema

**Note**: Be sure to enter the postgres admin password from `.env` file in step **2.**

```shell script
psql -h localhost -U postgres -d postgres -a -f db/temp_db_creation.sql
```

There should be the output of the SQL script dumped in the terminal.

8. Test the health end-points (again)

Here we expect the backend to be in a healthy state.

```shell script
curl http://localhost:8080/health --header 'Accept: application/json'
```

The response will show that all resources are **UP** and running!

```json
{
	"status": "UP",
	"checks": [
		{
			"id": "event-bus",
			"status": "UP",
			"checks": [
				{
					"id": "remotecontrol.devices",
					"status": "UP"
				}
			]
		},
		{
			"id": "database",
			"status": "UP"
		},
		{
			"id": "api",
			"status": "UP"
		}
	],
	"outcome": "UP"
}
```

9. Start using the backend

View the API documentation in `src/main/resources/device-spec.yaml`.

**Note**: The reason the spec file is hidden away there is because the backend uses the spec to create its router with route validations as described in the spec. You may upload the spec into an OpenAPI 3.1 render such as [Swagger Editor](https://editor.swagger.io/) or any other tool that supports the spec.

## APIs

The RESTful APIs follow OpenAPI spec v3.1.0

See `src/main/resources/device-spec.yaml`

# Architecture

## Platform

## Software

# Conventions

## Commits

Follow best practices of git commit messages.

 - A commit prefix must be used.
 - A commit is composed of 2 parts
   - Header - prefix: and a short descriptive title
   - Body - separated by a new line after the header
 - Commit messages are always in the present tense stating what it does and not what it did.
 - Optional message describing the rationale and implications are in the commit message body.

Below is a short and concise list of commit prefixes with a short description.

- **feat**: Introduces a new feature.
- **fix**: Patches a bug.
- **docs**: Documentation-only changes.
- **style**: Changes that do not affect the meaning of the code (white-space, formatting, etc).
- **refactor**: A code change that neither fixes a bug nor adds a feature.
- **perf**: Improves performance.
- **test**: Adds missing tests or corrects existing tests.
- **chore**: Changes to the build process or auxiliary tools and libraries such as documentation generation.
- **build**: Changes that affect the build system or external dependencies
- **ci**: Changes to the CI configuration files and scripts
- **revert**: Reverts a previous commit.

**References**

- https://graphite.com/guides/git-commit-message-best-practices
- https://www.conventionalcommits.org/en/v1.0.0/
- https://jabaltorres.com/blog/common-git-message-prefixes/
- https://github.com/angular/angular/blob/22b96b9/CONTRIBUTING.md#type

## Database

- Table names are plural.
- Column names are singular.
- Table and Column names are in lowercase snake_case.
- Primary and Foreign Keys
   - Use underscore to signify relationship tables.
   - Prefer schema namespace over prefixing table names.
   - Names of attributes must remain consistent across all tables.
- Key names
  - Primary Keys names in format of [singular_of_table_name]_id.
  - Foreign Keys use same name as the referenced Primary Key.
  - Names of attributes must remain consistent across all tables.
  - Use underscore to signify relationship tables table_name_to_table_name.
- Prefixes
  - Prefer schema namespace over prefixing table names.
  - Do not prefix tables.
  - Do not prefix columns.

# Diagrams


# TODOs

These are items for future consideration
