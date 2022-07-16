package antifraud.Models.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Card {
    @Column
    String number;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean isStolen;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int minLimit = 200;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int maxLimit = 1500;


    public Card(){}
}
