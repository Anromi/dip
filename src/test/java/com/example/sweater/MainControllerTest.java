package com.example.sweater;

import com.example.sweater.controller.MainController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@WithUserDetails(value = "dru")
public class MainControllerTest {
    @Autowired
    private MainController controller;

    @Autowired
    private MockMvc mock;

    @Test
    public void mainTest() throws Exception {
        this.mock.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='navbarSupportedContent']/div").string("dru"));
    }

    @Test
    public void messListTest() throws Exception {
        this.mock.perform(get("/main"))
                .andDo(print()).andExpect(authenticated())
                .andExpect(xpath("//*[@id='message-list']/div").nodeCount(3));
    }

    @Test
    public void filMessTest() throws Exception {
        this.mock.perform(get("/main").param("filter", "tag"))
                .andDo(print()).andExpect(authenticated())
                .andExpect(xpath("//*[@id='message-list']/div").nodeCount(2));
    }

    @Test
    public void addMessageTest() throws Exception {
        MockHttpServletRequestBuilder multipart = multipart("/main")
                .file("file", "3210".getBytes()).with(csrf());
        this.mock.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='message-list']/div").nodeCount(4));
    }
}
