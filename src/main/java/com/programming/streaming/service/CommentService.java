package com.programming.streaming.service;

import org.springframework.stereotype.Service;

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
