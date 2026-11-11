package com.skillstorm.skillstorm.service;


import java.util.List;
import java.util.Optional;

import com.google.common.hash.BloomFilter;
import com.skillstorm.skillstorm.dto.extraDtos.ForgotPassword;
import com.skillstorm.skillstorm.dto.extraDtos.UncompletedDto;
import com.skillstorm.skillstorm.dto.payloads.QuizUncompletedPayload;
import com.skillstorm.skillstorm.dto.requests.UserLogin;
import com.skillstorm.skillstorm.dto.requests.UserRegister;
import com.skillstorm.skillstorm.dto.requests.UserUpdateRequest;
import com.skillstorm.skillstorm.dto.responses.ProfileTrackerAPIResponse;
import com.skillstorm.skillstorm.dto.responses.UserResponse;
import com.skillstorm.skillstorm.enums.AuthProvider;
import com.skillstorm.skillstorm.enums.FilterType;
import com.skillstorm.skillstorm.exceptions.*;
import com.skillstorm.skillstorm.jwts.JwtTokenProvider;
import com.skillstorm.skillstorm.mappers.UserMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider tokenProvider;
    private final BloomFilter<String> usernameFilter;
    private final BloomFilter<String> emailFilter;
    private final UserTrackerService userTrackerService;
    private final EmailService emailService;
    private final UserBadgeService userBadgeService;
    private final LeaderboardService leaderboardService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);


    @PostConstruct
    public void init(){
        List<String> existingUsernames = userRepository.findAllUsername();
        existingUsernames.forEach(usernameFilter::put);

        List<String> existingEmails = userRepository.findAllEmail();
        existingEmails.forEach(emailFilter::put);
    }

    public String register(UserRegister request) {

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if(existingUser.isEmpty()) throw new ResourceNotFoundException("User doesn't exist with email: " + request.getEmail());

        if(existingUser.isPresent()){
            var error = String.format("Existing user with email: %s already exist",request.getEmail());
            throw new AuthorizationException(error);
        }

        User user = userMapper.toUser(request);

        if(!emailService.hasMXRecord(request.getEmail())){
            throw new InvalidEmailException("Email domain is invalid");
        }
        if(isUsernameTaken(user.getUsername())) throw new UsernameTakenException("Username is already taken");

        if(!isValidUsername(user.getUsername())) throw new InvalidUsernameException("malformed username detected: " + user.getUsername());

        // hashes the password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        // evicts the leaderboard from cache once a new user registers
        leaderboardService.refreshLeaderboard();

        // adds new user's details to each bloom filter (for username and email)
        addToFilters(user.getUsername(),user.getEmail());

        // creates locked badges and adds it to the user_badge table in the source of truth
        userBadgeService.initializeBadgesForUser(savedUser);

        return "Account created Successfully";
    }

    private void addToFilters(String username, String email) {
        usernameFilter.put(username);
        emailFilter.put(email);
    }

    public String login(UserLogin request){

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist with email: " + request.getEmail()));

        // check if the hash matches with the one entered and in the db password
        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new AuthorizationException("Invalid Credentials");
        }


        // generate jwt token and sends it to frontend
        return tokenProvider.generateToken(user);
    }

    @Cacheable(cacheNames = "user",key="#userId")
    public UserResponse getById(int userId) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist"));

        return userMapper.mapToResponse(existingUser);
    }

    @Cacheable(cacheNames = "user",key="'all'")
    public List<UserResponse> getAll() {
        return userRepository
            .findAll()
            .stream()
            .map(userMapper::mapToResponse)
            .toList();
    }

    @Cacheable(cacheNames = "user",key="'leaderboard_all'")
    public List<User> findAllByOrderByXpDesc() {
        return userRepository.findAllByOrderByXpDesc();
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "user",key="'all'")
    })
    @Transactional
    public UserResponse update(int userId, UserUpdateRequest request) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist"));

        if(isUsernameTaken(request.getUsername())) throw new UsernameTakenException("Username is already taken");
        if(!isValidUsername(request.getUsername())) throw new InvalidUsernameException("malformed username detected: " + request.getUsername());

        existingUser.setUsername(request.getUsername());

        usernameFilter.put(request.getUsername());

        User savedUser = userRepository.save(existingUser);

        return userMapper.mapToResponse(savedUser);
    }
    @Caching(evict = {
            @CacheEvict(cacheNames = "user",key="#userId"),
            @CacheEvict(cacheNames = "user",key="'all'")
    })

    @Transactional
    public void delete(int userId) {
        userBadgeService.deleteByUserId(userId);
        userTrackerService.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    public boolean takenType(FilterType type, String val){
        return switch(type) {
            case EMAIL -> isEmailTaken(val);
            case USERNAME -> isUsernameTaken(val);
        };
    }

    private boolean isUsernameTaken(String username){
        if(!usernameFilter.mightContain(username)) return false;
        return userRepository.existsByUsername(username);
    }

    private boolean isEmailTaken(String email){
        if(!emailFilter.mightContain(email)) return false;
        return userRepository.existsByEmail(email);
    }

    private boolean isValidUsername(String username) {
       var regex = "^(?!.*@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,})[a-zA-Z0-9_@.-]+$";

       return username != null && username.matches(regex);
    }

    public ProfileTrackerAPIResponse history(int userId) {
        return userTrackerService.history(userId);
    }


    public void forgotPassword(ForgotPassword payload) {

        var user = userRepository.findByEmail(payload.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist"));

        if(user.getAuthProvider().equals(AuthProvider.OAUTH)) throw new PasswordResetException("Cannot change password for a OAuth based user");
        user.setPassword(passwordEncoder.encode(payload.getPassword()));
        userRepository.save(user);
    }

    public void sendForgetEmail(String email) {

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist"));

        var token = tokenProvider.generateToken(user);

        emailService.sendEmail(user.getEmail(),token);
    }
}
