// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package aic.website.domain;

import aic.website.domain.AuthUser;
import aic.website.domain.Task;

privileged aspect Task_Roo_JavaBean {
    
    public AuthUser Task.getOwner() {
        return this.owner;
    }
    
    public void Task.setOwner(AuthUser owner) {
        this.owner = owner;
    }
    
    public String Task.getName() {
        return this.name;
    }
    
    public void Task.setName(String name) {
        this.name = name;
    }
    
    public String Task.getDescription() {
        return this.description;
    }
    
    public void Task.setDescription(String description) {
        this.description = description;
    }
    
    public String Task.getSearchTerm() {
        return this.searchTerm;
    }
    
    public void Task.setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    public double Task.getAnalysisResult() {
        return this.analysisResult;
    }
    
    public void Task.setAnalysisResult(double analysisResult) {
        this.analysisResult = analysisResult;
    }
    
}
