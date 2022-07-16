package antifraud.Config.Controller;

import antifraud.Models.*;
import antifraud.Models.Entity.User;
import antifraud.Service.RepoService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/auth/")
public class MainController {
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder encoder;

    @PostMapping("user")
    public ResponseEntity<?> auth(@RequestBody User user) {
        if (user.getUsername() != null && user.getPassword() != null && user.getName() != null) {
            if (userService.findUserByUsername(user.getUsername()) == null) {
                user.setPassword(encoder.encode(user.getPassword()));
                boolean isAdmin = !(userService.getAll().size() > 0);
                user.setAccountNonLocked(isAdmin);
                if (isAdmin) {
                    user.setRole("ADMINISTRATOR");
                } else {
                    user.setRole("MERCHANT");
                }
                userService.Save(user);
                return ResponseEntity.status(HttpStatus.CREATED).body(user);
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("list")
    public ResponseEntity<?> list() {
        return ResponseEntity.ok().body(userService.getAll());
    }

    @DeleteMapping("user/{username}")
    public ResponseEntity<?> delete(@PathVariable String username) {
        if (userService.findUserByUsername(username) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        userService.Delete(username);
        return ResponseEntity.ok().body(new DeleteUser(username));
    }

    @PutMapping("role")
    public ResponseEntity<?> set_role(@RequestBody Set_role set_role) {
        User user = userService.findUserByUsername(set_role.getUsername());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (set_role.getRole().equals("MERCHANT") || set_role.getRole().equals("SUPPORT")) {
            if (user.getRole().equals(set_role.getRole())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            } else {
                user.setRole(set_role.getRole());
                userService.Save(user);
                return ResponseEntity.ok().body(user);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("access")
    public ResponseEntity<?> access(@RequestBody Access access) {
        if (userService.findUserByUsername(access.getUsername()) != null) {
            User user = userService.findUserByUsername(access.getUsername());
            if (user.getRole().equals("ADMINISTRATOR")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            } else {
                boolean acc = !access.getOperation().equals("LOCK");
                user.setAccountNonLocked(acc);
                userService.Save(user);
                if (acc) {
                    return ResponseEntity.ok().body(new Status("User " + access.getUsername() + " unlocked!"));
                } else {
                    return ResponseEntity.ok().body(new Status("User " + access.getUsername() + " locked!"));
                }
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

}
