
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.streaming.controller.CommentController;
import com.programming.streaming.model.Comment;
import com.programming.streaming.repository.CommentRepository;
import com.programming.streaming.service.CommentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(CommentController.class)
public class TestCommentController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    public void testGetMethodName() throws Exception {
        mockMvc.perform(get("/comment/"))
                .andExpect(status().isOk());
    }

    // @Test
    // public void testCreateComment() throws Exception {
    //     Comment comment = Comment.builder()
    //             .id("1")
    //             .text("This is a comment")
    //             .author("Author")
    //             .likes(0)
    //             .dislikes(0)
    //             .build();

    //     when(commentRepository.save(comment)).thenReturn(comment);

    //     ObjectMapper objectMapper = new ObjectMapper();
    //     String commentJson = objectMapper.writeValueAsString(comment);

    //     mockMvc.perform(post("/comment/upload")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(commentJson))
    //             .andExpect(status().isCreated());
    // }
}
