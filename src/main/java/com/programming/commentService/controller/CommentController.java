package com.programming.commentService.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@RestController
@RequestMapping("/comment")
@AllArgsConstructor
public class CommentController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "new-comments";

    private final CommentRepository commentRepository;

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    @GetMapping("/")
    public String getServiceName() {
        MDC.put("type", "commentservice");
        logger.info("Comment Service Start");
        return "Comment Service";
    }

    private final Gson gson = new Gson();

    private final ConcurrentHashMap<String, CompletableFuture<Comment>> futures = new ConcurrentHashMap<>();
    private static final String SANITIZE_TOPIC = "sanitize-comments";
    private static final String RESULT_TOPIC = "sanitized-comments";
    
    @KafkaListener(topics = RESULT_TOPIC, groupId = "comment-group")
    public void listenSanitizedComments(String message) {
        try {
            // Chuyển đổi JSON từ Kafka thành Comment
            Comment sanitizedComment = gson.fromJson(message, Comment.class);

            // Nếu comment không có ID, tạo một ID mới
            if (sanitizedComment.getId() == null) {
                sanitizedComment.setId(UUID.randomUUID().toString());
            }

            // Lấy CompletableFuture từ futures map và hoàn thành nó
            CompletableFuture<Comment> future = futures.get(sanitizedComment.getId());
            if (future != null) {
                future.complete(sanitizedComment);  // Hoàn thành CompletableFuture với comment đã xử lý
            } else {
                // Nếu không tìm thấy CompletableFuture, log thông báo lỗi
                System.err.println("No future found for comment ID: " + sanitizedComment.getId());
            }
        } catch (Exception e) {
            // Ghi lại thông báo lỗi nếu có bất kỳ ngoại lệ nào xảy ra
            e.printStackTrace();
        }
    }

    // Nhận comment từ client và gửi tới Kafka để xử lý
    @PostMapping("/upload")
    public ResponseEntity<?> uploadComment(@RequestBody Comment comment) {
        // Nếu comment không có ID, tạo một ID mới
        if (comment.getId() == null) {
            comment.setId(UUID.randomUUID().toString());
        }

        try {
            // Chuyển comment sang JSON và gửi qua Kafka tới Flask (topic: sanitize-comments)
            String commentJson = gson.toJson(comment);
            kafkaTemplate.send(SANITIZE_TOPIC, commentJson);

            // Khởi tạo CompletableFuture để chờ phản hồi từ Flask qua Kafka (topic: sanitized-comments)
            CompletableFuture<Comment> future = new CompletableFuture<>();
            futures.put(comment.getId(), future);  // Lưu CompletableFuture vào futures map

            // Đợi phản hồi từ Kafka, timeout sau 30 giây
            Comment sanitizedComment = future.get(30, TimeUnit.SECONDS); // Timeout 30 giây

            // Lưu comment đã xử lý vào MongoDB
            Comment savedComment = commentRepository.save(sanitizedComment);

            //ELK
            MDC.put("type", "commentservice");
            MDC.put("action", "upload");
            logger.info("CommentId: " + savedComment.getId());
            logger.info("VideoId: " + comment.getVideoId());
            logger.info("UserId: " + comment.getUserId());
            return ResponseEntity.ok(savedComment);
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
