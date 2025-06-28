package it.camera.stampati.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;

public class CustomRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements CustomRepository<T, ID> {
	
	@PersistenceContext
    private final EntityManager em;
	
	public CustomRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.em = em;
    }

    @Override
    public void refresh(T entity) {
        em.refresh(entity);
    }

    @Override
    public void clear() {
        em.getEntityManagerFactory().getCache().evictAll();
        em.clear();
    }

}