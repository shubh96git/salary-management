CREATE TABLE employee (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    full_name     VARCHAR(100)  NOT NULL,
    email         VARCHAR(255)  NOT NULL,
    job_title     VARCHAR(100)  NOT NULL,
    department    VARCHAR(100)  NOT NULL,
    country_code  CHAR(2)       NOT NULL,
    currency      CHAR(3)       NOT NULL,
    annual_salary DECIMAL(12, 2) NOT NULL,
    hire_date     DATE          NOT NULL,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_employee PRIMARY KEY (id),
    CONSTRAINT uk_employee_email UNIQUE (email),
    CONSTRAINT ck_employee_salary_positive CHECK (annual_salary > 0)
);

-- Indexes follow the list filters and the insight GROUP BY columns.
CREATE INDEX idx_employee_country_job_title ON employee (country_code, job_title);
CREATE INDEX idx_employee_country_department ON employee (country_code, department);
CREATE INDEX idx_employee_full_name ON employee (full_name);
