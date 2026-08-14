package jakarta.persistence;
public interface EntityManager {
    Query createQuery(String jpql);
}
