package aic.website.domain;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findTasksByOwner" })
public class Task {

    @NotNull
    @ManyToOne
    private AuthUser owner;

    @NotNull
    private String name;

    private String description;

    @NotNull
    @Size(min = 3)
    private String searchTerm;

    private double analysisResult;
}
