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
package io.gravitee.rest.api.model;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class NewExternalUserEntity {

    /**
     * OWASP HTML sanitizer to prevent XSS attacks.
     */
    private static final PolicyFactory HTML_SANITIZER = new HtmlPolicyBuilder().toFactory();

    /**
     * The user first name
     */
    private String firstname;

    /**
     * The user last name
     */
    private String lastname;

    /**
     * The user email
     */
    @NotNull
    private String email;

    /**
     * The user source
     */
    private String source;

    /**
     * The user picture
     */
    private String picture;

    /**
     * The user source reference
     */
    private String sourceId;

    private Boolean newsletter;

    private Map<String, Object> customFields;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname != null ? HTML_SANITIZER.sanitize(firstname) : null;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname != null ? HTML_SANITIZER.sanitize(lastname) : null;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getNewsletter() {
        return newsletter;
    }

    public void setNewsletter(Boolean newsletter) {
        this.newsletter = newsletter;
    }

    public Map<String, Object> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
    }

    @Override
    public String toString() {
        return (
            "NewExternalUserEntity{" +
            "firstname='" +
            firstname +
            '\'' +
            ", lastname='" +
            lastname +
            '\'' +
            ", email='" +
            email +
            '\'' +
            ", source='" +
            source +
            '\'' +
            ", picture='" +
            picture +
            '\'' +
            ", sourceId='" +
            sourceId +
            '\'' +
            ", newsletter=" +
            newsletter +
            ", customFields=" +
            customFields +
            '}'
        );
    }
}
