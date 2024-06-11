package com.programming.streaming.service;

import org.springframework.stereotype.Service;

import com.programming.streaming.model.Comment;
import com.programming.streaming.repository.CommentRepository;

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
