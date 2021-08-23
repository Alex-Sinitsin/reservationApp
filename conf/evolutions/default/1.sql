# --- !Ups
CREATE SCHEMA auth;

CREATE TABLE auth.silhouette_user_roles (
  id   SERIAL NOT NULL PRIMARY KEY,
  name VARCHAR
);

INSERT INTO auth.silhouette_user_roles (name)
VALUES ('User'),
       ('Admin');

CREATE TABLE auth.silhouette_users (
  id         UUID    NOT NULL PRIMARY KEY,
  name VARCHAR NOT NULL,
  last_name  VARCHAR NOT NULL,
  position  VARCHAR NOT NULL,
  email      VARCHAR NOT NULL,
  role_id    INT     NOT NULL,
  CONSTRAINT auth_user_role_id_fk FOREIGN KEY (role_id) REFERENCES auth.silhouette_user_roles (id)
);

CREATE TABLE auth.silhouette_login_info (
  id           BIGSERIAL NOT NULL PRIMARY KEY,
  provider_id  VARCHAR,
  provider_key VARCHAR
);

CREATE TABLE auth.silhouette_user_login_info (
  user_id       UUID   NOT NULL,
  login_info_id BIGINT NOT NULL,
  CONSTRAINT auth_user_login_info_user_id_fk FOREIGN KEY (user_id) REFERENCES auth.silhouette_users (id),
  CONSTRAINT auth_user_login_info_login_info_id_fk FOREIGN KEY (login_info_id) REFERENCES auth.silhouette_login_info (id)
);

CREATE TABLE auth.silhouette_password_info (
  hasher        VARCHAR NOT NULL,
  password      VARCHAR NOT NULL,
  salt          VARCHAR,
  login_info_id BIGINT  NOT NULL,
  CONSTRAINT auth_password_info_login_info_id_fk FOREIGN KEY (login_info_id) REFERENCES auth.silhouette_login_info (id)
);

CREATE TABLE auth.silhouette_tokens (
  id      UUID        NOT NULL PRIMARY KEY,
  user_id UUID        NOT NULL,
  expiry  TIMESTAMPTZ NOT NULL,
  CONSTRAINT auth_token_user_id_fk FOREIGN KEY (user_id) REFERENCES auth.silhouette_users (id)
);

# --- !Downs

DROP TABLE auth.silhouette_tokens;
DROP TABLE auth.silhouette_password_info;
DROP TABLE auth.silhouette_user_login_info;
DROP TABLE auth.silhouette_login_info;
DROP TABLE auth.silhouette_users;
DROP TABLE auth.silhouette_user_roles;
DROP SCHEMA auth;