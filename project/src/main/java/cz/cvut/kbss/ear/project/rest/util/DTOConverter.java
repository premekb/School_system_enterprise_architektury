package cz.cvut.kbss.ear.project.rest.util;

import cz.cvut.kbss.ear.project.model.Parallel;
import cz.cvut.kbss.ear.project.rest.dto.ParallelDTO;

import java.util.Objects;

public class DTOConverter {
    public static Parallel dtoToParallel(ParallelDTO parallelDTO){
        Objects.requireNonNull(parallelDTO.getName());
        Objects.requireNonNull(parallelDTO.getClassroom());
        Objects.requireNonNull(parallelDTO.getCapacity());
        Objects.requireNonNull(parallelDTO.getParallelType());
        Objects.requireNonNull(parallelDTO.getStartTime());
        Objects.requireNonNull(parallelDTO.getDayOfWeek());
        Objects.requireNonNull(parallelDTO.getEndTime());

        Parallel parallel = new Parallel();
        parallel.setName(parallelDTO.getName());
        parallel.setClassroom(parallelDTO.getClassroom());
        parallel.setCapacity(parallelDTO.getCapacity());
        parallel.setStartTime(parallelDTO.getStartTime());
        parallel.setDayOfWeek(parallelDTO.getDayOfWeek());
        parallel.setEndTime(parallelDTO.getEndTime());
        parallel.setParallelType(parallelDTO.getParallelType());

        return parallel;
    }
}
