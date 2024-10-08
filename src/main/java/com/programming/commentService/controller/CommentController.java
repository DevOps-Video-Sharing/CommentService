package com.programming.commentService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.programming.commentService.model.Comment;
import com.programming.commentService.repository.CommentRepository;
import com.programming.commentService.service.CommentService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/comment")
@AllArgsConstructor
public class CommentController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "new-comments";

    private final CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @GetMapping("/")
    public String getServiceName() {
        return "Comment Service";
    }

    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadComment(@RequestBody Comment comment) {
        try {
            String commentJson = new Gson().toJson(commentRepository.save(comment));
            kafkaTemplate.send(TOPIC, commentJson);


            Comment save = commentRepository.save(comment);


            //Produce message to kafka

            return ResponseEntity.ok(HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getComment(@PathVariable String id) {
        try {
            Comment commentFromDb = commentRepository.findById(id)
                    .orElseThrow(() -> new Exception("Comment not found"));
            return ResponseEntity.ok(commentFromDb);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllComments() {
        try {
            return ResponseEntity.ok(commentRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    
    @GetMapping("/getAllCommentByVideoId/{videoId}")
    public ResponseEntity<?> getAllCommentByVideoId(@PathVariable String videoId) {
        try {
            return ResponseEntity.ok(commentRepository.findAllByVideoId(videoId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateComment(@PathVariable("id") String id, @RequestBody Comment comment) {
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
    public ResponseEntity<?> increaseLikes(@PathVariable("id") String id, @RequestParam int increment) {
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
    public ResponseEntity<?> increaseDislikes(@PathVariable("id") String id, @RequestParam int increment) {
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
    public ResponseEntity<?> decreaseLikes(@PathVariable("id") String id, @RequestParam int decrement) {
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
    public ResponseEntity<?> decreaseDislikes(@PathVariable("id") String id, @RequestParam int decrement) {
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
    public ResponseEntity<?> deleteComment(@PathVariable String id) {
        try {
            commentRepository.deleteById(id);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    
    @GetMapping("/getALLCommentByVideoId/{videoId}")
    public ResponseEntity<?> getCommentByVideoId(@PathVariable String videoId) {
        List<Comment> comments = commentService.getCommentsByVideoId(videoId);
        if (comments.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(comments);
        }
    }


    
    @PostMapping("/relyCommentByCommentId/{commentId}")
    public ResponseEntity<?> relyCommentByCommentId(@PathVariable String commentId, @RequestBody Comment comment) {
        try {
            Comment commentFromDb = commentRepository.findById(commentId)
                    .orElseThrow(() -> new Exception("Comment not found"));
            comment.setVideoId(commentFromDb.getVideoId());
            // comment.setUserId(commentFromDb.getUserId());
            // comment.setUserName(commentFromDb.getUserName());
            // comment.setAuthor(commentFromDb.getAuthor());
            comment.setNumber(commentFromDb.getNumber() + 1);
            Comment save = commentRepository.save(comment);
            return ResponseEntity.ok(HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
