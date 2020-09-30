package edu.bistu.kshost.service;

import edu.bistu.kshost.dao.entity.QuestionEntity;
import edu.bistu.kshost.dao.entity.SelectionEntity;
import edu.bistu.kshost.dao.entity.SubjectEntity;
import edu.bistu.kshost.dao.repository.QuestionRepository;
import edu.bistu.kshost.dao.repository.SelectionRepository;
import edu.bistu.kshost.dao.repository.SubjectRepository;
import edu.bistu.kshost.model.Question;
import edu.bistu.kshost.model.Selection;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService
{
    private SubjectRepository subjectRepository;
    private QuestionRepository questionRepository;
    private SelectionRepository selectionRepository;

    @Autowired
    public QuestionService(SubjectRepository subjectRepository, QuestionRepository questionRepository, SelectionRepository selectionRepository)
    {
        this.subjectRepository = subjectRepository;
        this.questionRepository = questionRepository;
        this.selectionRepository = selectionRepository;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Boolean createNewQuestion(Question question)
    {
        try
        {
            SubjectEntity subjectEntity = subjectRepository.findById(question.getSubjectID()).get();
            QuestionEntity questionEntity = new QuestionEntity();
            List<SelectionEntity> selectionEntities = new ArrayList<>();
            BeanUtils.copyProperties(question, questionEntity);
            questionEntity.setSubjectEntity(subjectEntity);
            questionEntity.setSelectionEntityList(selectionEntities);
            questionRepository.saveAndFlush(questionEntity);

            Selection[] selections = question.getSelections();
            if(selections != null)
            {
                for(int i = 0; i < selections.length; i++)
                {
                    SelectionEntity selectionEntity = new SelectionEntity();
                    BeanUtils.copyProperties(selections[i], selectionEntity);
                    selectionEntity.setQuestionEntity(questionEntity);
                    selectionRepository.save(selectionEntity);
                    selectionEntities.add(selectionEntity);
                }
                questionEntity.setSelectionEntityList(selectionEntities);
                questionRepository.save(questionEntity);
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public Question[] getQuestionsBySubjectID(Long subjectID)
    {
        try
        {
            SubjectEntity subjectEntity = subjectRepository.findById(subjectID).get();
            List<QuestionEntity> questionEntityList = questionRepository.findQuestionEntitiesBySubjectEntity(subjectEntity);
            Question[] questions = new Question[questionEntityList.size()];
            for(int i = 0; i < questions.length; i++)
            {
                questions[i] = toQuestion(questionEntityList.get(i));
            }
            return questions;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

//    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
//    public Boolean deleteAllQuestionsBySubject(Long subjectID)
//    {
//        try
//        {
//            SubjectEntity subjectEntity = subjectRepository.findById(subjectID).get();
//            List<QuestionEntity> questionEntityList = questionRepository.findQuestionEntitiesBySubjectEntity(subjectEntity);
//            if(questionEntityList == null)
//                return false;
//            for(QuestionEntity questionEntity : questionEntityList)
//            {
//                List<SelectionEntity> selectionEntityList = questionEntity.getSelectionEntityList();
//                if(selectionEntityList != null)
//                {
//                    for(SelectionEntity selectionEntity : selectionEntityList)
//                    {
//                        selectionRepository.delete(selectionEntity);
//                    }
//                }
//                questionRepository.delete(questionEntity);
//            }
//            return true;
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            return false;
//        }
//    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Boolean deleteAllQuestionsBySubject(Long subjectID)
    {
        try
        {
            SubjectEntity subjectEntity = subjectRepository.findById(subjectID).get();
            List<QuestionEntity> questionEntityList = questionRepository.findQuestionEntitiesBySubjectEntity(subjectEntity);
            if(questionEntityList == null)
                return false;
            for(QuestionEntity questionEntity : questionEntityList)
            {
                List<SelectionEntity> selectionEntityList = questionEntity.getSelectionEntityList();
                if(selectionEntityList != null)
                {
                    for(SelectionEntity selectionEntity : selectionEntityList)
                    {
                        selectionEntity.setDescription(fix(selectionEntity.getDescription()));
                        selectionRepository.save(selectionEntity);
                    }
                }
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean editQuestion(Question question)
    {
        return false;
    }

    private Selection toSelection(SelectionEntity selectionEntity)
    {
        Selection selection = new Selection();
        BeanUtils.copyProperties(selectionEntity, selection);
        selection.setQuestionID(selectionEntity.getQuestionEntity().getId());
        return selection;
    }

    private Question toQuestion(QuestionEntity questionEntity)
    {
        Question question = new Question();
        BeanUtils.copyProperties(questionEntity,question);
        question.setSubjectID(questionEntity.getSubjectEntity().getId());
        Selection[] selections = new Selection[questionEntity.getSelectionEntityList().size()];
        int i = 0;
        for(SelectionEntity selectionEntity : questionEntity.getSelectionEntityList())
        {
            selections[i] = toSelection(selectionEntity);
            i++;
        }
        question.setSelections(selections);
        return question;
    }

    private static String fix(String str)
    {
        try
        {
            int i, j;
            for(i = 0; i < str.length(); i++)
            {
                if(str.charAt(i) != ' ' && str.charAt(i) != '\t')
                    break;
            }
            for(j = str.length() - 1; j >= 0; j--)
            {
                if(str.charAt(j) != ' ' && str.charAt(j) != '\t')
                    break;
            }
            j++;
            System.out.println("i = " + i + ", j = " + j);
            return str.substring(i, j);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return str;
        }
    }
}
