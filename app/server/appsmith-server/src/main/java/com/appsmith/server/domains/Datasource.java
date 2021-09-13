package com.appsmith.server.domains;

import com.appsmith.external.models.BaseDomain;
import com.appsmith.external.models.DatasourceConfiguration;
import com.appsmith.external.models.DatasourceStructure;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "jsLib")
public class Datasource extends BaseDomain {

    @Transient
    public static final String DEFAULT_NAME_PREFIX = "Untitled Datasource";

    String name;

    String pluginId;

    String organizationId;

    DatasourceConfiguration datasourceConfiguration;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Set<String> invalids;

    /*
     * - To return useful hints to the user.
     * - These messages are generated by the API server based on the other datasource attributes.
     */
    @Transient
    Set<String> messages;

    /*
     * This field is used to determine if the Datasource has been generated by the client or auto-generated by the system.
     * We use this field because when embedded datasources are null, spring-data auditable interfaces throw exceptions
     * while trying set createdAt and updatedAt properties on the null object
     */
    @Transient
    @JsonIgnore
    Boolean isAutoGenerated = false;

    // The structure is ignored in JSON as it is not sent as part of the datasources API. We have a separate endpoint
    // to obtain the structure of the datasource. The value of this field serves as the cache.
    @JsonIgnore
    DatasourceStructure structure;

    // This field will only be used for git related functionality to sync the action object across different instances.
    // Once created no-one has access to update this field
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String gitSyncId;

    /**
     * This method is here so that the JSON version of this class' instances have a `isValid` field, for backwards
     * compatibility. It may be removed, when sure that no API received is relying on this field.
     *
     * @return boolean, indicating whether this datasource is valid or not.
     */
    public boolean getIsValid() {
        return CollectionUtils.isEmpty(invalids);
    }

    /**
     * Intended to function like `.equals`, but only semantically significant fields, except for the ID. Semantically
     * significant just means that if two datasource have same values for these fields, actions against them will behave
     * exactly the same.
     * @return true if equal, false otherwise.
     */
    public boolean softEquals(Datasource other) {
        if (other == null) {
            return false;
        }

        return new EqualsBuilder()
                .append(name, other.name)
                .append(pluginId, other.pluginId)
                .append(isAutoGenerated, other.isAutoGenerated)
                .append(datasourceConfiguration, other.datasourceConfiguration)
                .isEquals();
    }

}
