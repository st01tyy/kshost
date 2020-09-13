package edu.bistu.kshost.dao.repository;

import edu.bistu.kshost.dao.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, Long>
{
    List<SubjectEntity> findAll();
    Optional<SubjectEntity> findById(Long id);
}
