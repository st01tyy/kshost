package edu.bistu.kshost.dao.entity;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.List;

@Entity(name = "description")
@Data
public class DescriptionEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @ColumnDefault("10")
    private Integer timeLimit;

    @OneToMany
    private List<SelectionEntity> selectionEntities;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SubjectEntity subject;
}
