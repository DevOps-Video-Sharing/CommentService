package com.programming.commentService.service;

import org.springframework.stereotype.Service;

import com.programming.commentService.model.Comment;
import com.programming.commentService.repository.CommentRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    
    public Comment findById(String id) {

        return null;
    }
    
    public void increaseViews(String id) {

    }
    public List<Comment> getCommentsByVideoId(String videoId) {
        return commentRepository.findAllByVideoId(videoId);
    }
}
