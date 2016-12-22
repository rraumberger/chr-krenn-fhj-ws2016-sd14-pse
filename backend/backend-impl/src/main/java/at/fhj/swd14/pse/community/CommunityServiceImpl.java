package at.fhj.swd14.pse.community;

import at.fhj.swd14.pse.user.UserRepository;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class CommunityServiceImpl implements CommunityService {

    @EJB
    private CommunityRepository communityRepository;

    @EJB
    private UserRepository userRepository;

    @Override
    public long save(CommunityDto community) {
        Community dtoComm = CommunityConverter.convert(community);
        communityRepository.update(dtoComm);
        Community expected = communityRepository.find(dtoComm.getId());
        if (expected != null) {
            return expected.getId();
        }
        return 0;
    }

    @Override
    public long remove(CommunityDto community) {
        communityRepository.remove(CommunityConverter.convert(community));
        Community expected = communityRepository.find(community.getId());
        if (expected != null) {
            return expected.getId();
        }
        return 0;
    }

    @Override
    public CommunityDto find(long id) {
        return CommunityConverter.convert(communityRepository.find(id));
    }

    @Override
    public List<CommunityDto> findByAuthorId(Long creatorUserId) {


        Map<String, Object> parameter = new HashMap<>();
        parameter.put("authorUserId", creatorUserId);
        return executeNamedQuery("Community.findByAuthorId", parameter);
    }

    @Override
    public List<CommunityDto> findUserRelated(Long userId) {
        final List<CommunityDto> communities = executeNamedQuery("Community.findUserRelated", new HashMap<>());

        return communities.stream()
                .filter(dto -> dto.getAllowedUsers().stream().anyMatch(allowedUser -> Objects.equals(allowedUser.getId(), userId)))
                .collect(Collectors.toList());
    }

    private List<CommunityDto> executeNamedQuery(String name, Map<String, Object> parameter) {
        return new ArrayList<>(CommunityConverter.convertToDtoList(communityRepository.executeNamedQuery(name, parameter)));
    }

    @Override
    public List<CommunityDto> findAll() {

        return new ArrayList<>(CommunityConverter.convertToDtoList(communityRepository.findAll()));
    }


}
