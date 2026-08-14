package org.springframework.data.jpa.repository;
import java.util.List;
import java.util.Optional;

public interface JpaRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    void delete(T entity);
    long count();
}
