/**
 * 
 */
package at.fhj.swd14.pse.community;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.fhj.swd14.pse.user.UserDto;
import at.fhj.swd14.pse.user.UserService;

@Named
@SessionScoped
/**
 * @author schoeneg14, purkart
 *
 */
public class CommunityBean implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * The LOGGER to use
	 */
	private static final Logger LOGGER = LogManager.getLogger(CommunityBean.class);

	@EJB(name = "ejb/CommunityService")
	private CommunityService communityService;
	
	@EJB(name = "ejb/UserService")
	private UserService userService;

	private String newName;
	private String newDescription;
	private boolean newPublicState;
	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getNewDescription() {
		return newDescription;
	}

	public void setNewDescription(String newDescription) {
		this.newDescription = newDescription;
	}

	public boolean isNewPublicState() {
		return newPublicState;
	}

	public void setNewPublicState(boolean newPublicState) {
		this.newPublicState = newPublicState;
	}

	public List<CommunityDto> getCreatedCommunities() {
		return createdCommunities;
	}

	public void setCreatedCommunities(List<CommunityDto> createdCommunities) {
		this.createdCommunities = createdCommunities;
	}

	public List<CommunityDto> getJoinedCommunities() {
		return joinedCommunities;
	}

	public void setJoinedCommunities(List<CommunityDto> joinedCommunities) {
		this.joinedCommunities = joinedCommunities;
	}

	public List<CommunityDto> getPublicCommunities() {
		return publicCommunities;
	}

	public void setPublicCommunities(List<CommunityDto> publicCommunities) {
		this.publicCommunities = publicCommunities;
	}

	private List<CommunityDto> createdCommunities;
	private List<CommunityDto> joinedCommunities;
	private List<CommunityDto> publicCommunities;
	private List<CommunityDto> allCommunities;
	private List<CommunityDto> otherCommunities;
	private List<CommunityDto> communitiesToActivate;
	
	/*
	private CommunityDto community;
	public CommunityBean() {
    	LOGGER.debug("Create: " + CommunityBean.class.getSimpleName());
    	this.community = new CommunityDto();
    }
    */
	
	public List<CommunityDto> getCommunitiesToActivate() {
		return communitiesToActivate;
	}

	public void setCommunitiesToActivate(List<CommunityDto> communitiesToActivate) {
		this.communitiesToActivate = communitiesToActivate;
	}

	public List<CommunityDto> getOtherCommunities() {
		return otherCommunities;
	}

	public void setOtherCommunities(List<CommunityDto> otherCommunities) {
		this.otherCommunities = otherCommunities;
	}

	private UserDto loggedInUser;

	/**
	 * Initializes the bean for the view
	 */
	@PostConstruct
	public void init() {
		LOGGER.error("Initialising the CommunityBean");
		
		// Get logged in user
		long currentUserId = ((at.fhj.swd14.pse.security.DatabasePrincipal)FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal()).getUserId();;
		
		this.loggedInUser = userService.find(currentUserId);
		
		refresh();
	}
	
	private void refresh() {
		this.createdCommunities = new ArrayList<CommunityDto>();
		this.joinedCommunities = new ArrayList<CommunityDto>();
		this.publicCommunities = new ArrayList<CommunityDto>();
		this.allCommunities = new ArrayList<CommunityDto>();
		this.otherCommunities = new ArrayList<CommunityDto>();
		this.communitiesToActivate = new ArrayList<CommunityDto>();
		
		this.createdCommunities = communityService.findByAuthorId(this.loggedInUser.getId());
		this.joinedCommunities = communityService.findUserRelated(this.loggedInUser.getId());
		this.publicCommunities = communityService.findUserRelated(this.loggedInUser.getId());
		this.allCommunities = communityService.findAll();
		
		ArrayList<CommunityDto> dummy = new ArrayList<CommunityDto>();
		allCommunities.forEach(dto -> {
			if(dto.getAuthor().getId() != this.loggedInUser.getId())
			{
				dummy.add(dto);
			}
			
			if(!dto.getActiveState()) {
				this.communitiesToActivate.add(dto);
			}
		});
		
		dummy.forEach(dto -> {
			if(!dto.getAllowedUsers().contains(this.loggedInUser)) {
				this.otherCommunities.add(dto);
			}
		});
		
		
	}

	/**
	 * Creates a new Community
	 *
	 */
	public void createCommunity() {
		LOGGER.debug("Creating new Community...");
    	
		if(this.newName != null) {
			CommunityDto community = new CommunityDto();
			community.setAuthor(loggedInUser);
			community.setPublicState(newPublicState);
			community.setName(newName);
			
			// If it is a private community, set it to active..
			if(!newPublicState) {
				community.setActiveState(true);
			} else {
				community.setActiveState(false);
			}
			
			community.setDescription(newDescription);
			long generatedId = this.communityService.save(community);
			
			community = communityService.find(generatedId);
			
			LOGGER.error("created new community author: {} desc: {} name: {} public: {}",
					this.loggedInUser, this.newDescription, this.newName, this.newPublicState);
			
			refresh();
			
		} else {
			LOGGER.debug("Name is empty, can't create comunity");
		}
	}
	
	/**
	 * activate a community
	 *
	 */
	public void activate(CommunityDto community) {
		LOGGER.debug("activate Comunity: {}", community.getName());
    	
		if(community != null) {
			community.setActiveState(true);
			
			this.communityService.save(community);
			
			LOGGER.error("activated community {}", community.getName());
			
			refresh();
		}
	}
	
	/**
	 * deactivate a community
	 *
	 */
	public void deactivate(CommunityDto community) {
		LOGGER.debug("deactivate Comunity: {}", community.getName());
    	
		if(community != null) {
			community.setActiveState(false);
			
			this.communityService.save(community);
			
			LOGGER.error("deactivated community {}", community.getName());
			
			refresh();
		}
	}

	/**
	 * add the current user to a community
	 *
	 */
	public void join(CommunityDto community) {
		LOGGER.debug("User {} Joining the Comunity: {}", this.loggedInUser, community.getName());
    	
		if(community != null) {
			if (community.getPublicState()) {
				//CommunityDto communityToJoin = communityService.find(community.getId());
				community.addUser(this.loggedInUser);
				LOGGER.error("adding user {} to community {}",
						this.loggedInUser, community.getName());
			} else {
				community.addPendingUser(this.loggedInUser);
				LOGGER.error("add join request for user {} and community {}",
						this.loggedInUser, community.getName());
			}
			
			this.communityService.save(community);
			
			LOGGER.error("saved user {} to community {}",
					this.loggedInUser, community.getName());
			
			refresh();	
		}
	}
	
	/**
	 * leave a community
	 *
	 */
	public void leave(CommunityDto community) {
		LOGGER.debug("User {} leaves the Comunity: {}", this.loggedInUser, community.getName());
    	
		if(community != null) {
			community.deleteUser(this.loggedInUser.getId());
			
			this.communityService.save(community);
			
			LOGGER.error("removed user {} from community {}",
					this.loggedInUser, community.getName());
			
			refresh();
		}
	}
	
	/**
	 * accept a user request
	 *
	 */
	public void accept(CommunityDto community) {
		LOGGER.debug("accept User {} for Comunity: {}", this.loggedInUser, community.getName());
    	
		if(community != null) {
			community.addUser(this.loggedInUser);
			community.deletePendingUser(this.loggedInUser.getId());
			
			this.communityService.save(community);
			
			LOGGER.error("accepted user {} for community {}",
					this.loggedInUser, community.getName());
			
			refresh();
		}
	}
	
	/**
	 * decline a user request
	 *
	 */
	public void decline(CommunityDto community) {
		LOGGER.debug("decline User {} lfor Comunity: {}", this.loggedInUser, community.getName());
    	
		if(community != null) {
			community.deletePendingUser(this.loggedInUser.getId());
			
			this.communityService.save(community);
			
			LOGGER.error("acceptdesclined user {} for community {}",
					this.loggedInUser, community.getName());
			
			refresh();
		}
	}
}
