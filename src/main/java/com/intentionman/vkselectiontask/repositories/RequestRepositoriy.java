package com.intentionman.vkselectiontask.repositories;

import com.intentionman.vkselectiontask.domain.entities.RequestAudit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepositoriy extends CrudRepository<RequestAudit, Long> {
}
