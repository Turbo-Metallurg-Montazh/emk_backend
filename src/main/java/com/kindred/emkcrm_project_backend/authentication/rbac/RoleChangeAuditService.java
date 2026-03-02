package com.kindred.emkcrm_project_backend.authentication.rbac;

import com.kindred.emkcrm_project_backend.db.entities.RoleChangeLog;
import com.kindred.emkcrm_project_backend.db.repositories.RoleChangeLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleChangeAuditService {

    private final RoleChangeLogRepository roleChangeLogRepository;

    public RoleChangeAuditService(RoleChangeLogRepository roleChangeLogRepository) {
        this.roleChangeLogRepository = roleChangeLogRepository;
    }

    @Transactional
    public void log(String actorUsername, String targetUsername, String action, String roleCode, String details) {
        RoleChangeLog log = new RoleChangeLog();
        log.setActorUsername(actorUsername == null || actorUsername.isBlank() ? "system" : actorUsername);
        log.setTargetUsername(targetUsername);
        log.setAction(action);
        log.setRoleCode(roleCode);
        log.setDetails(details);
        roleChangeLogRepository.save(log);
    }
}
