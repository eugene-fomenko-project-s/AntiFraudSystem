package antifraud.Service.RepoService;

import antifraud.Models.Entity.IP;
import antifraud.Repository.IPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
@Service
public class IPService {
    @Autowired
    IPRepository ipRepository;
    public IP findByIP(String address) {
        List<IP> list_ip = ipRepository.findAll();
        for(IP ip : list_ip){
            if(Objects.equals(ip.getIp(), address)){
                return ip;
            }
        }
        return null;
    }

    public void Save(IP ip) {
        ipRepository.saveAndFlush(ip);
    }
    public void Delete(String address){
        List<IP> ips = ipRepository.findAll();
        for(IP ip : ips){
            if(Objects.equals(ip.getIp(), address)){
                ipRepository.deleteById(ip.getId());
            }
        }
    }
    public void Delete(long id) {
        ipRepository.deleteById(id);
    }

    public List<IP> getAll() {
        return ipRepository.findAll();
    }
}
