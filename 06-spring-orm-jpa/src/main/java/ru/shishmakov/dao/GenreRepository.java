package ru.shishmakov.dao;

import org.springframework.stereotype.Repository;
import ru.shishmakov.domain.Genre;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.Collection;
import java.util.Set;

@Repository
public class GenreRepository implements IRepository<Genre> {
    private EntityManager em;

    @PersistenceUnit
    public void setEm(EntityManagerFactory em) {
        this.em = em.createEntityManager();
    }

    @Override
    public Collection<Genre> getAll() {
        return em.createQuery("select g from Genre g", Genre.class)
                .getResultList();
    }

    @Override
    public Collection<Genre> getAll(Set<Long> ids) {
        return em.createQuery("select g from Genre g where g.id in (:ids)", Genre.class)
                .setParameter("ids", ids)
                .getResultList();
    }
}
