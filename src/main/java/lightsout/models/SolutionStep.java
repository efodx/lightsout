package lightsout.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class SolutionStep {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Solution solution;

    @Column(nullable = false)
    private int stepNum;

    @Column(nullable = false)
    private int step;
}
