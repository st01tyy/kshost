package edu.bistu.kshost.dao.entity;

import javax.persistence.*;
import java.util.List;

@Entity(name = "question")
public class QuestionEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer timeLimit;  //单位：秒

    @ManyToOne
    @JoinColumn(nullable = false)
    private SubjectEntity subjectEntity;

    @OneToMany
    @JoinColumn
    private List<SelectionEntity> selectionEntityList;

    public QuestionEntity()
    {
        id = null;
        description = "未填写";
        timeLimit = 10;
        subjectEntity = null;
        selectionEntityList = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public SubjectEntity getSubjectEntity() {
        return subjectEntity;
    }

    public void setSubjectEntity(SubjectEntity subjectEntity) {
        this.subjectEntity = subjectEntity;
    }

    public List<SelectionEntity> getSelectionEntityList() {
        return selectionEntityList;
    }

    public void setSelectionEntityList(List<SelectionEntity> selectionEntityList) {
        this.selectionEntityList = selectionEntityList;
    }
}
