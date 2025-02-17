/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.repository.mongodb.management.internal.model;

import java.util.List;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Florent CHAMFROY (florent.chamfroy at graviteesource.com)
 * @author GraviteeSource Team
 */
@Document(collection = "#{@environment.getProperty('management.mongodb.prefix')}organizations")
public class OrganizationMongo {

    @Id
    private String id;

    private String cockpitId;

    private List<String> hrids;

    private String name;

    private String description;

    private List<String> domainRestrictions;

    private String flowMode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getDomainRestrictions() {
        return domainRestrictions;
    }

    public void setDomainRestrictions(List<String> domainRestrictions) {
        this.domainRestrictions = domainRestrictions;
    }

    public List<String> getHrids() {
        return hrids;
    }

    public void setHrids(List<String> hrids) {
        this.hrids = hrids;
    }

    public String getFlowMode() {
        return flowMode;
    }

    public void setFlowMode(String flowMode) {
        this.flowMode = flowMode;
    }

    public String getCockpitId() {
        return cockpitId;
    }

    public void setCockpitId(String cockpitId) {
        this.cockpitId = cockpitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrganizationMongo)) return false;
        OrganizationMongo envMongo = (OrganizationMongo) o;
        return Objects.equals(id, envMongo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return (
            "OrganizationMongo{" +
            "id='" +
            id +
            '\'' +
            ", cockpitId='" +
            cockpitId +
            '\'' +
            ", hrids=" +
            hrids +
            ", name='" +
            name +
            '\'' +
            ", description='" +
            description +
            '\'' +
            ", domainRestrictions=" +
            domainRestrictions +
            ", flowMode='" +
            flowMode +
            '\'' +
            '}'
        );
    }
}
