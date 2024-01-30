package edu.poly.lab5.repository;

import edu.poly.lab5.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategotyRepository extends JpaRepository<Category,Long> {
}
