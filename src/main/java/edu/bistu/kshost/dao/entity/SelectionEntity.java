package edu.bistu.kshost.dao.entity;

import javax.persistence.*;

@Entity(name = "selection")
public class SelectionEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean isAnswer;

    @ManyToOne
    @JoinColumn
    private QuestionEntity questionEntity;

    public SelectionEntity()
    {
        id = null;
        description = "未填写";
        isAnswer = false;
        questionEntity = null;
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

    public Boolean getAnswer() {
        return isAnswer;
    }

    public void setAnswer(Boolean answer) {
        isAnswer = answer;
    }

    public QuestionEntity getQuestionEntity() {
        return questionEntity;
    }

    public void setQuestionEntity(QuestionEntity questionEntity) {
        this.questionEntity = questionEntity;
    }
}
