package edu.java.dao;

import edu.java.dao.model.Chat;
import org.springframework.data.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends CrudRepository<Chat, Long> {
}
