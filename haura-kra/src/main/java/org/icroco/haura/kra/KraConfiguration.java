package org.icroco.haura.kra;

import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

public class KraConfiguration extends Configuration
{
    @NotEmpty
    private String defaultName = "KRA";

    @JsonProperty
    public String getDefaultName()
    {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String aName)
    {
        this.defaultName = aName;
    }
}
