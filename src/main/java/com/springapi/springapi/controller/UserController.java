package com.springapi.springapi.controller;


import com.springapi.springapi.entity.UserEntity;
import com.springapi.springapi.model.OperationStatusModel;
import com.springapi.springapi.model.RequestOperationName;
import com.springapi.springapi.model.RequestOperationStaus;
import com.springapi.springapi.model.request.UserDetailsRequestModel;
import com.springapi.springapi.model.response.UserRest;
import com.springapi.springapi.service.UserService;
import com.springapi.springapi.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")//http://localhost:8080
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping(path = "/{id}",
    produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id){

        UserRest returnValue=new UserRest();

        UserDto userDto=userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto,returnValue);

        return returnValue;
    }

    @PostMapping(
            produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE}
            )
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails)throws Exception{
        UserRest returnValue=new UserRest();
        if (userDetails.getFirstName().isEmpty()) throw new NullPointerException( "The object is null" );
        UserDto userDto=new UserDto();
        BeanUtils.copyProperties(userDetails,userDto);
        UserDto createdUser=userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser,returnValue);
        return returnValue;

    }

    @PutMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE}
    )
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetals){
        UserRest returnValue =new UserRest();
        UserDto userDto=new UserDto();
        BeanUtils.copyProperties(userDetals,userDto);
        UserDto updateUser=userService.updateUser(id,userDto);
        BeanUtils.copyProperties(updateUser,returnValue);
        return  returnValue;


    }

    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id){

        OperationStatusModel returnValue=new OperationStatusModel();

        returnValue.setOperationName(RequestOperationName.DELETE.name());
        userService.delete(id);
        returnValue.setOperationResult(RequestOperationStaus.SUCCESS.name());

        return returnValue;

    }

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page",defaultValue ="0" ) int page,
                                   @RequestParam(value = "limit",defaultValue = "25" ) int limit){

        List<UserRest> returnValue=new ArrayList<>();

        List<UserDto> users=userService.getUsers(page,limit);
        for (UserDto userDto:users){
            UserRest userModel=new UserRest();
            BeanUtils.copyProperties(userDto,userModel);
            returnValue.add(userModel);
        }

        return returnValue;
    }

}
