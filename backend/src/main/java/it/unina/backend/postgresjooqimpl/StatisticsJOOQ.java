package it.unina.backend.postgresjooqimpl;

import it.unina.backend.dao.StatisticsDAO;
import it.unina.backend.dto.*;
import it.unina.backend.jooq.enums.Status;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static it.unina.backend.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
public class StatisticsJOOQ implements StatisticsDAO {
    private final DSLContext context;

    public StatisticsJOOQ(DSLContext context) {
        this.context = context;
    }

    @Override
    public DashboardDTO getDashboardData(String email) {
        DashboardDTO result = new DashboardDTO();
        List<Integer> myProjects = context.select(PROJECT.PROJECT_ID)
                .from(PROJECT).join(TEAM).using(PROJECT.PROJECT_ID)
                .naturalJoin(COLLABORATION).naturalJoin(USER_)
                .where(USER_.EMAIL.eq(email)).fetchInto(Integer.class);

        result.setOpenBugs(context.select(count(ISSUE.ISSUE_ID)).from(ISSUE)
                .where(ISSUE.STATUS.eq(Status.TODO)).and(ISSUE.PROJECT_ID.in(myProjects))
                .fetchOneInto(Integer.class));

        result.setOngoingBugs(context.select(count(ISSUE.ISSUE_ID)).from(ISSUE)
                .where(ISSUE.STATUS.eq(Status.ONGOING)).and(ISSUE.PROJECT_ID.in(myProjects))
                .fetchOneInto(Integer.class));

        result.setDoneBugs(context.select(count(ISSUE.ISSUE_ID)).from(ISSUE)
                .where(ISSUE.STATUS.eq(Status.DONE)).and(ISSUE.PROJECT_ID.in(myProjects))
                .fetchOneInto(Integer.class));

        result.setTotalBugs(context.select(count(ISSUE.ISSUE_ID)).from(ISSUE)
                .where(ISSUE.PROJECT_ID.in(myProjects))
                .fetchOneInto(Integer.class));

        result.setAverageGlobalResolutionTime(context.select(
                        avg(extract(ISSUE.DONE_AT, org.jooq.DatePart.EPOCH)
                                .sub(extract(ISSUE.CREATED_AT, org.jooq.DatePart.EPOCH))
                                .div(3600))
                ).from(ISSUE)
                .where(ISSUE.STATUS.eq(it.unina.backend.jooq.enums.Status.DONE))
                .and(ISSUE.PROJECT_ID.in(myProjects)).fetchOneInto(Double.class));

        result.setBugsPerUser(context.select(USER_.EMAIL, USER_.TYPE, count(ISSUE.ISSUE_ID).as("total"))
                .from(USER_).naturalJoin(COLLABORATION).naturalJoin(TEAM)
                .join(PROJECT).using(TEAM.PROJECT_ID)
                .leftJoin(ISSUE).on(USER_.USER_ID.eq(ISSUE.ASSIGNEE_ID))
                .and(ISSUE.STATUS.ne(Status.DONE))
                .where(USER_.USER_ID.ne(0)).and(PROJECT.PROJECT_ID.in(myProjects))
                .groupBy(USER_.EMAIL, USER_.TYPE)
                .fetchMap(
                        dataRecord -> {
                            UserDTO dto = new UserDTO();
                            dto.setEmail(dataRecord.get(USER_.EMAIL));
                            dto.setType(dataRecord.get(USER_.TYPE, UserType.class));
                            return dto;
                        },
                        dataRecord -> dataRecord.get(field("total"), Integer.class)
                ));

        result.setAverageResolutionTimePerUser(context.select(USER_.EMAIL, USER_.TYPE,
                        avg(extract(ISSUE.DONE_AT, org.jooq.DatePart.EPOCH)
                                .sub(extract(ISSUE.CREATED_AT, org.jooq.DatePart.EPOCH))
                                .div(3600)
                        ).as("avg_time")
                )
                .from(USER_).naturalJoin(COLLABORATION).naturalJoin(TEAM)
                .join(PROJECT).using(TEAM.PROJECT_ID)
                .leftJoin(ISSUE).on(USER_.USER_ID.eq(ISSUE.ASSIGNEE_ID))
                .and(ISSUE.STATUS.eq(Status.DONE))
                .where(USER_.USER_ID.ne(0)).and(PROJECT.PROJECT_ID.in(myProjects))
                .groupBy(USER_.EMAIL, USER_.TYPE)
                .fetchMap(
                        dataRecord -> {
                            UserDTO dto = new UserDTO();
                            dto.setEmail(dataRecord.get(USER_.EMAIL));
                            dto.setType(dataRecord.get(USER_.TYPE, UserType.class));
                            return dto;
                        },
                        dataRecord -> dataRecord.get(field("avg_time"), Double.class)
                ));

        return result;
    }

    @Override
    public ReportDTO getReportData(Integer month, Integer year, String project) {
        ReportDTO result = new ReportDTO();

        result.setTotalBugs(context.select(count(ISSUE.ISSUE_ID)).from(ISSUE).naturalJoin(PROJECT)
                .where(ISSUE.STATUS.ne(Status.DONE)).and(PROJECT.NAME.eq(project))
                .and(month(ISSUE.CREATED_AT).eq(month))
                .and(year(ISSUE.CREATED_AT).eq(year))
                .fetchOneInto(Integer.class));

        result.setTotalHandledBugs(context.select(count(ISSUE.ISSUE_ID)).from(ISSUE).naturalJoin(PROJECT)
                .where(ISSUE.STATUS.eq(Status.DONE)).and(PROJECT.NAME.eq(project))
                .and(month(ISSUE.CREATED_AT).eq(month))
                .and(year(ISSUE.CREATED_AT).eq(year))
                .fetchOneInto(Integer.class));

        result.setAverageGlobalResolutionTime(context.select(
                        avg(extract(ISSUE.DONE_AT, org.jooq.DatePart.EPOCH)
                                .sub(extract(ISSUE.CREATED_AT, org.jooq.DatePart.EPOCH))
                                .div(3600))
                ).from(ISSUE).naturalJoin(PROJECT)
                .where(ISSUE.STATUS.eq(it.unina.backend.jooq.enums.Status.DONE))
                .and(PROJECT.NAME.eq(project))
                .and(month(ISSUE.CREATED_AT).eq(month))
                .and(year(ISSUE.CREATED_AT).eq(year)).fetchOneInto(Double.class));

        result.setTotalBugsPerTeam(context.select(TEAM.NAME, count(ISSUE.ISSUE_ID).as("total"))
                .from(USER_).naturalJoin(COLLABORATION).naturalJoin(TEAM)
                .join(PROJECT).using(TEAM.PROJECT_ID)
                .leftJoin(ISSUE).on(USER_.USER_ID.eq(ISSUE.ASSIGNEE_ID))
                .and(ISSUE.STATUS.ne(Status.DONE))
                .and(month(ISSUE.CREATED_AT).eq(month))
                .and(year(ISSUE.CREATED_AT).eq(year))
                .where(USER_.USER_ID.ne(0)).and(PROJECT.NAME.eq(project))
                .groupBy(TEAM.NAME)
                .fetchMap(
                        dataRecord -> {
                            TeamDTO dto = new TeamDTO();
                            dto.setName(dataRecord.get(TEAM.NAME));
                            return dto;
                        },
                        dataRecord -> dataRecord.get(field("total"), Integer.class)
                ));

        result.setTotalHandledBugsPerTeam(context.select(TEAM.NAME, count(ISSUE.ISSUE_ID).as("total"))
                .from(USER_).naturalJoin(COLLABORATION).naturalJoin(TEAM)
                .join(PROJECT).using(TEAM.PROJECT_ID)
                .leftJoin(ISSUE).on(USER_.USER_ID.eq(ISSUE.ASSIGNEE_ID))
                .and(ISSUE.STATUS.eq(Status.DONE))
                .and(month(ISSUE.CREATED_AT).eq(month))
                .and(year(ISSUE.CREATED_AT).eq(year))
                .where(USER_.USER_ID.ne(0)).and(PROJECT.NAME.eq(project))
                .groupBy(TEAM.NAME)
                .fetchMap(
                        dataRecord -> {
                            TeamDTO dto = new TeamDTO();
                            dto.setName(dataRecord.get(TEAM.NAME));
                            return dto;
                        },
                        dataRecord -> dataRecord.get(field("total"), Integer.class)
                ));

        result.setAverageResolutionTimePerTeam(context.select(TEAM.NAME,
                        avg(extract(ISSUE.DONE_AT, org.jooq.DatePart.EPOCH)
                                .sub(extract(ISSUE.CREATED_AT, org.jooq.DatePart.EPOCH))
                                .div(3600)
                        ).as("avg_time"))
                .from(USER_).naturalJoin(COLLABORATION).naturalJoin(TEAM)
                .join(PROJECT).using(TEAM.PROJECT_ID)
                .leftJoin(ISSUE).on(USER_.USER_ID.eq(ISSUE.ASSIGNEE_ID))
                .and(ISSUE.STATUS.eq(Status.DONE))
                .and(month(ISSUE.CREATED_AT).eq(month))
                .and(year(ISSUE.CREATED_AT).eq(year))
                .where(USER_.USER_ID.ne(0)).and(PROJECT.NAME.eq(project))
                .groupBy(TEAM.NAME)
                .fetchMap(
                        dataRecord -> {
                            TeamDTO dto = new TeamDTO();
                            dto.setName(dataRecord.get(TEAM.NAME));
                            return dto;
                        },
                        dataRecord -> dataRecord.get(field("avg_time"), Double.class)
                ));

        result.setTotalBugsPerUser(context.select(USER_.EMAIL, USER_.TYPE, count(ISSUE.ISSUE_ID).as("total"))
                .from(USER_).naturalJoin(COLLABORATION).naturalJoin(TEAM)
                .join(PROJECT).using(TEAM.PROJECT_ID)
                .leftJoin(ISSUE).on(USER_.USER_ID.eq(ISSUE.ASSIGNEE_ID))
                .and(ISSUE.STATUS.ne(Status.DONE))
                .and(month(ISSUE.CREATED_AT).eq(month))
                .and(year(ISSUE.CREATED_AT).eq(year))
                .where(USER_.USER_ID.ne(0)).and(PROJECT.NAME.eq(project))
                .groupBy(USER_.EMAIL, USER_.TYPE)
                .fetchMap(
                        dataRecord -> {
                            UserDTO dto = new UserDTO();
                            dto.setEmail(dataRecord.get(USER_.EMAIL));
                            dto.setType(dataRecord.get(USER_.TYPE, UserType.class));
                            return dto;
                        },
                        dataRecord -> dataRecord.get(field("total"), Integer.class)
                ));

        result.setTotalHandledBugsPerUser(context.select(USER_.EMAIL, USER_.TYPE, count(ISSUE.ISSUE_ID).as("total"))
                .from(USER_).naturalJoin(COLLABORATION).naturalJoin(TEAM)
                .join(PROJECT).using(TEAM.PROJECT_ID)
                .leftJoin(ISSUE).on(USER_.USER_ID.eq(ISSUE.ASSIGNEE_ID))
                .and(ISSUE.STATUS.eq(Status.DONE))
                .and(month(ISSUE.CREATED_AT).eq(month))
                .and(year(ISSUE.CREATED_AT).eq(year))
                .where(USER_.USER_ID.ne(0)).and(PROJECT.NAME.eq(project))
                .groupBy(USER_.EMAIL, USER_.TYPE)
                .fetchMap(
                        dataRecord -> {
                            UserDTO dto = new UserDTO();
                            dto.setEmail(dataRecord.get(USER_.EMAIL));
                            dto.setType(dataRecord.get(USER_.TYPE, UserType.class));
                            return dto;
                        },
                        dataRecord -> dataRecord.get(field("total"), Integer.class)
                ));

        result.setAverageResolutionTimePerUser(context.select(USER_.EMAIL, USER_.TYPE,
                        avg(extract(ISSUE.DONE_AT, org.jooq.DatePart.EPOCH)
                                .sub(extract(ISSUE.CREATED_AT, org.jooq.DatePart.EPOCH))
                                .div(3600)
                        ).as("avg_time")
                )
                .from(USER_).naturalJoin(COLLABORATION).naturalJoin(TEAM)
                .join(PROJECT).using(TEAM.PROJECT_ID)
                .leftJoin(ISSUE).on(USER_.USER_ID.eq(ISSUE.ASSIGNEE_ID))
                .and(ISSUE.STATUS.eq(Status.DONE))
                .and(month(ISSUE.CREATED_AT).eq(month))
                .and(year(ISSUE.CREATED_AT).eq(year))
                .where(USER_.USER_ID.ne(0)).and(PROJECT.NAME.eq(project))
                .groupBy(USER_.EMAIL, USER_.TYPE)
                .fetchMap(
                        dataRecord -> {
                            UserDTO dto = new UserDTO();
                            dto.setEmail(dataRecord.get(USER_.EMAIL));
                            dto.setType(dataRecord.get(USER_.TYPE, UserType.class));
                            return dto;
                        },
                        dataRecord -> dataRecord.get(field("avg_time"), Double.class)
                ));

        return result;
    }
}