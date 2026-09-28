package it.unina.backend.postgresjooqimpl;

import it.unina.backend.dao.UserDAO;
import it.unina.backend.dto.*;
import it.unina.backend.jooq.Routines;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static it.unina.backend.jooq.Tables.*;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.field;

@Repository
public class UserJOOQ implements UserDAO {
    private final DSLContext context;

    public UserJOOQ(DSLContext context){
        this.context = context;
    }

    @Override
    public Optional<UserDTO> login(String email){
        return context.selectFrom(USER_)
                .where(USER_.EMAIL.eq(email))
                .fetchOptionalInto(UserDTO.class);
    }

    @Override
    public void createProject(String name, String teamNames) {
        Routines.addNewTeams(context.configuration(), teamNames, name);
    }

    @Override
    public List<ProjectDTO> getProjects() {
        return context.selectFrom(PROJECT)
                .fetchInto(ProjectDTO.class);
    }

    @Override
    public List<TeamDTO> getTeams(int id) {
        return context.select(
                        TEAM.TEAM_ID,
                        TEAM.NAME
                )
                .from(TEAM)
                .where(PROJECT.PROJECT_ID.eq(id))
                .fetch(teamRecord -> new TeamDTO(
                        teamRecord.get(TEAM.TEAM_ID),
                        teamRecord.get(TEAM.NAME),
                        null
                ));
    }

    @Override
    public void joinTeam(int id, String email) {
        context.insertInto(COLLABORATION)
                .values(id, context.select(USER_.USER_ID).from(USER_).where(USER_.EMAIL.eq(email)))
                .execute();
    }

    @Override
    public void newUser(String email, String hashedPassword, UserType type) {
        context.insertInto(USER_, USER_.EMAIL, USER_.HASHED_PASSWORD, USER_.TYPE)
                .values(email, hashedPassword, it.unina.backend.jooq.enums.UserType.valueOf(type.name()))
                .execute();
    }

    @Override
    public List<UserDTO> getSuggestion() {
        return context.select(USER_.USER_ID, USER_.EMAIL, count(ISSUE.ISSUE_ID).as("workload"))
                .from(USER_).leftJoin(ISSUE).on(USER_.USER_ID.eq(ISSUE.ASSIGNEE_ID)).and(ISSUE.STATUS.eq(it.unina.backend.jooq.enums.Status.ONGOING))
                .where(USER_.USER_ID.ne(0))
                .groupBy(USER_.USER_ID, USER_.EMAIL)
                .orderBy(field("workload")).limit(3)
                .fetch(dataRecord -> new UserDTO(
                        dataRecord.get(USER_.USER_ID),
                        dataRecord.get(USER_.EMAIL),
                        null, null
                ));
    }
}