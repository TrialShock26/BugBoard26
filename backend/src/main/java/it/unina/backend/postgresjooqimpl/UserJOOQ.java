package it.unina.backend.postgresjooqimpl;

import it.unina.backend.dao.UserDAO;
import it.unina.backend.dto.*;
import it.unina.backend.jooq.Routines;
import it.unina.backend.jooq.tables.Team;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static it.unina.backend.jooq.Tables.*;

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
}