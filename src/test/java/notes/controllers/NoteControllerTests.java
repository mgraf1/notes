package notes.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import notes.Application;
import notes.models.Note;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class NoteControllerTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;
    private ObjectMapper mapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        mapper = new ObjectMapper();
    }

    @Test
    public void shouldBeAbleToCreateAndGetNote() throws Exception {
        String bodyText = "A test body";
        Note note = new Note(bodyText);
        String jsonBody = mapper.writeValueAsString(note);

        MvcResult result = mvc.perform(post("/api/notes")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        Note responseNote = mapper.readValue(response.getContentAsString(), Note.class);

        mvc.perform(get("/api/notes/" + responseNote.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void create_whenContentTypeIsNotSet_shouldReturnUnsupportedMediaType() throws Exception {
        String bodyText = "A test body";
        Note note = new Note(bodyText);
        String jsonBody = mapper.writeValueAsString(note);

        mvc.perform(post("/api/notes")
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void get_whenResourceIsNotThere_shouldReturnNotFound() throws Exception {

        mvc.perform(get("/api/notes/3")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
