package antifraud.Config.Controller;


import antifraud.Models.Entity.IP;
import antifraud.Models.Status;
import antifraud.Service.RepoService.IPService;
import antifraud.Service.Utils.Support;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/antifraud/")
public class IPController {
    @Autowired
    IPService ipService;
    Support support = new Support();

    @PostMapping("suspicious-ip")
    public ResponseEntity<?> suspicious_ip(@RequestBody IP ip) {
        if (ipService.findByIP(ip.getIp()) == null) {
            if (support.check_valid(ip.getIp())) {
                ipService.Save(ip);
                return ResponseEntity.ok().body(ip);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("suspicious-ip")
    public ResponseEntity<?> suspicious_ip() {
        return ResponseEntity.ok().body(ipService.getAll());
    }

    @DeleteMapping("suspicious-ip/{ip}")
    public ResponseEntity<?> delete_suspicious_ip(@PathVariable String ip) {
        if (support.check_valid(ip)) {
            if (ipService.findByIP(ip) != null) {
                ipService.Delete(ip);
                return ResponseEntity.ok().body(new Status("IP " + ip +" successfully removed!"));
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
