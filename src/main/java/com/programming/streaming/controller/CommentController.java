package com.programming.streaming.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.programming.streaming.model.Comment;
import com.programming.streaming.repository.CommentRepository;
import com.programming.streaming.service.CommentService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/comment")
@AllArgsConstructor
public class CommentController {
    private final CommentRepository commentRepository;
    private final CommentService commentService;
    @GetMapping("/")    
    public ResponseEntity<Map<String, String>> getMethodName() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Comment service");
        return ResponseEntity.ok(response);
    }
    

    @PostMapping("/upload")
    public ResponseEntity uploadComment(@RequestBody Comment comment) {
        try {
            Comment save = commentRepository.save(comment);
            return ResponseEntity.ok(HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity getComment(@RequestBody Comment comment) {
        try {
            Comment commentFromDb = commentRepository.findById(comment.getId())
                    .orElseThrow(() -> new Exception("Comment not found"));
            return ResponseEntity.ok(commentFromDb);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity getAllComments() {
        try {
            return ResponseEntity.ok(commentRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity updateComment(@PathVariable("id") String id, @RequestBody Comment comment) {
        try {
            Comment commentFromDb = commentRepository.findById(id)
                .orElseThrow(() -> new Exception("Comment not found"));
            commentFromDb.setText(comment.getText());
            Comment save = commentRepository.save(commentFromDb);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/increaseLikes/{id}")
    public ResponseEntity increaseLikes(@PathVariable("id") String id, @RequestParam int increment) {
        try {
            Comment commentFromDb = commentRepository.findById(id)
                    .orElseThrow(() -> new Exception("Comment not found"));
            commentFromDb.setLikes(commentFromDb.getLikes() + increment);
            Comment save = commentRepository.save(commentFromDb);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/increaseDislikes/{id}")
    public ResponseEntity increaseDislikes(@PathVariable("id") String id, @RequestParam int increment) {
        try {
            Comment commentFromDb = commentRepository.findById(id)
                    .orElseThrow(() -> new Exception("Comment not found"));
            commentFromDb.setDislikes(commentFromDb.getDislikes() + increment);
            Comment save = commentRepository.save(commentFromDb);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/decreaseLikes/{id}")
    public ResponseEntity decreaseLikes(@PathVariable("id") String id, @RequestParam int decrement) {
        try {
            Comment commentFromDb = commentRepository.findById(id)
                    .orElseThrow(() -> new Exception("Comment not found"));
            commentFromDb.setLikes(commentFromDb.getLikes() - decrement);
            Comment save = commentRepository.save(commentFromDb);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/decreaseDislikes/{id}")
    public ResponseEntity decreaseDislikes(@PathVariable("id") String id, @RequestParam int decrement) {
        try {
            Comment commentFromDb = commentRepository.findById(id)
                    .orElseThrow(() -> new Exception("Comment not found"));
            commentFromDb.setDislikes(commentFromDb.getDislikes() - decrement);
            Comment save = commentRepository.save(commentFromDb);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity deleteComment(@RequestBody Comment comment) {
        try {
            commentRepository.deleteById(comment.getId());
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
