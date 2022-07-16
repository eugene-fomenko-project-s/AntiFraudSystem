package antifraud.Service.RepoService;

import antifraud.Models.Entity.User;
import antifraud.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    public User findUserByUsername(String username) {
        List<User> list_users = userRepository.findAll();
        for(User user : list_users){
            if(Objects.equals(user.getUsername(), username)){
                return user;
            }
        }
        return null;
    }

    public void Save(User user) {
        userRepository.saveAndFlush(user);
    }
    public void Delete(String username){
        List<User> users = userRepository.findAll();
        for(User user : users){
            if(user.getUsername().equals(username)){
                userRepository.deleteById(user.getId());
            }
        }
    }
    public void Delete(long id) {
        userRepository.deleteById(id);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }
}
