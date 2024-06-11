import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.programming.commentService.model.Comment;

@SpringBootTest(classes = com.programming.commentService.StreamingApplication.class)
@AutoConfigureMockMvc
public class TestCommentController {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetServiceName() throws Exception {
        mockMvc.perform(get("/comment/"))
                .andExpect(status().isOk());
    }

    // @Test
    // public void testUploadComment() throws Exception {
    //     Comment comment = Comment.builder()
    //             .text("text")
    //             .author("author")
    //             .build();
    //     String json = "{ \"text\": \"text\", \"author\": \"author\" }";
    //     mockMvc.perform(post("/comment/upload")
    //             .contentType("application/json")
    //             .content(json))
    //             .andExpect(status().isOk());
    // }
}
