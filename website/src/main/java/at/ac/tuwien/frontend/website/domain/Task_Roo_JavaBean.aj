// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package at.ac.tuwien.frontend.website.domain;

import at.ac.tuwien.frontend.website.domain.Task;

privileged aspect Task_Roo_JavaBean {
    
    public String Task.getTaskName() {
        return this.taskName;
    }
    
    public void Task.setTaskName(String taskName) {
        this.taskName = taskName;
    }
    
    public String Task.getDescription() {
        return this.description;
    }
    
    public void Task.setDescription(String description) {
        this.description = description;
    }
    
    public String Task.getKeyword() {
        return this.keyword;
    }
    
    public void Task.setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public Double Task.getSentiment() {
        return this.sentiment;
    }
    
    public void Task.setSentiment(Double sentiment) {
        this.sentiment = sentiment;
    }
    
}