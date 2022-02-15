package cz.cvut.kbss.ear.project.rest.dto;

public class EnrolParallelFormDTO {
    private Integer parallel;

    public EnrolParallelFormDTO() {
    }

    public EnrolParallelFormDTO(Integer parallel) {
        this.parallel = parallel;
    }

    public Integer getParallel() {
        return parallel;
    }

    public void setParallel(Integer parallel) {
        this.parallel = parallel;
    }
}
