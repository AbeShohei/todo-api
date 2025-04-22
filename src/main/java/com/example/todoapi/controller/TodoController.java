package com.example.todoapi.controller;

import com.example.todoapi.entity.Todo;
import com.example.todoapi.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // @PathVariable などを使うために必要

import java.util.List;
import java.util.Optional; // Optional を使うために必要

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")

// @CrossOrigin // 開発中は一旦すべて許可 (後でReactアプリのURLに限定推奨)
public class TodoController {

    private final TodoRepository todoRepository;

    // GET /api/todos : 全てのToDoを取得 (実装済み)
    @GetMapping
    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    // POST /api/todos : 新しいToDoを作成 (実装済み)
    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Todo newTodo) {
        Todo savedTodo = todoRepository.save(newTodo);
        return new ResponseEntity<>(savedTodo, HttpStatus.CREATED);
    }

    // --- ↓↓↓ ここから追加 ↓↓↓ ---

    // GET /api/todos/{id} : 指定されたIDのToDoを取得
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        // リポジトリでIDを検索
        Optional<Todo> optionalTodo = todoRepository.findById(id);

        // Optionalの中身が存在すればそれを返し(200 OK)、なければ404 Not Foundを返す
        return optionalTodo.map(todo -> new ResponseEntity<>(todo, HttpStatus.OK))
                         .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        /* 上の map/orElse は以下のif文と同じ意味です
        if (optionalTodo.isPresent()) {
            return new ResponseEntity<>(optionalTodo.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        */
    }

    // PUT /api/todos/{id} : 指定されたIDのToDoを更新
    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo updatedTodoDetails) {
        // まずIDで既存のToDoを探す
        Optional<Todo> optionalTodo = todoRepository.findById(id);

        if (optionalTodo.isPresent()) {
            // 既存のToDoが見つかった場合
            Todo existingTodo = optionalTodo.get();

            // リクエストボディの内容で既存ToDoの情報を更新
            existingTodo.setTitle(updatedTodoDetails.getTitle());
            existingTodo.setCompleted(updatedTodoDetails.isCompleted()); // booleanのgetterはisXxx()

            // 更新したToDoを保存
            Todo savedTodo = todoRepository.save(existingTodo);
            return new ResponseEntity<>(savedTodo, HttpStatus.OK);
        } else {
            // 既存のToDoが見つからなければ404 Not Found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE /api/todos/{id} : 指定されたIDのToDoを削除
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        // IDが存在するか確認 (存在しないIDを削除しようとした場合に404を返すため)
        if (todoRepository.existsById(id)) {
            // 存在すれば削除
            todoRepository.deleteById(id);
            // 削除成功時は通常 204 No Content を返す (レスポンスボディはなし)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            // 存在しなければ 404 Not Found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // --- ↑↑↑ ここまで追加 ↑↑↑ ---

}