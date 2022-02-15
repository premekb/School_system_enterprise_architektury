package cz.cvut.kbss.ear.project.dao;

import cz.cvut.kbss.ear.project.model.Parallel;
import org.springframework.stereotype.Repository;

@Repository
public class ParallelDao extends BaseDao<Parallel> {

    public ParallelDao() {
        super(Parallel.class);
    }



}
