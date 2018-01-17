package notes.controllers;

import notes.models.Note;
import notes.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
        Note createdNote = noteService.createNote(note);
        return createdNote;
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.GET,
            produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Note getNote(@PathVariable long id) {
        Note retrievedNote = noteService.getNote(id);
        if (retrievedNote == null) {
            throw new ResourceNotFoundException("Note not found");
        }
        return retrievedNote;
    }
}
