package it.unina.backend.postgresjooqimpl;

import it.unina.backend.dao.UserDAO;
import it.unina.backend.dto.UserDTO;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static it.unina.backend.jooq.Tables.USER_;

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
}