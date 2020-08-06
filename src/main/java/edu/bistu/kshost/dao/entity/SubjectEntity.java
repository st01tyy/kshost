package edu.bistu.kshost.dao.entity;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity(name = "subject")
@Data
public class SubjectEntity
{
    @Id
    private Long id;

    @Column(nullable = false)
    @ColumnDefault("untitled")   //中文会报错
    private String name;

    @OneToMany
    private List<SubjectScoreEntity> subjectScoreEntities;

    @OneToMany
    private List<DescriptionEntity> descriptionEntities;
}
