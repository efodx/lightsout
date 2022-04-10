package lightsout.models;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Problem {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ElementCollection
    @Column(nullable = false)
    private List<Integer> grid;

    @ManyToOne
    private Player createdBy;
}
