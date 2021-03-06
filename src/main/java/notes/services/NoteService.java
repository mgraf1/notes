package notes.services;

import notes.data.NoteRepository;
import notes.models.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    public Note getNote(long id) {
        return noteRepository.findOne(id);
    }

    public Note createNote(Note note) {
        return noteRepository.save(note);
    }

    public void deleteNote(long id) {
        try {
            noteRepository.delete(id);
        } catch (EmptyResultDataAccessException exception) {
            throw new ResourceNotFoundException("Note " + id + " not found");
        }
    }

    public void updateNote(Note note) {
        noteRepository.save(note);
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    public List<Note> getAllNotesByBody(String body) {
        return noteRepository.findByBodyContaining(body);
    }
}
