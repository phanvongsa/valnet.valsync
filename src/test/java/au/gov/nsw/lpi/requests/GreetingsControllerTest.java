package au.gov.nsw.lpi.requests;

import au.gov.nsw.lpi.controllers.GreetingsController;
import org.junit.jupiter.api.Test;
//import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest(classes = GreetingsController.class)
@AutoConfigureMockMvc
public class GreetingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void greetJava() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/greeting/Java"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Hello Java!"));
    }
}