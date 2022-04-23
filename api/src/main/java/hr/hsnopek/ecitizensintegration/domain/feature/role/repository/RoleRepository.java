package hr.hsnopek.ecitizensintegration.domain.feature.role.repository;

import hr.hsnopek.ecitizensintegration.domain.feature.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
