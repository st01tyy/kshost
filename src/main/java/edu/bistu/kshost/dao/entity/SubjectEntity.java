package edu.bistu.kshost.dao.entity;

import javax.persistence.*;

@Entity(name = "subject")
public class SubjectEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "blob")
    private Byte[] icon;

    public SubjectEntity()
    {
        id = null;
        name = "未命名";
        icon = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte[] getIcon() {
        return icon;
    }

    public void setIcon(Byte[] icon) {
        this.icon = icon;
    }
}
