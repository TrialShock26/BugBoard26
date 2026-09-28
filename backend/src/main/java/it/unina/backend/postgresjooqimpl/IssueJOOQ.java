package it.unina.backend.postgresjooqimpl;

import it.unina.backend.dao.IssueDAO;
import it.unina.backend.dto.*;
import org.jooq.DSLContext;
import org.jooq.ResultQuery;
import org.jooq.SelectConditionStep;
import org.jooq.SelectSeekStep1;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static it.unina.backend.jooq.Tables.*;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.field;

@Repository
public class IssueJOOQ implements IssueDAO {
    private DSLContext context;

    public IssueJOOQ(DSLContext context) {
        this.context = context;
    }

    @Override
    public List<IssueDTO> getAllIssues(String email, Map<String, String> requestParams) {
        SelectConditionStep<?> commonStep = context.select(
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
                                        .join(TEAM).using(PROJECT.PROJECT_ID).naturalJoin(COLLABORATION).naturalJoin(USER_)
                                        .where(USER_.EMAIL.eq(email))
                        ));
        if (!requestParams.containsKey("assignee") || requestParams.get("assignee").equals("me")) {
            commonStep = commonStep.and(ISSUE.ASSIGNEE_ID.eq(context.select(USER_.USER_ID).from(USER_).where(USER_.EMAIL.eq(email))));
        }
        if (requestParams.containsKey("type")) {
            commonStep = commonStep.and(ISSUE.TYPE.eq(it.unina.backend.jooq.enums.IssueType.valueOf(requestParams.get("type"))));
        }
        if (requestParams.containsKey("status")) {
            commonStep = commonStep.and(ISSUE.STATUS.eq(it.unina.backend.jooq.enums.Status.valueOf(requestParams.get("status"))));
        }
        if (requestParams.containsKey("priority")) {
            commonStep = commonStep.and(ISSUE.PRIORITY.eq(it.unina.backend.jooq.enums.Priority.valueOf(requestParams.get("priority"))));
        }
        ResultQuery<?> query = commonStep;
        if (requestParams.containsKey("sort")) {
            switch (requestParams.get("sort")) {
                case "priority": query = commonStep.orderBy(ISSUE.PRIORITY.desc()); break;
                case "status": query = commonStep.orderBy(ISSUE.STATUS.desc()); break;
                case "title":   query = commonStep.orderBy(ISSUE.TITLE); break;
                default: query = commonStep.orderBy(ISSUE.CREATED_AT.desc()); break;
            }
        }

        return query.fetch(issueRecord -> new IssueDTO(
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
    public boolean handleIssue(int id, String email) {
        IssueDTO issue = context.selectFrom(ISSUE).where(ISSUE.ISSUE_ID.eq(id)).fetchOneInto(IssueDTO.class);
        if (issue.getStatus() != Status.TODO) return false;
        context.update(ISSUE)
                .set(ISSUE.ASSIGNEE_ID, context.select(USER_.USER_ID).from(USER_).where(USER_.EMAIL.eq(email)))
                .set(ISSUE.STATUS, it.unina.backend.jooq.enums.Status.ONGOING)
                .where(ISSUE.ISSUE_ID.eq(id))
                .execute();
        return true;
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

    @Override
    public List<UserDTO> getSuggestion() {
        return context.select(USER_.USER_ID, USER_.EMAIL, count(ISSUE.ISSUE_ID).as("workload"))
                .from(USER_).leftJoin(ISSUE).on(USER_.USER_ID.eq(ISSUE.ASSIGNEE_ID))
                .and(ISSUE.STATUS.eq(it.unina.backend.jooq.enums.Status.ONGOING))
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