package hr.hsnopek.ecitizensintegration.security.service;

import hr.hsnopek.ecitizensintegration.domain.feature.user.entity.User;
import hr.hsnopek.ecitizensintegration.domain.feature.user.repository.UserRepository;
import hr.hsnopek.ecitizensintegration.general.localization.Message;
import hr.hsnopek.ecitizensintegration.general.service.Translator;
import hr.hsnopek.ecitizensintegration.security.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    final UserRepository userRepository;

    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findOneByIdent(username);

        if(user == null)
            throw new UsernameNotFoundException(Translator.toLocale(Message.ERROR_USER_DOESNT_EXIST));

        return new UserPrincipal(username, null);
    }

}
