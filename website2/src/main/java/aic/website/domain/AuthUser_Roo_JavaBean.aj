// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package aic.website.domain;

import aic.website.domain.AuthRole;
import aic.website.domain.AuthUser;
import aic.website.domain.Task;
import java.util.Set;

privileged aspect AuthUser_Roo_JavaBean {
    
    public String AuthUser.getName() {
        return this.name;
    }
    
    public void AuthUser.setName(String name) {
        this.name = name;
    }
    
    public String AuthUser.getPassword() {
        return this.password;
    }
    
    public void AuthUser.setPassword(String password) {
        this.password = password;
    }
    
    public Boolean AuthUser.getEnabled() {
        return this.enabled;
    }
    
    public void AuthUser.setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public Set<AuthRole> AuthUser.getRoles() {
        return this.roles;
    }
    
    public void AuthUser.setRoles(Set<AuthRole> roles) {
        this.roles = roles;
    }
    
    public Set<Task> AuthUser.getTasks() {
        return this.tasks;
    }
    
    public void AuthUser.setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }
    
}
