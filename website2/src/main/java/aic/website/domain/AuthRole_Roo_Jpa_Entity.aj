// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package aic.website.domain;

import aic.website.domain.AuthRole;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

privileged aspect AuthRole_Roo_Jpa_Entity {
    
    declare @type: AuthRole: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long AuthRole.id;
    
    @Version
    @Column(name = "version")
    private Integer AuthRole.version;
    
    public Long AuthRole.getId() {
        return this.id;
    }
    
    public void AuthRole.setId(Long id) {
        this.id = id;
    }
    
    public Integer AuthRole.getVersion() {
        return this.version;
    }
    
    public void AuthRole.setVersion(Integer version) {
        this.version = version;
    }
    
}