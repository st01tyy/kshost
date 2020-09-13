package edu.bistu.kshost.dao.repository;

import edu.bistu.kshost.dao.entity.QuestionEntity;
import edu.bistu.kshost.dao.entity.SelectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelectionRepository extends JpaRepository<SelectionEntity, Long>
{
    List<SelectionEntity> findSelectionEntitiesByQuestionEntity(QuestionEntity questionEntity);
}
