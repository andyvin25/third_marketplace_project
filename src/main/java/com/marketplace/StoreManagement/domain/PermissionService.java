package com.marketplace.StoreManagement.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("Permission_StoreManagement")
public class PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    public Permission getOrCreatePermission(Permission.PermissionsEnum permissionEnum) {

        Permission permission = permissionRepository.findByName(permissionEnum).orElse(null);
        System.out.println("found permission = " + permission);
        if (permission == null) {
            System.out.println("new permission = " + permission);
            permission = new Permission(permissionEnum);
            System.out.println("new permission have been initialized= " + permission);
            permissionRepository.save(permission);
            System.out.println("permission success to save");
        }

        return  permission;
    }
}
