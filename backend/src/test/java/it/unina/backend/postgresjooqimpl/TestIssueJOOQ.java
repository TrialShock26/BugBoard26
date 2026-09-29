package it.unina.backend.postgresjooqimpl;

import it.unina.backend.dto.IssueDTO;
import it.unina.backend.dto.IssueType;
import it.unina.backend.dto.Priority;
import it.unina.backend.dto.ProjectDTO;
import lombok.Getter;
import lombok.Setter;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.unina.backend.jooq.Tables.ISSUE;
import static it.unina.backend.jooq.Tables.USER_;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestIssueJOOQ {

    @Getter
    @Setter
    static class Executed {
        private final String sql;
        private final Object[] bindings;

        Executed(String sql, Object[] bindings) {
            this.sql = sql;
            this.bindings = bindings;
        }
    }

    private final List<Executed> executed = new ArrayList<>();
    private IssueJOOQ repository;

    @BeforeEach
    void setUp() {
        DSLContext helper = DSL.using(SQLDialect.POSTGRES);

        MockDataProvider provider = ctx -> {
            executed.add(new Executed(ctx.sql(), ctx.bindings()));
            String sql = ctx.sql().toLowerCase();

            if (sql.startsWith("select")) {
                Result<Record1<Integer>> r = helper.newResult(USER_.USER_ID);
                r.add(helper.newRecord(USER_.USER_ID).values(42));
                return new MockResult[] { new MockResult(1, r) };
            }
            if (sql.startsWith("insert")) {
                Result<Record1<Integer>> r = helper.newResult(ISSUE.ISSUE_ID);
                r.add(helper.newRecord(ISSUE.ISSUE_ID).values(7));
                return new MockResult[] { new MockResult(1, r) };
            }
            return new MockResult[] { new MockResult(1) };
        };

        DSLContext ctx = DSL.using(new MockConnection(provider), SQLDialect.POSTGRES);
        repository = new IssueJOOQ(ctx);
    }

    private IssueDTO baseDto() {
        IssueDTO dto = new IssueDTO();
        dto.setTitle("Bug");
        dto.setDescription("desc");
        dto.setType(IssueType.BUG);
        dto.setProject(new ProjectDTO(1, "aProject"));
        return dto;
    }

    @Test
    void newIssue_noOptionalFields_selectNInsert() {
        repository.newIssue(baseDto(), "a@b.it");

        assertEquals(2, executed.size());
        assertTrue(executed.get(0).getSql().toLowerCase().startsWith("select"));
        assertTrue(executed.get(1).getSql().toLowerCase().startsWith("insert"));
    }

    @Test
    void newIssue_withPriority_updatePriority() {
        IssueDTO dto = baseDto();
        dto.setPriority(Priority.HIGH);

        repository.newIssue(dto, "a@b.it");

        assertEquals(3, executed.size());
        Executed update = executed.get(2);
        assertTrue(update.getSql().toLowerCase().contains("update"));
        assertTrue(update.getSql().toLowerCase().contains("priority"));
        assertEquals(7, update.getBindings()[update.getBindings().length - 1]);
    }

    @Test
    void newIssue_withImage_updateImage() {
        IssueDTO dto = baseDto();
        dto.setImage(new byte[] {1, 2, 3});

        repository.newIssue(dto, "a@b.it");

        assertEquals(3, executed.size());
        assertTrue(executed.get(2).getSql().toLowerCase().contains("image"));
    }

    @Test
    void newIssue_withTags_updateTags() {
        IssueDTO dto = baseDto();
        dto.setTags(List.of("ui", "urgent"));

        repository.newIssue(dto, "a@b.it");

        Executed update = executed.get(2);
        assertTrue(update.getSql().toLowerCase().contains("tags"));
        assertEquals("ui,urgent", update.getBindings()[0]);
    }

    @Test
    void newIssue_allFields_allUpdates() {
        IssueDTO dto = baseDto();
        dto.setPriority(Priority.LOW);
        dto.setImage(new byte[] {1});
        dto.setTags(List.of("x"));

        repository.newIssue(dto, "a@b.it");

        assertEquals(5, executed.size());
    }
}