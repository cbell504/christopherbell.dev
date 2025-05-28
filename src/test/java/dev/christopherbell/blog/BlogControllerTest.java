package dev.christopherbell.blog;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithMockUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BlogController.class)
public class BlogControllerTest {

  @MockitoBean
  private BlogService blogService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @WithMockUser
  public void testGetBlogPostById_success() throws Exception {

    when(blogService.getPostById(any())).thenReturn(BlogStub.getBlogResponseStub());

    mockMvc.perform(get("/api/blog/v1/posts/1"))
        .andExpect(status().isOk());
  }

}
