package notes.data;

import notes.models.Note;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface NoteRepository extends CrudRepository<Note, Long> {
    Note findByBody(String body);
}
