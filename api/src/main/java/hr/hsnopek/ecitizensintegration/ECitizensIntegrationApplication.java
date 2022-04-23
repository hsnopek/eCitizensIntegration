package hr.hsnopek.ecitizensintegration;

import hr.hsnopek.ecitizensintegration.domain.feature.role.entity.Role;
import hr.hsnopek.ecitizensintegration.domain.feature.role.enumeration.RoleNameEnum;
import hr.hsnopek.ecitizensintegration.domain.feature.role.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.transaction.Transactional;

@SpringBootApplication
public class ECitizensIntegrationApplication implements CommandLineRunner {
    private final RoleRepository roleRepository;

    public ECitizensIntegrationApplication(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public static void main(String[] args) {
        System.setProperty("javax.xml.accessExternalSchema", "all");
        System.setProperty("javax.xml.accessExternalDTD", "all");
        SpringApplication.run(ECitizensIntegrationApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        roleRepository.save(new Role(RoleNameEnum.USER));
        roleRepository.save(new Role(RoleNameEnum.ADMIN));
    }
}
