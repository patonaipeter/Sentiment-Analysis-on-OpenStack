package aic.website.domain;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findAuthUsersByNameEquals" })
public class AuthUser {

    @NotNull
    @Column(unique=true)
    @Size(min = 3, max = 30)
    private String name;

    @Size(max = 64)
    private String password;

    @NotNull
    private Boolean enabled;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<AuthRole> roles = new HashSet<AuthRole>();

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    private Set<Task> tasks = new HashSet<Task>();
}
