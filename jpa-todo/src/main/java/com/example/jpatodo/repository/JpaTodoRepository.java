package com.example.jpatodo.repository;

import com.example.jpatodo.domain.Todo;
import com.ligero.beans.stereotype.Repository;
import com.ligero.jpa.Jpa;

import java.util.List;
import java.util.Optional;

/**
 * JPA-backed repository: reads run with a short-lived entity manager, writes
 * run inside a transaction — all through the {@link Jpa} helper.
 */
@Repository
public class JpaTodoRepository implements TodoRepository {

    private final Jpa jpa;

    public JpaTodoRepository(Jpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Todo> all() {
        return jpa.read(em ->
            em.createQuery("select t from Todo t order by t.id", Todo.class).getResultList());
    }

    @Override
    public Optional<Todo> find(long id) {
        return jpa.read(em -> Optional.ofNullable(em.find(Todo.class, id)));
    }

    @Override
    public Todo add(String title) {
        return jpa.tx(em -> {
            Todo todo = new Todo(title);
            em.persist(todo);
            return todo;
        });
    }

    @Override
    public boolean complete(long id) {
        return jpa.tx(em -> {
            Todo todo = em.find(Todo.class, id);
            if (todo == null) {
                return false;
            }
            todo.setDone(true);
            return true;
        });
    }

    @Override
    public boolean delete(long id) {
        return jpa.tx(em -> {
            Todo todo = em.find(Todo.class, id);
            if (todo == null) {
                return false;
            }
            em.remove(todo);
            return true;
        });
    }
}
