package edu.bistu.kshost.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Selection
{
    private Long id;
    private String description;

    @JsonProperty
    private Boolean isAnswer;


    private Long questionID;

    public Selection() {
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

    public Long getQuestionID() {
        return questionID;
    }

    public void setQuestionID(Long questionID) {
        this.questionID = questionID;
    }
}
