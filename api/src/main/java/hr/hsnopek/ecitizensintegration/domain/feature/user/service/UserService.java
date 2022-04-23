package hr.hsnopek.ecitizensintegration.domain.feature.user.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import hr.hsnopek.ecitizensintegration.domain.feature.nias.dtos.AuthnResponseTokenDto;
import hr.hsnopek.ecitizensintegration.domain.feature.person.entity.Person;
import hr.hsnopek.ecitizensintegration.domain.feature.user.entity.QUser;
import hr.hsnopek.ecitizensintegration.domain.feature.user.entity.User;
import hr.hsnopek.ecitizensintegration.domain.feature.user.repository.UserRepository;
import hr.hsnopek.ecitizensintegration.domain.feature.usersession.entity.QUserSession;
import hr.hsnopek.ecitizensintegration.domain.feature.usersession.entity.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class UserService {

    @Autowired
    EntityManager entityManager;

    final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByIdent(String subjectId) {
        return userRepository.findOneByIdent(subjectId);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User createNewUser(AuthnResponseTokenDto token, Person person, UserSession userSession) {
        User user = new User();
        user.setIdent(token.getIdent());
        user.setOib(token.getOib());
        user.setDn(token.getDn());
        user.setUserSessions(List.of(userSession));
        user.setPerson(person);
        return user;
    }

    public User findByIdentAndUserDevice(String ident, String userAgent) {
        QUser qUser = QUser.user;
        QUserSession qUserSession = QUserSession.userSession;

        JPAQuery query = new JPAQuery(entityManager);
        query
                .select(qUser, qUserSession)
                .from(QUser.user)
                .innerJoin(qUserSession)
                .on(
                        qUserSession.user.userId.eq(qUser.userId)
                                .and(qUserSession.deviceId.eq(userAgent)
                                        .and(qUserSession.sessionIndex.isNotNull()))
                )
                .where(
                        qUser.ident.equalsIgnoreCase(ident)
                                .and(qUser.active.eq(true))
                );

        List<Tuple> results = query.fetch();
        for (Tuple tuple : results) {
            return tuple.get(qUser);
        }

        return null;
    }
}
