package com.example.vinhomeproject.service;

import com.example.vinhomeproject.dto.ProblemDTO;
import com.example.vinhomeproject.mapper.ProblemsMapper;
import com.example.vinhomeproject.models.Problems;
import com.example.vinhomeproject.repositories.ProblemsRepository;
import com.example.vinhomeproject.response.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProblemsService {

    @Autowired
    private ProblemsRepository repo;
    @Autowired
    private ProblemsMapper mapper;

    public ResponseEntity<ResponseObject> getListProblems(){
        List<Problems> problems = repo.findAll();
        if(problems.size()>0){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    "Successfully",
                    problems
            ));
        }else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    "List user null",
                    null
            ));
        }
    }
    public ResponseEntity<ResponseObject> getProblemsById(Long id){
        Optional<Problems> problems = repo.findById(id);
        if(problems.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    "Successfully",
                    problems
            ));
        }else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    "List user null",
                    null
            ));
        }
    }
    public ResponseEntity<ResponseObject> createProblems(ProblemDTO problemDTO){
        repo.save(mapper.createProblemsToProblemsDto(problemDTO));
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ResponseObject(
                    "Create problems success",
                    null
            ));
    }
    public ResponseEntity<ResponseObject>  deleteProblems(Long id){
        Optional<Problems> problems = repo.findById(id);
        problems.get().setStatus(false);
        repo.save(problems.get());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                "Delete probelms successfully",
                null
        ));
    }

    public ResponseEntity<ResponseObject>  updateProblems(Long id,ProblemDTO problemDTO){
        Optional<Problems> problems = repo.findById(id);
        mapper.update(problemDTO,problems.get());
        repo.save(problems.get());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                "Delete problems successfully",
                null
        ));
    }

}
