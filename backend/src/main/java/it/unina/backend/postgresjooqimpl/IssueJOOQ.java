package it.unina.backend.postgresjooqimpl;

import it.unina.backend.dao.IssueDAO;
import it.unina.backend.dto.*;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static it.unina.backend.jooq.Tables.ISSUE;
import static it.unina.backend.jooq.Tables.PROJECT;

@Repository
public class IssueJOOQ implements IssueDAO {
    private DSLContext context;

    public IssueJOOQ(DSLContext context) {
        this.context = context;
    }

    @Override
    public List<IssueDTO> getAllIssues(String email) {
        return context.select(
                        ISSUE.ISSUE_ID,
                        ISSUE.TITLE,
                        ISSUE.DESCRIPTION,
                        ISSUE.PRIORITY,
                        ISSUE.STATUS,
                        ISSUE.TYPE,
                        ISSUE.TAGS,
                        ISSUE.CREATED_AT,
                        ISSUE.DONE_AT,
                        ISSUE.issueCreatorIdFkey().EMAIL,
                        ISSUE.issueAssigneeIdFkey().EMAIL,
                        ISSUE.project().NAME
                )
                .from(ISSUE)
                .where(ISSUE.PROJECT_ID
                        .in(
                                context.select(PROJECT.PROJECT_ID)
                                        .from(PROJECT)
                                        .where(PROJECT.team().user_().EMAIL.eq(email))
                        )).fetch(issueRecord -> new IssueDTO(
                        issueRecord.get(ISSUE.ISSUE_ID),
                        issueRecord.get(ISSUE.TITLE),
                        issueRecord.get(ISSUE.DESCRIPTION),
                        issueRecord.get(ISSUE.PRIORITY, Priority.class),
                        issueRecord.get(ISSUE.STATUS, Status.class),
                        issueRecord.get(ISSUE.TYPE, IssueType.class),
                        null,
                        issueRecord.get(ISSUE.TAGS),
                        issueRecord.get(ISSUE.CREATED_AT),
                        issueRecord.get(ISSUE.DONE_AT),
                        new UserDTO(null, issueRecord.get(ISSUE.issueCreatorIdFkey().EMAIL), null, null),
                        new UserDTO(null, issueRecord.get(ISSUE.issueAssigneeIdFkey().EMAIL), null, null),
                        new ProjectDTO(null, issueRecord.get(ISSUE.project().NAME))
                ));
    }
}