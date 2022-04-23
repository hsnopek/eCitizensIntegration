package hr.hsnopek.ecitizensintegration.domain.feature.person.repository;

import hr.hsnopek.ecitizensintegration.domain.feature.person.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByTid(String tid);
}
