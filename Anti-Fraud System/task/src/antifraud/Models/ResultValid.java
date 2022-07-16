package antifraud.Models;

import lombok.Data;

@Data
public class ResultValid {
    String result;
    String info;
    public ResultValid(String result){
        this.result = result;
    }
}
