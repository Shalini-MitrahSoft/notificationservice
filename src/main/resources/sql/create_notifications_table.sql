CREATE TABLE IF NOT EXISTS notifications (
    id          BIGINT          AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT          NOT NULL,
    order_id    BIGINT,
    type        VARCHAR(100)    NOT NULL,
    title       VARCHAR(255),
    message     TEXT,
    is_read     BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at  DATETIME        NOT NULL
);
