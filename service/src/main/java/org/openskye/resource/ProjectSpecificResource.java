package org.openskye.resource;

import com.google.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.openskye.domain.Identifiable;

/**
 * User: atcmostafavi
 * Date: 12/10/13
 * Time: 12:17 PM
 * Project: platform
 */
public abstract class ProjectSpecificResource<T extends Identifiable> extends AbstractUpdatableDomainResource<T> {

    @Inject
    protected String projectID="*";

    @Override
    protected void authorize(String action){
        if(!SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":" + action + ":" + projectID)){
            throw new UnauthorizedException();
        }
    }

    protected boolean isPermitted(String action, String projectID){
        return SecurityUtils.getSubject().isPermitted(getPermissionDomain() + ":" + action + ":" + projectID);
    }
}
