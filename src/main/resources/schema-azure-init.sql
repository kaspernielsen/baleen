-- Database initialization script for Azure PostgreSQL
-- This script creates all required tables for Baleen application

-- Enable PostGIS extension for geometry support
CREATE EXTENSION IF NOT EXISTS postgis;

-- Create sequence for ID generation
CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START WITH 1 INCREMENT BY 1;

-- Base table for inheritance (SecomTransactionalEntity)
CREATE TABLE IF NOT EXISTS secom_transactional_entity (
    id BIGINT PRIMARY KEY,
    version INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- SecomNodeEntity table
CREATE TABLE IF NOT EXISTS secom_node_entity (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    mrn VARCHAR(255) UNIQUE,
    url VARCHAR(255),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SecomSubscriberEntity table (the one causing the error)
CREATE TABLE IF NOT EXISTS secom_subscriber (
    id BIGINT PRIMARY KEY,
    node_id BIGINT,
    data_product_type VARCHAR(255),
    product_version VARCHAR(255),
    geometry geometry,
    container_type VARCHAR(255),
    data_reference VARCHAR(255),
    callback VARCHAR(255),
    subscription_start TIMESTAMP,
    subscription_end TIMESTAMP,
    original_wkt TEXT,
    original_unlocode VARCHAR(10),
    CONSTRAINT fk_secom_subscriber_node FOREIGN KEY (node_id) REFERENCES secom_node_entity(id),
    CONSTRAINT fk_secom_subscriber_transactional FOREIGN KEY (id) REFERENCES secom_transactional_entity(id)
);

-- Create spatial index on geometry column
CREATE INDEX IF NOT EXISTS idx_secom_subscriber_geometry ON secom_subscriber USING GIST (geometry);

-- SecomTransactionalUploadEntity table
CREATE TABLE IF NOT EXISTS secom_transactional_upload_entity (
    id BIGINT PRIMARY KEY,
    content_length BIGINT,
    content_type VARCHAR(255),
    dataset_id VARCHAR(255),
    data_product_type VARCHAR(255),
    data_reference VARCHAR(255),
    gml_data TEXT,
    boundaries geometry,
    CONSTRAINT fk_secom_upload_transactional FOREIGN KEY (id) REFERENCES secom_transactional_entity(id)
);

-- Create spatial index on boundaries column
CREATE INDEX IF NOT EXISTS idx_secom_upload_boundaries ON secom_transactional_upload_entity USING GIST (boundaries);

-- SecomTransactionalUploadLinkEntity table
CREATE TABLE IF NOT EXISTS secom_transactional_upload_link_entity (
    id BIGINT PRIMARY KEY,
    upload_id BIGINT,
    subscriber_id BIGINT,
    dataset_metadata TEXT,
    CONSTRAINT fk_upload_link_upload FOREIGN KEY (upload_id) REFERENCES secom_transactional_upload_entity(id),
    CONSTRAINT fk_upload_link_subscriber FOREIGN KEY (subscriber_id) REFERENCES secom_subscriber(id),
    CONSTRAINT fk_upload_link_transactional FOREIGN KEY (id) REFERENCES secom_transactional_entity(id)
);

-- S100DataSetIdentificationEntity table
CREATE TABLE IF NOT EXISTS s100_data_set_identification_entity (
    id BIGINT PRIMARY KEY,
    dataset_id VARCHAR(255) UNIQUE,
    enav_product_code INTEGER,
    data_server_identifier VARCHAR(255),
    dataset_identification_type VARCHAR(255),
    version_number INTEGER,
    dataset_name VARCHAR(255),
    date_of_issue DATE,
    dataset_language_iso_639_3 VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- S124DatasetInstanceEntity table
CREATE TABLE IF NOT EXISTS s124_dataset_instance_entity (
    id BIGINT PRIMARY KEY,
    dataset_id VARCHAR(255),
    version_number INTEGER,
    dataset_time DATE,
    geometry geometry,
    gml_data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create spatial index on geometry column
CREATE INDEX IF NOT EXISTS idx_s124_dataset_geometry ON s124_dataset_instance_entity USING GIST (geometry);

-- S124 dataset references join table
CREATE TABLE IF NOT EXISTS s124_dataset_references (
    dataset_id BIGINT NOT NULL,
    reference_id BIGINT NOT NULL,
    PRIMARY KEY (dataset_id, reference_id),
    CONSTRAINT fk_s124_dataset_ref_dataset FOREIGN KEY (dataset_id) REFERENCES s124_dataset_instance_entity(id),
    CONSTRAINT fk_s124_dataset_ref_reference FOREIGN KEY (reference_id) REFERENCES s124_dataset_instance_entity(id)
);

-- PublicationEntity table
CREATE TABLE IF NOT EXISTS publication_entity (
    id BIGINT PRIMARY KEY,
    publication_id VARCHAR(255) UNIQUE,
    dataset_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PublicationRecipiantEntity table
CREATE TABLE IF NOT EXISTS publication_recipiant_entity (
    id BIGINT PRIMARY KEY,
    publication_id BIGINT,
    subscriber_id BIGINT,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pub_recipient_publication FOREIGN KEY (publication_id) REFERENCES publication_entity(id),
    CONSTRAINT fk_pub_recipient_subscriber FOREIGN KEY (subscriber_id) REFERENCES secom_subscriber(id)
);

-- PublicationRecipiantAttemptedDeliveryEntity table
CREATE TABLE IF NOT EXISTS publication_recipiant_attempted_delivery_entity (
    id BIGINT PRIMARY KEY,
    recipiant_id BIGINT,
    attempt_number INTEGER,
    attempted_at TIMESTAMP,
    response_code INTEGER,
    response_message TEXT,
    success BOOLEAN DEFAULT false,
    CONSTRAINT fk_delivery_attempt_recipient FOREIGN KEY (recipiant_id) REFERENCES publication_recipiant_entity(id)
);

-- SecomOutboxEntity table
CREATE TABLE IF NOT EXISTS secom_outbox_entity (
    id BIGINT PRIMARY KEY,
    dataset_id VARCHAR(255),
    subscriber_id BIGINT,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_outbox_subscriber FOREIGN KEY (subscriber_id) REFERENCES secom_subscriber(id)
);

-- SecomOutboxMessageEntity table
CREATE TABLE IF NOT EXISTS secom_outbox_message_entity (
    id BIGINT PRIMARY KEY,
    outbox_id BIGINT,
    message_content TEXT,
    message_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_outbox_message_outbox FOREIGN KEY (outbox_id) REFERENCES secom_outbox_entity(id)
);

-- Create indexes for foreign keys and commonly queried fields
CREATE INDEX IF NOT EXISTS idx_secom_subscriber_node_id ON secom_subscriber(node_id);
CREATE INDEX IF NOT EXISTS idx_secom_subscriber_data_product_type ON secom_subscriber(data_product_type);
CREATE INDEX IF NOT EXISTS idx_upload_link_upload_id ON secom_transactional_upload_link_entity(upload_id);
CREATE INDEX IF NOT EXISTS idx_upload_link_subscriber_id ON secom_transactional_upload_link_entity(subscriber_id);
CREATE INDEX IF NOT EXISTS idx_s100_dataset_id ON s100_data_set_identification_entity(dataset_id);
CREATE INDEX IF NOT EXISTS idx_s124_dataset_id ON s124_dataset_instance_entity(dataset_id);
CREATE INDEX IF NOT EXISTS idx_publication_dataset_id ON publication_entity(dataset_id);
CREATE INDEX IF NOT EXISTS idx_pub_recipient_publication_id ON publication_recipiant_entity(publication_id);
CREATE INDEX IF NOT EXISTS idx_pub_recipient_subscriber_id ON publication_recipiant_entity(subscriber_id);
CREATE INDEX IF NOT EXISTS idx_delivery_attempt_recipient_id ON publication_recipiant_attempted_delivery_entity(recipiant_id);
CREATE INDEX IF NOT EXISTS idx_outbox_subscriber_id ON secom_outbox_entity(subscriber_id);
CREATE INDEX IF NOT EXISTS idx_outbox_message_outbox_id ON secom_outbox_message_entity(outbox_id);

-- Grant permissions (adjust as needed for your database user)
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO baleendbadmin;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO baleendbadmin;