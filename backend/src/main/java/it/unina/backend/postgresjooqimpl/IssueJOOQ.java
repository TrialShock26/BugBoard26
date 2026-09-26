package it.unina.backend.postgresjooqimpl;

import it.unina.backend.dao.IssueDAO;
import it.unina.backend.dto.*;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

import static it.unina.backend.jooq.Tables.*;

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
                                        .naturalJoin(TEAM).naturalJoin(COLLABORATION).naturalJoin(USER_)
                                        .where(USER_.EMAIL.eq(email))
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

    @Override
    public void newIssue(IssueDTO dto, String email) {
        Integer userId = context.select(USER_.USER_ID).from(USER_).where(USER_.EMAIL.eq(email)).fetchOneInto(Integer.class);

        Integer issueId = context.insertInto(ISSUE, ISSUE.TITLE, ISSUE.DESCRIPTION, ISSUE.TYPE,
                        ISSUE.CREATED_AT, ISSUE.CREATOR_ID, ISSUE.PROJECT_ID)
                .values(dto.getTitle(), dto.getDescription(), it.unina.backend.jooq.enums.IssueType.valueOf(dto.getType().name()),
                        OffsetDateTime.now(), userId, dto.getProject().getProjectId())
                .returning(ISSUE.ISSUE_ID).fetchOneInto(Integer.class);

        if (dto.getPriority() != null) {
            context.update(ISSUE).set(ISSUE.PRIORITY, it.unina.backend.jooq.enums.Priority.valueOf(dto.getPriority().name()))
                    .where(ISSUE.ISSUE_ID.eq(issueId)).execute();
        }
        if (dto.getImage() != null) {
            context.update(ISSUE).set(ISSUE.IMAGE, dto.getImage())
                    .where(ISSUE.ISSUE_ID.eq(issueId)).execute();
        }
        if (dto.getTags() != null) {
            context.update(ISSUE).set(ISSUE.TAGS, String.join(",", dto.getTags()))
                    .where(ISSUE.ISSUE_ID.eq(issueId)).execute();
        }
    }

    @Override
    public void handleIssue(int id, String email) {
        context.update(ISSUE)
                .set(ISSUE.ASSIGNEE_ID, context.select(USER_.USER_ID).from(USER_).where(USER_.EMAIL.eq(email)))
                .set(ISSUE.STATUS, it.unina.backend.jooq.enums.Status.ONGOING)
                .where(ISSUE.ISSUE_ID.eq(id))
                .execute();
    }

    @Override
    public void resolveIssue(int id) {
        context.update(ISSUE)
                .set(ISSUE.STATUS, it.unina.backend.jooq.enums.Status.DONE)
                .set(ISSUE.DONE_AT, OffsetDateTime.now())
                .where(ISSUE.ISSUE_ID.eq(id))
                .execute();
    }

    @Override
    public byte[] getImage(int id) {
        return context.select(ISSUE.IMAGE).from(ISSUE).where(ISSUE.ISSUE_ID.eq(id)).fetchOneInto(byte[].class);
    }
}