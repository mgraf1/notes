package notes.services;

import notes.data.NoteRepository;
import notes.models.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
