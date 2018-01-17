package notes.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    public void shouldBeAbleToCreateUpdateGetAndDeleteNote() throws Exception {
        String bodyText = "A test body";
        Note note = new Note(bodyText);
        String jsonBody = mapper.writeValueAsString(note);

        // Create Note.
        MvcResult result = mvc.perform(post("/api/notes")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        Note responseNote = mapper.readValue(response.getContentAsString(), Note.class);

        // Update Note.
        String updatedText = "updated body";
        Note noteUpdate = new Note(responseNote.getId(), updatedText);
        String updateJsonBody = mapper.writeValueAsString(noteUpdate);

        mvc.perform(put("/api/notes/" + responseNote.getId())
                .content(updateJsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Get Note.
        mvc.perform(get("/api/notes/" + responseNote.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body")
                .value(equalTo(noteUpdate.getBody())));

        // Delete Note.
        mvc.perform(delete("/api/notes/" + responseNote.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Note is no longer there.
        mvc.perform(get("/api/notes/" + responseNote.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
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

    @Test
    public void delete_whenNoResourceExists_shouldReturnNotFound() throws Exception {
        mvc.perform(delete("/api/notes/3")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void update_whenNoResourceExists_shouldReturnNotFound() throws Exception {
        String bodyText = "A test body";
        Note note = new Note(111, bodyText);
        String jsonBody = mapper.writeValueAsString(note);

        mvc.perform(put("/api/notes/111")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void update_whenIdsDontMatch_shouldReturnBadRequest() throws Exception {
        String bodyText = "A test body";
        Note note = new Note(2, bodyText);
        String jsonBody = mapper.writeValueAsString(note);

        mvc.perform(put("/api/notes/3")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenContentTypeIsNotSet_shouldReturnUnsupportedMediaType() throws Exception {
        long id = createNote("some note body");

        Note note = new Note(id, "updated note");
        String jsonBody = mapper.writeValueAsString(note);

        mvc.perform(put("/api/notes/" + id)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void getAll_shouldReturnAllNotes() throws Exception {
        long id1 = createNote("Some note");
        long id2 = createNote("Some other note");

        MvcResult result = mvc.perform(get("/api/notes")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        List<Note> responseNotes = mapper.readValue(response.getContentAsString(),
                new TypeReference<List<Note>>(){});

        assertThat(responseNotes, hasItem(hasProperty("id", is(id1))));
        assertThat(responseNotes, hasItem(hasProperty("id", is(id2))));
    }

    @Test
    public void getAll_withFilter_shouldReturnAllMatchingNotes() throws Exception {
        long id1 = createNote("Some note");
        long id2 = createNote("Some other note");

        MvcResult result = mvc.perform(get("/api/notes?query=other")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        List<Note> responseNotes = mapper.readValue(response.getContentAsString(),
                new TypeReference<List<Note>>(){});

        assertThat(responseNotes, hasItem(hasProperty("id", not(is(id1)))));
        assertThat(responseNotes, hasItem(hasProperty("id", is(id2))));
    }

    @Test
    public void getAll_whenNoNotesMatchFilter_shouldReturnEmptyList() throws Exception {
        createNote("Some note");
        createNote("Some other note");

        MvcResult result = mvc.perform(get("/api/notes?query=milk")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        List<Note> responseNotes = mapper.readValue(response.getContentAsString(),
                new TypeReference<List<Note>>(){});

        assertEquals(true, responseNotes.isEmpty());
    }

    private long createNote(String noteBody) throws Exception {
        Note note = new Note(noteBody);
        String jsonBody = mapper.writeValueAsString(note);

        MvcResult result = mvc.perform(post("/api/notes")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        Note responseNote = mapper.readValue(response.getContentAsString(), Note.class);
        return responseNote.getId();
    }
}
