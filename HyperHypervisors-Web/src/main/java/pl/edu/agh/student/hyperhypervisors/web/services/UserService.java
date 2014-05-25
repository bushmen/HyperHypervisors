package pl.edu.agh.student.hyperhypervisors.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.ServerNode;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.domain.User;
import pl.edu.agh.student.hyperhypervisors.web.neo4j.repositories.UserRepository;
import pl.edu.agh.student.hyperhypervisors.web.session.UserSessionDetails;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserSessionDetails loadUserByUsername(String login)
            throws UsernameNotFoundException, DataAccessException {

        User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + login);
        }
        return new UserSessionDetails(user);
    }

    public User findByLogin(String userName) {
        return userRepository.findByLogin(userName);
    }

    public void addServer(User user, ServerNode server) {
        user.getServers().add(server);
        userRepository.save(user);
    }
}
