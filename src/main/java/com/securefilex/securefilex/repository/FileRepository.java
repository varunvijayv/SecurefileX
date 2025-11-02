package com.securefilex.securefilex.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.securefilex.securefilex.model.FileRecord;

public interface FileRepository extends CrudRepository<FileRecord, Long> {
    List<FileRecord> findByOwner(String owner);
}
