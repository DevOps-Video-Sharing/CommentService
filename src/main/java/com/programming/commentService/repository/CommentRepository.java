package com.programming.commentService.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.programming.commentService.model.Comment;
import java.util.List;
@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List <Comment> findAllByVideoId(String videoId);
}