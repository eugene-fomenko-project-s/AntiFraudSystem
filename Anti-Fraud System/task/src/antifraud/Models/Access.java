package antifraud.Models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Access {
    String username;
    String operation;
}
