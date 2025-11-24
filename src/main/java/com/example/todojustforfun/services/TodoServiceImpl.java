package com.example.todojustforfun.services;

import com.example.todojustforfun.models.Todo;
import com.example.todojustforfun.repositories.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoServiceImpl implements TodoService{

    private final TodoRepository todoRepository;

    public TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    @Override
    public List<Todo> getAllTodosByTitle(String title) {
        return todoRepository.findByTitleIgnoreCaseContaining(title);
    }

    @Override
    public Optional<Todo> getTodoById(Long id) {
        return todoRepository.findById(id);
    }

    @Override
    public Todo createTodo(Todo todo) {
        if (todoRepository.existsByTitleIgnoreCase(todo.getTitle())) {
            throw new RuntimeException("Todo already created with that title");
        }

        if (todo.getTitle() != null && todo.getDescription() != null) {
            return todoRepository.save(todo);
        }

        return null;
    }

    @Override
    public Todo updateTodo(Long id, Todo todoDetails) {
        Todo todoToUpdate = todoRepository.findById(id).orElseThrow();

        if (todoRepository.existsByTitleIgnoreCase(todoDetails.getTitle())) {
            throw new RuntimeException("Todo already created with that title");
        }

        todoToUpdate.setTitle(todoDetails.getTitle());
        todoToUpdate.setDescription(todoDetails.getDescription());


        if (todoDetails.getTitle() != null && todoDetails.getDescription() != null) {
            return todoRepository.save(todoToUpdate);
        }

        return null;
    }

    @Override
    public void deleteTodoById(Long id) {
        Todo toDeleteTodo = todoRepository.findById(id).orElseThrow();
        todoRepository.delete(toDeleteTodo);
    }
}
