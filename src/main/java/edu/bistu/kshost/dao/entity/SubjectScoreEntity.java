package edu.bistu.kshost.dao.entity;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity(name = "subject_score")
@Data
public class SubjectScoreEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    //主键自增

    @Column(nullable = false)
    @ColumnDefault("1200")
    private Integer score;

    @ManyToOne
    @JoinColumn(nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SubjectEntity subject;
}
