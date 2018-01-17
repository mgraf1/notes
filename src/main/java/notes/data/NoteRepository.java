package notes.data;

import notes.models.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByBodyContaining(String body);
}
