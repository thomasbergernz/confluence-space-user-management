package raju.kadam.confluence.permissionmgmt.service.impl;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.persistence.dao.SpaceDao;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.Group;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.DefaultPager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import raju.kadam.confluence.permissionmgmt.config.CustomPermissionConfiguration;
import raju.kadam.confluence.permissionmgmt.service.*;
import raju.kadam.confluence.permissionmgmt.service.vo.ServiceContext;
import raju.kadam.confluence.permissionmgmt.util.GroupNameUtil;
import raju.kadam.confluence.permissionmgmt.util.GroupUtil;
import raju.kadam.util.StringUtil;

import java.util.*;
import java.util.regex.Pattern;

/**
 * (c) 2007 Duke University
 * User: gary.weaver@duke.edu
 * Date: Jun 18, 2007
 * Time: 11:47:08 AM
 */
public class ConfluenceGroupManagementService implements GroupManagementService {

    private Log log = LogFactory.getLog(this.getClass());

    // assuming these are autowired
    private BootstrapManager bootstrapManager;
    private BandanaManager bandanaManager;
    private SpaceDao spaceDao;
    private SpacePermissionManager spacePermissionManager;
    private UserAccessor userAccessor;
    private CustomPermissionConfiguration customPermissionConfiguration;

    public ConfluenceGroupManagementService() {
        log.debug("ConfluenceGroupManagementService start constructor");
        bootstrapManager = (BootstrapManager)ContainerManager.getComponent("bootstrapManager");
        bandanaManager = (BandanaManager)ContainerManager.getComponent("bandanaManager");
        spaceDao = (SpaceDao)ContainerManager.getComponent("spaceDao");
        spacePermissionManager = (SpacePermissionManager)ContainerManager.getComponent("spacePermissionManager");
        userAccessor = (UserAccessor)ContainerManager.getComponent("userAccessor");
        //customPermissionConfiguration = (CustomPermissionConfiguration) ConfluenceUtil.loadComponentWithRetry("customPermissionConfiguration");
        log.debug("ConfluenceGroupManagementService end cosntructor");
    }

    public Pager findGroups( ServiceContext context ) throws FindException {
        log.debug("findGroups() called");
        Map mapWithGroupnamesAsKeys = getGroupsWithViewspacePermissionAsKeysAsMapWithGroupnamesAsKeys(context);
        List groups = getGroupsThatMatchNamePatternExcludingConfluenceAdministrators(mapWithGroupnamesAsKeys, context);
        GroupUtil.sortGroupsByGroupnameAscending(groups);
        Pager pager = new DefaultPager(groups);
        return pager;
    }

    private List getGroupsThatMatchNamePatternExcludingConfluenceAdministrators( Map mapWithGroupnamesAsKeys, ServiceContext context ) {
        log.debug("getGroupsThatMatchNamePatternExcludingConfluenceAdministrators() called");
        List groups = new ArrayList();
        Space space = context.getSpace();

        ArrayList notAllowedUserGroups = new ArrayList();
    	notAllowedUserGroups.add("confluence-administrators");

    	Pattern pat = GroupNameUtil.createGroupMatchingPattern(getCustomPermissionConfiguration(), space.getKey());

        for (Iterator iterator = mapWithGroupnamesAsKeys.keySet().iterator(); iterator.hasNext();)
        {
        	String grpName = (String) iterator.next();
            //If notAllowedUserGroups doesn't contain this group name
        	//and group name matches the pattern, then only add this user-group for display.
    		//log.debug("Selected Groups .....");
            boolean isPatternMatch = GroupNameUtil.doesGroupMatchPattern(grpName, pat);
            if( (!notAllowedUserGroups.contains(grpName)) && isPatternMatch)
        	{
        		log.debug("Group '" + grpName + "' allowed and matched pattern " + pat.pattern() );
        		groups.add(userAccessor.getGroup(grpName));
            }
            else {
                log.debug("Group '" + grpName + "' not allowed or didn't match pattern. notAllowedUserGroups=" + StringUtil.convertCollectionToCommaDelimitedString(notAllowedUserGroups) + " isPatternMatch=" + isPatternMatch + " pattern=" + pat.pattern());
            }
            //log.debug("-------End of Groups---------");

        }
        return groups;
    }

    private Map getGroupsWithViewspacePermissionAsKeysAsMapWithGroupnamesAsKeys(ServiceContext context) {
        log.debug("getGroupsWithViewspacePermissionAsKeysAsMapWithGroupnamesAsKeys() called");
        Space space = context.getSpace();
        //VIEWSPACE_PERMISSION is basic permission that every user group can have.
        Map map = spacePermissionManager.getGroupsForPermissionType(SpacePermission.VIEWSPACE_PERMISSION, space);
        if ( map==null || map.size()==0 ) {
            log.debug("No groups with permissiontype SpacePermission.VIEWSPACE_PERMISSION");
        }
        else {
            log.debug("Got the following groups with permissiontype SpacePermission.VIEWSPACE_PERMISSION: " + StringUtil.convertCollectionToCommaDelimitedString(map.keySet()));
        }
        return map;
    }



    public void addGroups( List groupNames, ServiceContext context ) throws AddException {
        log.debug("addGroups() called. groupName='" + StringUtil.convertCollectionToCommaDelimitedString(groupNames) + "'");
        Space space = context.getSpace();

        for (int i=0; i<groupNames.size(); i++) {
            String groupName = (String)groupNames.get(i);
            if (userAccessor.getGroup(groupName) == null) {

                Group vGroup = userAccessor.addGroup(groupName);
                log.debug("created " + groupName);

                //If group exists then set all required permissions
                if (vGroup != null)
                {
                    SpacePermission perm = new SpacePermission(SpacePermission.VIEWSPACE_PERMISSION, space, vGroup.getName());
                    space.addPermission(perm);
                    log.debug("added viewspace perm to " + groupName);
                }
            }
            else {
                log.debug("group was already there, so didn't do anything.");
            }
        }
    }

    public void removeGroups( List groupNames, ServiceContext context ) throws RemoveException
    {
        log.debug("removeGroupsByGroupnames() called.");
        RemoveException ex = null;

        //Remove Selected Groups
        for(Iterator iterator = groupNames.iterator(); iterator.hasNext();)
        {
            String grpName = (String)iterator.next();
            Pattern pat = GroupNameUtil.createGroupMatchingPattern(getCustomPermissionConfiguration(), context.getSpace().getKey());
            boolean isPatternMatch = GroupNameUtil.doesGroupMatchPattern(grpName, pat);

            // Space admin should not be able to delete any groups whose names begin with "confluence"
            if (!grpName.startsWith("confluence") && isPatternMatch) {
                Group group = userAccessor.getGroup(grpName);
                if (group!=null) {
                    userAccessor.removeGroup(group);
                }
            }
            else {
                log.debug("Not deleting group '" + grpName + "', as either it started with 'confluence' or didn't match pattern " + pat.pattern());

                if (ex==null) {
                    ex = new RemoveException(ErrorReason.INVALID_GROUP_NAME);
                }

                ex.addId(grpName);
            }
        }

        // if we failed, throw exception
        if (ex!=null) {
            throw ex;
        }
    }

    public BandanaManager getBandanaManager() {
        return bandanaManager;
    }

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    public BootstrapManager getBootstrapManager() {
        return bootstrapManager;
    }

    public void setBootstrapManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    public CustomPermissionConfiguration getCustomPermissionConfiguration() {
        return customPermissionConfiguration;
    }

    public void setCustomPermissionConfiguration(CustomPermissionConfiguration customPermissionConfiguration) {
        this.customPermissionConfiguration = customPermissionConfiguration;
    }

    public SpacePermissionManager getSpacePermissionManager() {
        return spacePermissionManager;
    }

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    public SpaceDao getSpaceDao() {
        return spaceDao;
    }

    public void setSpaceDao(SpaceDao spaceDao) {
        this.spaceDao = spaceDao;
    }
    
    public UserAccessor getUserAccessor() {
        return userAccessor;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }
}
