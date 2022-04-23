package hr.hsnopek.ecitizensintegration.domain.feature.user.repository;

import hr.hsnopek.ecitizensintegration.domain.feature.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findOneByIdent(String ident);
}
