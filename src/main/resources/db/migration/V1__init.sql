-- ==========================
-- USERS
-- ==========================
CREATE TABLE user_info
(
    user_id  BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    email    VARCHAR(255) UNIQUE,
    password VARCHAR(255)
);

-- ==========================
-- ROLES
-- ==========================
CREATE TABLE roles
(
    role_id BIGSERIAL PRIMARY KEY,
    name    VARCHAR(255) UNIQUE
);

-- ==========================
-- USER → ROLES (ManyToMany)
-- ==========================
CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL REFERENCES user_info (user_id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles (role_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- ==========================
-- ILLIQUID ASSETS
-- ==========================
CREATE TABLE illiquid_assets
(
    id                       BIGSERIAL PRIMARY KEY,
    commodity_material_value VARCHAR(255) NOT NULL,
    article_number           VARCHAR(255),
    quantity                 REAL,
    units_of_measurement     VARCHAR(255),
    price                    REAL,
    currency                 VARCHAR(255),
    summary_price            REAL,
    arrival_date             VARCHAR(255) NOT NULL,
    creating_date            TIMESTAMP    NOT NULL DEFAULT now(),
    last_update_date         TIMESTAMP,
    responsible_employee     VARCHAR(255),
    created_by_id            BIGINT       NOT NULL,
    commentary               TEXT,
    avito                    VARCHAR(255),
    asset_type               VARCHAR(255),
    asset_status             VARCHAR(255) NOT NULL
);

-- ==========================
-- TENDER FILTER
-- ==========================
CREATE TABLE tender_filter
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    user_id     BIGINT,
    is_active   BOOLEAN,
    json_filter TEXT
);

-- ==========================
-- UNLOADING DATE
-- ==========================
CREATE TABLE unloading_date
(
    id          BIGSERIAL PRIMARY KEY,
    filter_id   BIGINT NOT NULL,
    unload_date TIMESTAMP
);

-- ==========================
-- FOUND TENDER
-- ==========================
CREATE TABLE tenders
(
    id                     VARCHAR(255) PRIMARY KEY,
    notification_number    VARCHAR(255),
    order_name             VARCHAR(500),
    notification_type_desc VARCHAR(255),
    type_of_trading        INTEGER,
    max_price              DOUBLE PRECISION,
    currency               VARCHAR(64),
    ep_uri                 VARCHAR(500),
    link                   VARCHAR(500),
    application_deadline   TIMESTAMP,
    is_cancelled           BOOLEAN,
    create_date            TIMESTAMP,
    other_information      TEXT,
    commission_deadline    TIMESTAMP,
    is_abandoned           BOOLEAN,
    is_planning            BOOLEAN
);