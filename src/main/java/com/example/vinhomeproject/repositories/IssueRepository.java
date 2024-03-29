package com.example.vinhomeproject.repositories;

import com.example.vinhomeproject.models.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue,Long> {
    Optional<Issue> findIssueById(Long id);
}
