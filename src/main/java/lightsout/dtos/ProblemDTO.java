package lightsout.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProblemDTO {
    private long id;
    private List<List<Integer>> grid;
    private String createdByUsername;
}
