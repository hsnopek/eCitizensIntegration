package hr.hsnopek.ecitizensintegration.domain.feature.usersession.repository;

import hr.hsnopek.ecitizensintegration.domain.feature.usersession.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
}
