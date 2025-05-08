package org.example.listener;

import org.example.entity.Revision;
import org.hibernate.envers.RevisionListener;

import java.util.concurrent.ThreadLocalRandom;

public class UserRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        if (revisionEntity instanceof Revision revision) {
            revision.setUser("Test user " + ThreadLocalRandom.current().nextLong());
        }
    }
}
