package edu.bistu.kshost.service;

import edu.bistu.kshost.dao.entity.SubjectEntity;
import edu.bistu.kshost.dao.repository.SubjectRepository;
import edu.bistu.kshost.model.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectService
{
    private final SubjectRepository subjectRepository;

    @Autowired
    public SubjectService(SubjectRepository subjectRepository)
    {
        this.subjectRepository = subjectRepository;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Long createNewSubject(Subject subject)
    {
        try
        {
            SubjectEntity subjectEntity = new SubjectEntity();
            BeanUtils.copyProperties(subject, subjectEntity);
            subjectRepository.save(subjectEntity);
            return subjectEntity.getId();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public Subject[] getAllSubjects()
    {
        List<SubjectEntity> subjectEntityList = subjectRepository.findAll();
        if(subjectEntityList == null)
            return null;
        Subject[] res = new Subject[subjectEntityList.size()];
        for(int i = 0; i < res.length; i++)
        {
            res[i] = new Subject();
            BeanUtils.copyProperties(subjectEntityList.get(i), res[i]);
            res[i].setIcon(null);
        }
        return res;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Boolean deleteSubject(Long subjectID)
    {
        try
        {
            subjectRepository.deleteById(subjectID);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Boolean editSubject(Subject subject)
    {
        try
        {
            SubjectEntity subjectEntity = new SubjectEntity();
            BeanUtils.copyProperties(subject, subjectEntity);
            subjectRepository.save(subjectEntity);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
