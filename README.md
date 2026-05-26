# remote-control-device

![vertx](https://img.shields.io/badge/vert.x-5.0.12-purple.svg?style=for-the-badge&logo=eclipsevertdotx&logoColor=white?style=for-the-badge)
![postgresql](https://img.shields.io/badge/PostgreSQL-18.2-316192?style=for-the-badge&logo=postgresql&logoColor=white)
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

- Eclipse Vert.x 5.0.12
- PostgreSQL 18.2

## Quick Start

1. Clone the git repo

```shell script
git clone git@github.com:george-haddad/remote-control-device.git
```

## APIs

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
