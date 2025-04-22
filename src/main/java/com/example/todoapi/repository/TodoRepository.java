package com.example.todoapi.repository;

import com.example.todoapi.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    // 基本的なCRUDメソッドは自動実装される
}