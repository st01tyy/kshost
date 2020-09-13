package edu.bistu.kshost.dao.repository;

import edu.bistu.kshost.dao.entity.QuestionEntity;
import edu.bistu.kshost.dao.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long>
{
    List<QuestionEntity> findQuestionEntitiesBySubjectEntity(SubjectEntity subjectEntity);
}
