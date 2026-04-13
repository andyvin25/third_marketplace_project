package com.marketplace.StoreManagement.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component("Main_data_seeder")
public class DataSeeder {

        @Value("${admin.name}")
        private String admin_name;

        @Value("${admin.password}")
        private String admin_password;

        @Value("${admin.email}")
        private String admin_email;

        @Bean
        public CommandLineRunner initiateAdminUser(AccountRepository userRepository, StoreService storeService,
                        RoleService roleService, PermissionService permissionService) {
                return args -> {

                        if (userRepository.findByEmail(admin_email).isEmpty()) {
                                System.out.println("initialize");
                                Permission AdminRead = permissionService
                                                .getOrCreatePermission(Permission.PermissionsEnum.ADMIN_CREATE);
                                System.out.println("AdminRead = " + AdminRead);
                                Permission AdminCreate = permissionService
                                                .getOrCreatePermission(Permission.PermissionsEnum.ADMIN_READ);
                                System.out.println("AdminCreate = " + AdminCreate);
                                Permission AdminUpdate = permissionService
                                                .getOrCreatePermission(Permission.PermissionsEnum.ADMIN_UPDATE);
                                System.out.println("AdminUpdate = " + AdminUpdate);
                                Permission AdminDelete = permissionService
                                                .getOrCreatePermission(Permission.PermissionsEnum.ADMIN_DELETE);
                                System.out.println("AdminDelete = " + AdminDelete);
                                Permission SellerRead = permissionService
                                                .getOrCreatePermission(Permission.PermissionsEnum.SELLER_CREATE);
                                System.out.println("SellerRead = " + SellerRead);
                                Permission SellerCreate = permissionService
                                                .getOrCreatePermission(Permission.PermissionsEnum.SELLER_READ);
                                Permission SellerUpdate = permissionService
                                                .getOrCreatePermission(Permission.PermissionsEnum.SELLER_UPDATE);
                                Permission SellerDelete = permissionService
                                                .getOrCreatePermission(Permission.PermissionsEnum.SELLER_DELETE);

                                Set<Permission> adminPermissions = new HashSet<>();
                                adminPermissions.add(AdminRead);
                                adminPermissions.add(AdminCreate);
                                adminPermissions.add(AdminUpdate);
                                adminPermissions.add(AdminDelete);

                                System.out.println("adminPermissions execute= " + adminPermissions);
                                Set<Permission> sellerPermissions = new HashSet<>();
                                sellerPermissions.add(SellerRead);
                                sellerPermissions.add(SellerCreate);
                                sellerPermissions.add(SellerUpdate);
                                sellerPermissions.add(SellerDelete);

                                System.out.println("sellerPermissions execute= " + sellerPermissions);
                                System.out.println("admin_password = " + admin_password);
                                Set<Role> roles = new HashSet<>();
                                Role role1 = roleService.getOrCreateRoleAccount(Role.RoleEnum.ADMIN, adminPermissions);
                                System.out.println("role1 get or create role account execute= " + role1);

                                Role role2 = roleService.getOrCreateRoleAccount(Role.RoleEnum.SELLER,
                                                sellerPermissions);
                                roles.add(role1);
                                roles.add(role2);

                                Set<Store> stores = new HashSet<>();

                                StoreDetail storeDetail = StoreDetail.builder()
                                                .rate(5)
                                                .numberOfSales(0)
                                                .status(StoreDetail.StoreStatusEnum.PLATINUM)
                                                .build();

                                Store store = Store.builder()
                                                .name("admin_store")
                                                .description(
                                                                "This is Admin Store. And there's no need for history like the other store that register in here")
                                                .storeDetail(storeDetail)
                                                .isDeleted(false)
                                                .build();

                                stores.add(store);

                                storeDetail.setStore(store);
                                Account user = new Account(admin_email, admin_name, admin_password, roles, stores);

                                store.setAccount(user);

                                userRepository.save(user);

                        }

                };
        }
}
