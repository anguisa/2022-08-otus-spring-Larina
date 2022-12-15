package ru.otus.security;

import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Permission;

public class CustomPermissionGrantingStrategy extends DefaultPermissionGrantingStrategy {
    /**
     * Creates an instance with the logger which will be used to record granting and
     * denial of requested permissions.
     *
     * @param auditLogger
     */
    public CustomPermissionGrantingStrategy(AuditLogger auditLogger) {
        super(auditLogger);
    }

    /**
     * Сравнение не по прямому соответствию маски, а с учётом CumulativePermission
     * @param ace the ACE from the Acl holding the mask.
     * @param p the Permission we are checking against.
     * @return result of check
     */
    @Override
    protected boolean isGranted(AccessControlEntry ace, Permission p) {
        if (ace.isGranting() && p.getMask() != 0) {
            return (ace.getPermission().getMask() & p.getMask()) != 0;
        } else {
            return ace.getPermission().getMask() == p.getMask();
        }
    }
}
