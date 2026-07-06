package vn.phankyanh.shopbongda.user.service.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.exception.UserNotFoundException;
import vn.phankyanh.shopbongda.membership.domain.MembershipTier;
import vn.phankyanh.shopbongda.membership.repository.MembershipTierRepository;
import vn.phankyanh.shopbongda.user.domain.Role;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.dto.request.UpdateRoleRequest;
import vn.phankyanh.shopbongda.user.dto.request.UpdateUserStatusRequest;
import vn.phankyanh.shopbongda.user.dto.request.UserCreateRequest;
import vn.phankyanh.shopbongda.user.dto.request.UserUpdateRequest;
import vn.phankyanh.shopbongda.user.dto.response.UserCreateResponse;
import vn.phankyanh.shopbongda.user.mapper.UserMapper;
import vn.phankyanh.shopbongda.user.repository.UserRepository;
import vn.phankyanh.shopbongda.user.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MembershipTierRepository membershipTierRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserCreateResponse createUser(UserCreateRequest request) {

        request.setRole(Role.ROLE_CLIENT);
        request.setStatus("active");

        return createClientUser(request);
    }

    @Override
    @Transactional
    public UserCreateResponse createUser(
            UserCreateRequest request,
            Long actorId
    ) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role role = request.getRole() != null
                ? request.getRole()
                : Role.ROLE_CLIENT;

        assertCanCreateRole(getActor(actorId), role);

        String status = request.getStatus() == null
                || request.getStatus().isBlank()
                ? "active"
                : request.getStatus().trim().toLowerCase();

        return persistUser(request, role, status);
    }

    @Override
    public Page<UserCreateResponse> getAllUsers(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            String search,
            Role role,
            String status,
            Long actorId
    ) {
        User actor = getActor(actorId);
        Role effectiveRole = normalizeRoleForActor(actor, role);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Specification<User> specification =
                buildUserSpecification(search, effectiveRole, status, actor);

        return userRepository.findAll(specification, pageable)
                .map(userMapper::toUserCreateResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserCreateResponse getUserById(Long id, Long actorId) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        assertCanAccessTarget(getActor(actorId), user);

        return toUserResponse(user);
    }

    @Override
    @Transactional
    public UserCreateResponse updateUser(
            Long id,
            UserUpdateRequest request,
            Long actorId
    ) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        assertCanAccessTarget(getActor(actorId), user);

        if (request.getUsername() != null
                && !request.getUsername().isBlank()) {

            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null
                && !request.getEmail().isBlank()) {

            if (!request.getEmail().equals(user.getEmail())) {

                if (userRepository.existsByEmail(request.getEmail())) {
                    throw new RuntimeException("Email đã tồn tại!");
                }
            }

            user.setEmail(request.getEmail());
        }

        if (request.getPhone() != null
                && !request.getPhone().isBlank()) {

            user.setPhone(request.getPhone());
        }

        return toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public User updateRole(Long id, UpdateRoleRequest request, Long actorId) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        Role newRole = request.getRole();

        if (newRole == null) {
            throw new RuntimeException("Role is required");
        }

        User actor = getActor(actorId);
        assertCanChangeRole(actor, user, newRole);

        user.setRole(newRole);

        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateStatus(
            Long id,
            UpdateUserStatusRequest request,
            Long actorId
    ) {

        if (request.getStatus() == null
                || request.getStatus().isBlank()) {
            throw new RuntimeException("Status is required");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        User actor = getActor(actorId);
        assertCanAccessTarget(actor, user);

        if (actor.getId().equals(user.getId())) {
            throw new RuntimeException("Cannot update your own status");
        }

        user.setStatus(request.getStatus().trim().toLowerCase());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id, Long actorId) {

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        User actor = getActor(actorId);
        User target = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        assertCanAccessTarget(actor, target);

        if (actor.getId().equals(target.getId())) {
            throw new RuntimeException("Cannot delete your own account");
        }

        userRepository.deleteById(id);
    }

    private Specification<User> buildUserSpecification(
            String search,
            Role role,
            String status,
            User actor
    ) {

        return (root, query, criteriaBuilder) -> {
            Predicate predicate =
                    criteriaBuilder.conjunction();

            if (actor.getRole() == Role.ROLE_MANAGER) {
                predicate = criteriaBuilder.and(
                        predicate,
                        root.get("role").in(
                                Role.ROLE_CLIENT,
                                Role.ROLE_STAFF
                        )
                );
            }

            if (role != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("role"),
                                role
                        )
                );
            }

            if (status != null && !status.isBlank()) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                criteriaBuilder.lower(root.get("status")),
                                status.trim().toLowerCase()
                        )
                );
            }

            if (search != null && !search.trim().isEmpty()) {
                String keyword =
                        "%" + search.trim().toLowerCase() + "%";

                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.or(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(root.get("email")),
                                        keyword
                                ),
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(root.get("username")),
                                        keyword
                                ),
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(root.get("phone")),
                                        keyword
                                )
                        )
                );
            }

            return predicate;
        };
    }

    private Role normalizeRoleForActor(
            User actor,
            Role role
    ) {

        if (actor.getRole() != Role.ROLE_MANAGER) {
            return role;
        }

        if (role == null) {
            return null;
        }

        if (role != Role.ROLE_CLIENT
                && role != Role.ROLE_STAFF) {
            throw new RuntimeException(
                    "Manager can only manage client and staff accounts"
            );
        }

        return role;
    }

    private User getActor(Long actorId) {
        if (actorId == null) {
            throw new RuntimeException("Actor is required");
        }

        return userRepository.findById(actorId)
                .orElseThrow(() ->
                        new UserNotFoundException(actorId));
    }

    private void assertCanAccessTarget(
            User actor,
            User target
    ) {

        if (actor.getId().equals(target.getId())) {
            return;
        }

        if (actor.getRole() == Role.ROLE_ADMIN) {
            return;
        }

        if (actor.getRole() == Role.ROLE_MANAGER
                && (target.getRole() == Role.ROLE_CLIENT
                || target.getRole() == Role.ROLE_STAFF)) {
            return;
        }

        throw new RuntimeException(
                "You do not have permission to manage this user"
        );
    }

    private void assertCanChangeRole(
            User actor,
            User target,
            Role newRole
    ) {

        if (actor.getId().equals(target.getId())) {
            throw new RuntimeException("Cannot change your own role");
        }

        if (actor.getRole() == Role.ROLE_ADMIN) {
            return;
        }

        if (actor.getRole() == Role.ROLE_MANAGER
                && target.getRole() != Role.ROLE_ADMIN
                && target.getRole() != Role.ROLE_MANAGER
                && (newRole == Role.ROLE_CLIENT
                || newRole == Role.ROLE_STAFF)) {
            return;
        }

        throw new RuntimeException(
                "You do not have permission to change this role"
        );
    }

    private void assertCanCreateRole(
            User actor,
            Role role
    ) {

        if (actor.getRole() == Role.ROLE_ADMIN) {
            return;
        }

        if (actor.getRole() == Role.ROLE_MANAGER
                && (role == Role.ROLE_CLIENT
                || role == Role.ROLE_STAFF)) {
            return;
        }

        throw new RuntimeException(
                "You do not have permission to create this role"
        );
    }

    private UserCreateResponse createClientUser(UserCreateRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        return persistUser(
                request,
                Role.ROLE_CLIENT,
                "active"
        );
    }

    private UserCreateResponse persistUser(
            UserCreateRequest request,
            Role role,
            String status
    ) {

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(role)
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return UserCreateResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .loyaltyPoints(0)
                .build();
    }

    private UserCreateResponse toUserResponse(User user) {
        UserCreateResponse response = userMapper.toUserCreateResponse(user);
        int points = user.getLoyaltyPoints() == null ? 0 : user.getLoyaltyPoints();

        List<MembershipTier> activeTiers = membershipTierRepository
                .findByIsActiveTrue()
                .stream()
                .sorted(Comparator.comparing(MembershipTier::getRequiredPoints))
                .toList();

        MembershipTier currentTier = activeTiers.stream()
                .filter(tier -> tier.getRequiredPoints() <= points)
                .max(Comparator.comparing(MembershipTier::getRequiredPoints))
                .orElse(null);

        MembershipTier nextTier = activeTiers.stream()
                .filter(tier -> tier.getRequiredPoints() > points)
                .findFirst()
                .orElse(null);

        response.setLoyaltyPoints(points);
        response.setMembershipTierName(
                currentTier == null ? null : currentTier.getName()
        );
        response.setMembershipDiscountPercent(
                currentTier == null ? 0 : currentTier.getDiscountPercent()
        );
        response.setNextMembershipTierName(
                nextTier == null ? null : nextTier.getName()
        );
        response.setNextMembershipRequiredPoints(
                nextTier == null ? null : nextTier.getRequiredPoints()
        );
        response.setPointsToNextTier(
                nextTier == null ? 0 : nextTier.getRequiredPoints() - points
        );

        int lowerBound = currentTier == null ? 0 : currentTier.getRequiredPoints();
        int upperBound = nextTier == null ? lowerBound : nextTier.getRequiredPoints();
        int progress = nextTier == null
                ? 100
                : Math.min(
                        100,
                        Math.max(
                                0,
                                (int) Math.round(
                                        ((points - lowerBound) * 100.0)
                                                / Math.max(1, upperBound - lowerBound)
                                )
                        )
                );
        response.setMembershipProgressPercent(progress);

        return response;
    }
}
