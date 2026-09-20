package it.unina.backend.postgresjooqimpl;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class UserJOOQ {
    private final DSLContext context;

    public UserJOOQ(DSLContext context){
        this.context = context;
    }


}
