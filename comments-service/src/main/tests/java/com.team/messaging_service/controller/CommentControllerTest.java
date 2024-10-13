package com.team.messaging_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.messaging_service.dto.CommentDTO;
import com.team.messaging_service.model.Comment;
import com.team.messaging_service.service.CommentService;
import com.team.messaging_service.exceptions.CommentExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)  // Specifies that only CommentController is being tested
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private CommentDTO commentDTO;
    private Comment comment;

    @BeforeEach
    public void setup() {
        commentDTO = new CommentDTO("user123", "This is a comment", "post456");
        comment = new Comment("1", "user123", "This is a comment", "post456", null);
    }

    @Test
    public void testCreateComment_Success() throws Exception {
        // Mock service to return created comment
        when(commentService.createComment(any(CommentDTO.class))).thenReturn(comment);

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.text").value("This is a comment"))
                .andExpect(jsonPath("$.postId").value("post456"));
    }

    @Test
    public void testCreateComment_InvalidData() throws Exception {
        // Simulate an invalid comment data scenario
        doThrow(new CommentExceptions.InvalidCommentDataException("Invalid data")).when(commentService).createComment(any(CommentDTO.class));

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllComments_Success() throws Exception {
        List<Comment> comments = Arrays.asList(comment);
        // Mock service to return list of comments
        when(commentService.getAllComments()).thenReturn(comments);

        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].text").value("This is a comment"))
                .andExpect(jsonPath("$[0].postId").value("post456"));
    }

    @Test
    public void testGetCommentById_Success() throws Exception {
        // Mock service to return a comment by ID
        when(commentService.getCommentById("1")).thenReturn(comment);

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.text").value("This is a comment"))
                .andExpect(jsonPath("$.postId").value("post456"));
    }

    @Test
    public void testGetCommentById_NotFound() throws Exception {
        // Simulate a comment not found scenario
        when(commentService.getCommentById("1")).thenReturn(null);

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isNotFound());
    }
}
