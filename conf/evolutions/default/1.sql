# --- !Ups
CREATE SCHEMA auth;

CREATE TABLE auth.silhouette_user_roles
(
    id   SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR
);

INSERT INTO auth.silhouette_user_roles (name)
VALUES ('User'),
       ('Admin');

CREATE TABLE auth.silhouette_users
(
    id        UUID    NOT NULL PRIMARY KEY,
    name      VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    position  VARCHAR NOT NULL,
    email     VARCHAR NOT NULL,
    role_id   INT     NOT NULL,
    CONSTRAINT auth_user_role_id_fk FOREIGN KEY (role_id) REFERENCES auth.silhouette_user_roles (id)
);

INSERT INTO auth.silhouette_users (id, name, last_name, position, email, role_id)
VALUES ('af6bc68e-2ff5-4c9b-bedb-739aff461bbf', 'Администратор', '1', 'Администратор', 'admin@admin.com', 2);

CREATE TABLE auth.silhouette_login_info
(
    id           BIGSERIAL NOT NULL PRIMARY KEY,
    provider_id  VARCHAR,
    provider_key VARCHAR
);

INSERT INTO auth.silhouette_login_info (provider_id, provider_key)
VALUES ('credentials', 'admin@admin.com');

CREATE TABLE auth.silhouette_user_login_info
(
    user_id       UUID   NOT NULL,
    login_info_id BIGINT NOT NULL,
    CONSTRAINT auth_user_login_info_user_id_fk FOREIGN KEY (user_id) REFERENCES auth.silhouette_users (id) ON DELETE CASCADE,
    CONSTRAINT auth_user_login_info_login_info_id_fk FOREIGN KEY (login_info_id) REFERENCES auth.silhouette_login_info (id) ON DELETE CASCADE
);

INSERT INTO auth.silhouette_user_login_info (user_id, login_info_id)
VALUES ('af6bc68e-2ff5-4c9b-bedb-739aff461bbf', 1);

CREATE TABLE auth.silhouette_password_info
(
    hasher        VARCHAR NOT NULL,
    password      VARCHAR NOT NULL,
    salt          VARCHAR,
    login_info_id BIGINT  NOT NULL,
    CONSTRAINT auth_password_info_login_info_id_fk FOREIGN KEY (login_info_id) REFERENCES auth.silhouette_login_info (id) ON DELETE CASCADE
);

INSERT INTO auth.silhouette_password_info (hasher, password, salt, login_info_id)
VALUES ('bcrypt-sha256', '$2a$10$Pw9UIqJ1uv41wip6/z7DauF7dgT1bfW5txkRPDRbQF3hSq6UoBj9W', NULL, 1);

CREATE TABLE auth.silhouette_tokens
(
    id      UUID        NOT NULL PRIMARY KEY,
    user_id UUID        NOT NULL,
    expiry  TIMESTAMPTZ NOT NULL,
    CONSTRAINT auth_token_user_id_fk FOREIGN KEY (user_id) REFERENCES auth.silhouette_users (id) ON DELETE CASCADE
);

INSERT INTO auth.silhouette_tokens (id, user_id, expiry)
VALUES ('1e199ded-8a9b-4146-ae06-072546193091', 'af6bc68e-2ff5-4c9b-bedb-739aff461bbf',
        '2021-08-30T20:22:06.212633+05:00');

# --- !Downs

DROP TABLE auth.silhouette_tokens;
DROP TABLE auth.silhouette_password_info;
DROP TABLE auth.silhouette_user_login_info;
DROP TABLE auth.silhouette_login_info;
DROP TABLE auth.silhouette_users CASCADE;
DROP TABLE auth.silhouette_user_roles;
DROP SCHEMA auth;

# --- !Ups
CREATE SCHEMA app;

CREATE TABLE app.items
(
    id   BIGSERIAL NOT NULL PRIMARY KEY,
    name VARCHAR   NOT NULL
);

INSERT INTO app.items (name)
VALUES ('Переговорная');

CREATE TABLE app.events
(
    id             BIGSERIAL NOT NULL PRIMARY KEY,
    title          VARCHAR   NOT NULL,
    start_datetime TIMESTAMP NOT NULL,
    end_datetime   TIMESTAMP NOT NULL,
    org_user_id    UUID      NOT NULL,
    item_id        BIGINT    NOT NULL,
    description    VARCHAR,
    CONSTRAINT events_user_id_fk FOREIGN KEY (org_user_id) REFERENCES auth.silhouette_users (id) ON DELETE CASCADE,
    CONSTRAINT events_item_id_fk FOREIGN KEY (item_id) REFERENCES app.items (id) ON DELETE CASCADE
);

CREATE TABLE app.event_members
(
    event_id BIGSERIAL NOT NULL,
    user_id  UUID      NOT NULL,
    CONSTRAINT member_event_id_fk FOREIGN KEY (event_id) REFERENCES app.events(id) ON DELETE CASCADE,
    CONSTRAINT member_user_id_fk FOREIGN KEY (user_id) REFERENCES auth.silhouette_users (id) ON DELETE CASCADE
);

# --- !Downs
DROP TABLE app.events;
DROP TABLE app.items;
DROP TABLE app.event_members;
DROP SCHEMA app