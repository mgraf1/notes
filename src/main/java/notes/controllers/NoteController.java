package notes.controllers;

import notes.models.Note;
import notes.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(value="/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @RequestMapping(value = "",
        method = RequestMethod.POST,
        consumes = "application/json",
        produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Note createNote(@RequestBody Note note) {
        return noteService.createNote(note);
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.GET,
            produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Note getNote(@PathVariable long id) {
        Note retrievedNote = noteService.getNote(id);
        if (retrievedNote == null) {
            throw new ResourceNotFoundException("Note " + id + " not found");
        }
        return retrievedNote;
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.DELETE,
            produces = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNote(@PathVariable long id) {
        noteService.deleteNote(id);
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.PUT,
            consumes = "application/json",
            produces = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateNote(@PathVariable long id, @RequestBody Note note) {
        if (id != note.getId()) {
            throw new IllegalArgumentException("Note " + id + " does not match body with id " + note.getId());
        }
        this.getNote(id);
        noteService.updateNote(note);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    void handleBadRequests(HttpServletResponse response, IllegalArgumentException exception) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
