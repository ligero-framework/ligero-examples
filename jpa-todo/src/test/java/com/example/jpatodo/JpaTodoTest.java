package com.example.jpatodo;

import com.ligero.Modules;
import com.ligero.beans.Beans;
import com.ligero.devtools.Devtools;
import com.ligero.test.LigeroTest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives the JPA/Hibernate-on-H2 Todo API end to end through the same module
 * wiring as production, on an ephemeral port.
 */
class JpaTodoTest {

    @Test
    void createsListsAndValidatesTodos() {
        try (LigeroTest test = LigeroTest.create(app -> {
            Devtools devtools = Devtools.create();
            Beans beans = Modules.install(app, devtools.recorder(), new TodosModule());
            devtools.install(app, beans);
        })) {
            // create
            LigeroTest.TestResponse created =
                test.post("/todos").json("{\"title\":\"Write tests\"}").execute();
            assertEquals(201, created.status());
            assertTrue(created.body().contains("Write tests"));

            // list reflects it
            LigeroTest.TestResponse list = test.get("/todos").execute();
            assertEquals(200, list.status());
            assertTrue(list.body().contains("Write tests"));

            // validation: a blank title is rejected
            assertEquals(400, test.post("/todos").json("{\"title\":\"\"}").execute().status());
        }
    }
}
