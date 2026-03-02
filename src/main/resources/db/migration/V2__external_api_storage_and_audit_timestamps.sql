-- ==========================
-- AUDIT COLUMNS FOR EXISTING TABLES
-- ==========================
ALTER TABLE user_info
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT now(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT now();

ALTER TABLE roles
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT now(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT now();

ALTER TABLE tender_filter
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT now(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT now();

ALTER TABLE unloading_date
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT now(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT now();

ALTER TABLE illiquid_assets
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT now(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT now();


ALTER TABLE tenders
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT now(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT now();

-- ==========================
-- REMOVE PREVIOUS EXTERNAL TABLES (IF EXISTS)
-- ==========================
DROP TABLE IF EXISTS external_favorites;
DROP TABLE IF EXISTS external_markers;
DROP TABLE IF EXISTS external_purchase_results;
DROP TABLE IF EXISTS external_purchases;

-- ==========================
-- FULL PURCHASE ENTITY (tenderEntity.Tender)
-- ==========================
CREATE TABLE IF NOT EXISTS purchase_tenders
(
    db_id                   BIGSERIAL PRIMARY KEY,
    purchase_id             VARCHAR(255)  NOT NULL,
    source_type             VARCHAR(16)   NOT NULL,
    updated_datetime        TIMESTAMP,
    notification_type       VARCHAR(255),
    notification_placing_way VARCHAR(255),
    auction_date_time       TIMESTAMP,
    etp_link                VARCHAR(2048),
    eis_link                VARCHAR(2048),
    link                    VARCHAR(2048),
    cancel_reason           TEXT,
    planned_publish_date    TIMESTAMP,
    notification_number     VARCHAR(255),
    title                   VARCHAR(1024),
    smp                     BOOLEAN,
    publication_datetime_utc TIMESTAMP,
    application_deadline    TIMESTAMP,
    commission_deadline     TIMESTAMP,
    payload_json            TEXT          NOT NULL,
    created_at              TIMESTAMP     NOT NULL DEFAULT now(),
    updated_at              TIMESTAMP     NOT NULL DEFAULT now(),
    CONSTRAINT uk_purchase_tenders_purchase_source UNIQUE (purchase_id, source_type)
);

CREATE INDEX IF NOT EXISTS idx_purchase_tenders_purchase_id
    ON purchase_tenders (purchase_id);

-- ==========================
-- PURCHASE RESULTS ENTITY
-- ==========================
CREATE TABLE IF NOT EXISTS purchase_results
(
    db_id                   BIGSERIAL PRIMARY KEY,
    purchase_id             VARCHAR(255) NOT NULL,
    link                    VARCHAR(2048),
    protocols_count         INTEGER,
    contract_projects_count INTEGER,
    contracts_count         INTEGER,
    payload_json            TEXT         NOT NULL,
    created_at              TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at              TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT uk_purchase_results_purchase_id UNIQUE (purchase_id)
);

-- ==========================
-- FAVORITES ENTITIES
-- ==========================
CREATE TABLE IF NOT EXISTS favorite_tenders
(
    db_id       BIGSERIAL PRIMARY KEY,
    purchase_id VARCHAR(255) NOT NULL,
    marker_name VARCHAR(255) NOT NULL,
    source_type VARCHAR(16)  NOT NULL,
    page_number INTEGER,
    total_count BIGINT,
    payload_json TEXT        NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT now(),
    updated_at TIMESTAMP     NOT NULL DEFAULT now(),
    CONSTRAINT uk_favorite_tenders_purchase_source_marker UNIQUE (purchase_id, source_type, marker_name)
);

CREATE INDEX IF NOT EXISTS idx_favorite_tenders_purchase_id
    ON favorite_tenders (purchase_id);

CREATE TABLE IF NOT EXISTS favorite_markers
(
    db_id       BIGSERIAL PRIMARY KEY,
    marker_id   VARCHAR(255) NOT NULL,
    name        VARCHAR(255),
    payload_json TEXT        NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT uk_favorite_markers_marker_id UNIQUE (marker_id)
);

CREATE INDEX IF NOT EXISTS idx_favorite_markers_marker_id
    ON favorite_markers (marker_id);
