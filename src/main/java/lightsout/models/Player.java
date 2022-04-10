package lightsout.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Data
public class Player {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Size(min = 3, max = 12)
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private int age;
}
