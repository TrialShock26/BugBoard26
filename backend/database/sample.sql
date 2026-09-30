INSERT INTO User_ (email, hashed_password, type) VALUES
('alice.dev@company.com', '$2a$12$Yel74Dners.hNcTkfSsrf.ev.yvJpy7Y3LnaDk9KAFGMroFc54n8O', 'DEV'),
('bob.admin@company.com', '$2a$12$Yel74Dners.hNcTkfSsrf.ev.yvJpy7Y3LnaDk9KAFGMroFc54n8O', 'ADMIN'),
('charlie.dev@company.com', '$2a$12$Yel74Dners.hNcTkfSsrf.ev.yvJpy7Y3LnaDk9KAFGMroFc54n8O', 'DEV'),
('david.dev@company.com', '$2a$12$Yel74Dners.hNcTkfSsrf.ev.yvJpy7Y3LnaDk9KAFGMroFc54n8O', 'DEV'),
('eva.dev@company.com', '$2a$12$Yel74Dners.hNcTkfSsrf.ev.yvJpy7Y3LnaDk9KAFGMroFc54n8O', 'DEV'),
('frank.dev@company.com', '$2a$12$Yel74Dners.hNcTkfSsrf.ev.yvJpy7Y3LnaDk9KAFGMroFc54n8O', 'DEV'),
('grace.dev@company.com', '$2a$12$Yel74Dners.hNcTkfSsrf.ev.yvJpy7Y3LnaDk9KAFGMroFc54n8O', 'DEV'),
('heidi.admin@company.com', '$2a$12$Yel74Dners.hNcTkfSsrf.ev.yvJpy7Y3LnaDk9KAFGMroFc54n8O', 'ADMIN');

INSERT INTO Project (name) VALUES
('Apollo Nexus'),
('Titan Core');

INSERT INTO Team (name, project_id) VALUES
('Nexus Frontend', 1),
('Nexus Backend', 1),
('Titan Infra', 2),
('Titan Security', 2);

INSERT INTO Collaboration (team_id, user_id) VALUES
(1, 2), (1, 3),
(2, 5), (2, 4),
(3, 7), (3, 6),
(4, 9), (4, 8);

INSERT INTO Issue (title, description, priority, status, type, created_at, done_at, creator_id, assignee_id, project_id, tags) VALUES
('Login Page Crash', 'App crashes on Safari mobile', 'CRITICAL', 'ONGOING', 'BUG', '2026-09-20 10:00:00+02', NULL, 5, 2, 1, 'frontend,ios'),
('API Rate Limiting', 'Implement Redis rate limiter', 'HIGH', 'TODO', 'FEATURE', '2026-09-21 11:30:00+02', NULL, 2, NULL, 1, 'backend,security'),
('Update Readme', 'Add setup steps for Docker', 'LOW', 'DONE', 'DOCUMENTATION', '2026-09-18 09:00:00+02', '2026-09-19 14:00:00+02', 2, 3, 1, 'docs'),
('Button alignment', 'Fix CSS flexbox on dashboard', 'NONE', 'TODO', 'BUG', '2026-09-25 16:00:00+02', NULL, 2, NULL, 1, 'ui'),
('GraphQL Query Optimization', 'Reduce n+1 queries on fetch', 'MEDIUM', 'ONGOING', 'FEATURE', '2026-09-22 15:22:00+02', NULL, 4, 4, 1, 'backend'),
('How to reset test DB?', 'Question about seed scripts', 'NONE', 'DONE', 'QUESTION', '2026-09-24 10:00:00+02', '2026-09-24 11:00:00+02', 3, 3, 1, 'help'),

('S3 Bucket Permissions', 'Fix Access Denied error on upload', 'HIGH', 'DONE', 'BUG', '2026-09-15 08:00:00+02', '2026-09-16 17:30:00+02', 8, 6, 2, 'aws,infra'),
('OAuth2 Integration', 'Support login via Azure AD', 'MEDIUM', 'TODO', 'FEATURE', '2026-09-26 09:00:00+02', NULL, 6, NULL, 2, 'auth,security'),
('CI/CD Pipeline Slowness', 'Runner takes 20m to compile build', 'MEDIUM', 'ONGOING', 'BUG', '2026-09-23 12:00:00+02', NULL, 6, 6, 2, 'devops'),
('Document TLS Setup', 'Add certbot renewal instructions', 'LOW', 'TODO', 'DOCUMENTATION', '2026-09-25 14:15:00+02', NULL, 9, NULL, 2, 'docs,security'),
('Memory Leak in Worker', 'Process OOMs every 24 hours', 'CRITICAL', 'ONGOING', 'BUG', '2026-09-24 18:00:00+02', NULL, 7, 9, 2, 'core'),
('Vulnerability Scan Alert', 'Upgrade lodash dependency in tools', 'HIGH', 'DONE', 'BUG', '2026-09-19 10:10:00+02', '2026-09-20 11:15:00+02', 8, 8, 2, 'security');