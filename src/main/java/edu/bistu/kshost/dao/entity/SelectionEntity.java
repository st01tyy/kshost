package edu.bistu.kshost.dao.entity;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "selection")
@Data
public class SelectionEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String selection;

    @Column(nullable = false)
    private Boolean isAnswer;

    @ManyToOne
    @JoinColumn(nullable = false)
    private DescriptionEntity description;
}
