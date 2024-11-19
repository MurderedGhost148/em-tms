package ru.em.tms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.em.tms.model.db.User;

public interface UserRepo extends JpaRepository<User, Integer> {
}
