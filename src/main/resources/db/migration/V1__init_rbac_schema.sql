CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE privileges (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE role_privileges (
    role_id BIGINT NOT NULL,
    privilege_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, privilege_id),
    CONSTRAINT fk_role_priv_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_priv_priv FOREIGN KEY (privilege_id) REFERENCES privileges(id) ON DELETE CASCADE
);

CREATE TABLE job_applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    company VARCHAR(140) NOT NULL,
    role VARCHAR(140) NOT NULL,
    status VARCHAR(20) NOT NULL,
    location VARCHAR(120),
    salary_min DECIMAL(12,2),
    salary_max DECIMAL(12,2),
    job_url VARCHAR(500),
    applied_date DATE,
    follow_up_date DATE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_job_app_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_app_user_status ON job_applications(user_id, status);
CREATE INDEX idx_app_user_company ON job_applications(user_id, company);
CREATE INDEX idx_app_user_followup ON job_applications(user_id, follow_up_date);

-- Seed base privileges
INSERT INTO privileges (name, description) VALUES
('APPLICATION_READ', 'Read own job applications'),
('APPLICATION_WRITE', 'Create/update own job applications'),
('APPLICATION_DELETE', 'Delete own job applications'),
('DASHBOARD_READ', 'Read dashboard summary'),
('ROLE_READ', 'Read roles'),
('ROLE_WRITE', 'Manage roles and privilege assignments');

-- Seed base roles
INSERT INTO roles (name, description) VALUES
('USER', 'Standard user'),
('ADMIN', 'System administrator');

-- USER role privileges
INSERT INTO role_privileges (role_id, privilege_id)
SELECT r.id, p.id
FROM roles r
JOIN privileges p
WHERE r.name = 'USER'
AND p.name IN ('APPLICATION_READ', 'APPLICATION_WRITE', 'APPLICATION_DELETE', 'DASHBOARD_READ');

-- ADMIN gets all
INSERT INTO role_privileges (role_id, privilege_id)
SELECT r.id, p.id
FROM roles r
JOIN privileges p
WHERE r.name = 'ADMIN';