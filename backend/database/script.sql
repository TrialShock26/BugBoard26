--ENUMS
CREATE TYPE user_type AS ENUM ('DEV', 'ADMIN');
CREATE TYPE issue_type AS ENUM ('BUG', 'DOCUMENTATION', 'FEATURE', 'QUESTION');
CREATE TYPE status AS ENUM ('TODO', 'ONGOING', 'DONE');
CREATE TYPE priority AS ENUM ('NONE', 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL');
--ENTITIES
CREATE TABLE IF NOT EXISTS User_ (
    user_id SERIAL PRIMARY KEY,
    email VARCHAR(64) NOT NULL UNIQUE,
    hashed_password VARCHAR(255) NOT NULL,
    type user_type DEFAULT 'DEV'
);

CREATE TABLE IF NOT EXISTS Project (
    project_id SERIAL PRIMARY KEY,
    name VARCHAR(30) UNIQUE
);

CREATE TABLE IF NOT EXISTS Issue (
    issue_id SERIAL PRIMARY KEY,
    title VARCHAR(30) NOT NULL,
    description TEXT NOT NULL,
    priority priority DEFAULT 'NONE',
    status status DEFAULT 'TODO',
    type issue_type NOT NULL,
    image BYTEA,
    tags TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    done_at TIMESTAMPTZ,
    creator_id INT NOT NULL DEFAULT 0,
    assignee_id INT,
    project_id INT,
    FOREIGN KEY (creator_id) REFERENCES User_(user_id)
        ON UPDATE CASCADE
        ON DELETE SET DEFAULT,
    FOREIGN KEY (assignee_id) REFERENCES User_(user_id)
        ON UPDATE CASCADE,
        --ON DELETE TRIGGER TO SET 0
    FOREIGN KEY (project_id) REFERENCES Project(project_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT natural_key_issue UNIQUE(title, description),
    CONSTRAINT coherent_timestamps CHECK ((done_at IS NULL) OR (created_at < done_at))
);

CREATE TABLE IF NOT EXISTS Team (
    team_id SERIAL PRIMARY KEY,
    name VARCHAR(30),
    project_id INT,
    FOREIGN KEY (project_id) REFERENCES Project(project_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT natural_key_team UNIQUE(name, project_id)
);

CREATE TABLE IF NOT EXISTS Collaboration (
    team_id INT NOT NULL,
    user_id INT NOT NULL DEFAULT 0,
    PRIMARY KEY (team_id, user_id),
    FOREIGN KEY (team_id) REFERENCES Team(team_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES User_(user_id)
        ON UPDATE CASCADE
        ON DELETE SET DEFAULT
);

CREATE OR REPLACE FUNCTION default_value_assignee_f()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE Issue
    SET assignee_id = 0
    WHERE assignee_id = OLD.user_id;

    RETURN OLD;
END;
$$;
CREATE OR REPLACE TRIGGER default_value_assignee_t
    BEFORE DELETE ON User_
    FOR EACH ROW
    EXECUTE FUNCTION default_value_assignee_f();

CREATE OR REPLACE PROCEDURE add_new_teams(
    IN team_names TEXT, IN project_name Project.name%TYPE)
LANGUAGE plpgsql
AS $$
DECLARE
    new_team Team.name%TYPE;
    pos      INT;
    p_id     INT;
BEGIN
    IF EXISTS (SELECT 1 FROM Project WHERE name = project_name) THEN
        RAISE EXCEPTION 'This project name already exists';
    END IF;
    INSERT INTO Project(name) VALUES (project_name) RETURNING project_id INTO p_id;
    BEGIN
        LOOP
            pos := STRPOS(team_names, ',');
            EXIT WHEN pos <= 0;
            new_team := SUBSTR(team_names, 1, pos - 1);
            team_names := LTRIM(team_names, new_team);
            team_names := LTRIM(team_names, ',');
            INSERT INTO Team (name, project_id)
                VALUES (new_team, p_id);
        END LOOP;
    EXCEPTION
        WHEN unique_violation THEN
            RAISE EXCEPTION 'Team names have to be different within the same project';
    END;
END;
$$;

CREATE OR REPLACE FUNCTION not_in_two_teams_f()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
    p_id Project.project_id%TYPE;
BEGIN
    SELECT project_id INTO p_id
    FROM Team
    WHERE team_id = NEW.team_id;

    IF EXISTS (
        SELECT 1
        FROM Collaboration NATURAL JOIN Team
        WHERE project_id = p_id AND user_id = NEW.user_id
    ) THEN
        RAISE EXCEPTION 'You already are a collaborator in this project.';
    END IF;

    RETURN NEW;
END;
$$;
CREATE OR REPLACE TRIGGER not_in_two_teams_t
    BEFORE INSERT ON Collaboration
    FOR EACH ROW
    EXECUTE FUNCTION not_in_two_teams_f();

INSERT INTO User_ (user_id, email, hashed_password) VALUES (0, 'deleted.user@default.com', 'no_password');
INSERT INTO User_ (email, hashed_password, type) VALUES ('admin@default.com', '$2a$12$WpLJtF7qmh7d90L3y8.QreM2dTvMPAVVddvX5OJ8eiuWoNFkNz6eK', 'ADMIN');