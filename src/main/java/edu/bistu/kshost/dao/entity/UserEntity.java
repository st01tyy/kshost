package edu.bistu.kshost.dao.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity(name = "user")
@Data
public class UserEntity
{
    @Id
    private Long uid;   //学号

    @Column(nullable = false)
    private String pw;  //密码

    @Column(nullable = false)
    private String name;    //姓名

    @OneToMany
    private List<SubjectScoreEntity> subjectScoreEntities;
}
