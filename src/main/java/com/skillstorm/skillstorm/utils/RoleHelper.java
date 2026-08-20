package com.skillstorm.skillstorm.utils;

import com.skillstorm.skillstorm.enums.Role;
import com.skillstorm.skillstorm.exceptions.InvalidRoleFormatException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleHelper {

    public static Set<Role> convertFromStringToSet(String rolesStr) throws InvalidRoleFormatException{

        if(rolesStr == null || rolesStr.isBlank()){
            return Set.of();
        }
        return Arrays.stream(rolesStr.split("[:,;|]"))
                .map(String::trim)
                .filter(segment -> !segment.isEmpty())
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }

    public static String convertFromSetToString(Set<Role> roles) throws InvalidRoleFormatException {

        if(roles == null || roles.isEmpty()){
            return "USER:";
        }

        return roles.stream()
                .map(Role::name)
                .collect(Collectors.joining(":")) + ":";
    }
}
