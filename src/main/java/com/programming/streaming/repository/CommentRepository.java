package com.programming.streaming.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.programming.streaming.model.Comment;
@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

}