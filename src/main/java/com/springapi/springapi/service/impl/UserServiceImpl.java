package com.springapi.springapi.service.impl;

import com.springapi.springapi.entity.UserEntity;
import com.springapi.springapi.exception.UserServiceException;
import com.springapi.springapi.model.response.ErrorMessage;
import com.springapi.springapi.model.response.ErrorMessages;
import com.springapi.springapi.repository.UserRepository;
import com.springapi.springapi.service.UserService;
import com.springapi.springapi.shared.Utils;
import com.springapi.springapi.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
   private UserRepository userRepository;
    @Autowired
    private Utils utils;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public UserDto createUser(UserDto user) {
        if(userRepository.findByEmail(user.getEmail()) != null) throw new RuntimeException("Record already exists ");
        String publicUserId=utils.generateUserId(30);
        UserEntity userEntity=new UserEntity();
        BeanUtils.copyProperties(user,userEntity);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setUserId(publicUserId  );
        UserEntity storedUserDetails=userRepository.save(userEntity);
        UserDto returnValue=new UserDto();
        BeanUtils.copyProperties(storedUserDetails,returnValue);
        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity=userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException("User Not Found");
        return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(),new ArrayList<>());
    }

    public UserDto getUser(String email){
        UserEntity userEntity=userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException("User Not Found");
        UserDto returnValue=new UserDto();
        BeanUtils.copyProperties(userEntity,returnValue);
        return returnValue;

    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserDto returnValue =new UserDto();

        UserEntity userEntity=userRepository.findByUserId(userId);
        if(userEntity == null)throw new UsernameNotFoundException("User with "+userId+"Not found");
        BeanUtils.copyProperties(userEntity,returnValue);


        return returnValue;
    }

    @Override
    public UserDto updateUser(String userId, UserDto userDto) {
        UserDto returnValue=new UserDto();
        UserEntity userEntity=userRepository.findByUserId(userId);
        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
       UserEntity updated= userRepository.save(userEntity);
       BeanUtils.copyProperties(updated,returnValue);

        return returnValue;
    }

    @Override
    public void delete(String userId) {
        UserEntity userEntity=userRepository.findByUserId(userId);
        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }

        userRepository.delete(userEntity);


    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        if (page > 0) page = page-1;
        List<UserDto> returnValue=new ArrayList<>();
        Pageable pageableRequest= PageRequest.of(page,limit);
        Page<UserEntity> usersPage=userRepository.findAll(pageableRequest);
        List<UserEntity> users=usersPage.getContent();
        for (UserEntity userEntity:users){
            UserDto userDto=new UserDto();
            BeanUtils.copyProperties(userEntity,userDto);
            returnValue.add(userDto);
        }

        return returnValue;
    }
}
