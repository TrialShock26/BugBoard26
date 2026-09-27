package it.unina.backend.postgresjooqimpl;

import it.unina.backend.dao.StatisticsDAO;
import it.unina.backend.dto.*;
import it.unina.backend.jooq.enums.Status;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

import static it.unina.backend.jooq.Tables.ISSUE;
import static it.unina.backend.jooq.Tables.USER_;
import static org.jooq.impl.DSL.*;

@Repository
public class StatisticsJOOQ implements StatisticsDAO {
    private final DSLContext context;

    public StatisticsJOOQ(DSLContext context) {
        this.context = context;
    }

    @Override
    public DashboardDTO getDashboardData() {
        DashboardDTO result = new DashboardDTO();
        result.setBugsPerUser(new HashMap<>());
        result.setAverageResolutionTimePerUser(new HashMap<>());

        result.setTotalBugs(context.select(count(ISSUE.ISSUE_ID)).from(ISSUE)
                .where(ISSUE.STATUS.ne(Status.DONE)).fetchOneInto(Integer.class));

        result.setBugsPerUser(context.select(USER_.EMAIL, USER_.TYPE, count(ISSUE.ISSUE_ID).as("total"))
                .from(USER_).leftJoin(ISSUE).on(USER_.USER_ID.eq(ISSUE.ASSIGNEE_ID))
                .and(ISSUE.STATUS.ne(Status.DONE)).where(USER_.USER_ID.ne(0))
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

        result.setAverageGlobalResolutionTime(context.select(
                    avg(extract(ISSUE.DONE_AT, org.jooq.DatePart.EPOCH)
                            .sub(extract(ISSUE.CREATED_AT, org.jooq.DatePart.EPOCH))
                            .div(3600))
                ).from(ISSUE)
                .where(ISSUE.STATUS.eq(it.unina.backend.jooq.enums.Status.DONE)).fetchOneInto(Double.class));

        result.setAverageResolutionTimePerUser(context.select(USER_.EMAIL, USER_.TYPE,
                        avg(extract(ISSUE.DONE_AT, org.jooq.DatePart.EPOCH)
                                        .sub(extract(ISSUE.CREATED_AT, org.jooq.DatePart.EPOCH))
                                        .div(3600)
                        ).as("avg_time")
                )
                .from(USER_)
                .leftJoin(ISSUE).on(USER_.USER_ID.eq(ISSUE.ASSIGNEE_ID))
                .and(ISSUE.STATUS.eq(Status.DONE))
                .where(USER_.USER_ID.ne(0))
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
    public ReportDTO getReportData(String month, String year) {}
}