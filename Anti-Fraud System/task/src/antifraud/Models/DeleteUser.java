package antifraud.Models;

import lombok.Data;

@Data
public class DeleteUser {
    private String username;
    private String status = "Deleted successfully!";
    public DeleteUser(String username){
        this.username = username;
    }
}
