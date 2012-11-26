// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package aic.website.domain;

import aic.website.domain.AuthUser;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect AuthUser_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager AuthUser.entityManager;
    
    public static final EntityManager AuthUser.entityManager() {
        EntityManager em = new AuthUser().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long AuthUser.countAuthUsers() {
        return entityManager().createQuery("SELECT COUNT(o) FROM AuthUser o", Long.class).getSingleResult();
    }
    
    public static List<AuthUser> AuthUser.findAllAuthUsers() {
        return entityManager().createQuery("SELECT o FROM AuthUser o", AuthUser.class).getResultList();
    }
    
    public static AuthUser AuthUser.findAuthUser(Long id) {
        if (id == null) return null;
        return entityManager().find(AuthUser.class, id);
    }
    
    public static List<AuthUser> AuthUser.findAuthUserEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM AuthUser o", AuthUser.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void AuthUser.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void AuthUser.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            AuthUser attached = AuthUser.findAuthUser(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void AuthUser.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void AuthUser.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public AuthUser AuthUser.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        AuthUser merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}