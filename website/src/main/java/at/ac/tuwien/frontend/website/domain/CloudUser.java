package at.ac.tuwien.frontend.website.domain;

import javax.validation.constraints.Size;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class CloudUser {

    @Size(min = 1, max = 30)
    private String userName;

    @Size(min = 1, max = 30)
    private String password;

    @Size(min = 1, max = 30)
    private String company;

    @Size(min = 1, max = 30)
    private String email;
}
