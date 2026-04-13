package com.marketplace.StoreManagement.domain;

import java.util.*;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marketplace.Exception.ResourceDuplicationException;
import com.marketplace.Exception.ResourceNotFoundException;
import com.marketplace.StoreManagement.api.StoreRequestDTO;
import com.marketplace.StoreManagement.domain.StoreDetail.StoreStatusEnum;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public Optional<Account> getAccountById(String id) {
        return accountRepository.findById(id);
    }

    public Account saveAccount(Account account) {
        Objects.requireNonNull(account);
        return accountRepository.save(account);
    }

    public void deleteStoreFromAccount(String sellerId) {
        Account account = getAccountById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with this id " + sellerId));
        Store store = account.getStores()
                .stream()
                .filter(store1 -> store1.getIsDeleted() == null || !store1.getIsDeleted())
                .findAny()
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with this id " + sellerId));
        store.setIsDeleted(true);
        saveAccount(account);
    }

    public Store getStoreWithAccount(String id) {
        Account account = accountRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("There is no account with this id " + id ));

        System.out.println("account = " + account);
        System.out.println("account.getName() = " + account.getName());
        System.out.println("account.getEmail() = " + account.getEmail());
        System.out.println("account.getStore = " + account.getStores());
        Store store = account.getStores()
                .stream()
                .filter(foundStore -> foundStore.getIsDeleted() == null || !foundStore.getIsDeleted())
                .findAny()
                .orElseThrow(() -> new ResourceNotFoundException("You need to create store"));
        System.out.println("store = " + store);
        System.out.println("store.getName() = " + store.getName());
        return store;
    }

    public Store createStore(Account account, Role role, Boolean hasStoreNameSame, StoreRequestDTO storeDto) {

        System.out.println("account = " + account);
        System.out.println("account.getEmail() = " + account.getEmail());
        System.out.println("account.getName() = " + account.getName());

        if (!account.getStores().isEmpty()) {
            throw new ResourceDuplicationException("The store has already created");
        }
//        if (account.getStores().isEmpty() && account.getId().equals(account.getId())) {
//            throw new ResourceNotFoundException("Store name has already been taken and ");
//        }
        System.out.println("Check is empty pass");
        if (hasStoreNameSame) {
            throw new ResourceDuplicationException("The name of the store is already been taken");
        }
        System.out.println("check has store same name passed");
        account.getAccountRoles().add(role);
        System.out.println("account.getAccountRoles() = " + account.getAccountRoles());
        for (Role role1: account.getAccountRoles()) {
            System.out.println("role1.getRoleName() = " + role1.getRoleName());
            for (Permission permission: role1.getPermissions()) {
                System.out.println("permission = " + permission.getName());
            }
        }

        StoreDetail storeDetail = StoreDetail.builder()
            .rate(0)
            .numberOfSales(0)
            .status(StoreStatusEnum.BRONZE)
            .build();
        Store createdStore = Store.builder()
            .name(storeDto.storeName())
            .storeDetail(storeDetail)
            .isDeleted(false)
            .build();
        Profile storeProfile = new Profile(createdStore);

        System.out.println("Insert the store passed");

        createdStore.setStoreProfile(storeProfile);
        storeDetail.setStore(createdStore);
        createdStore.setAccount(account);
        account.getStores().add(createdStore);
        System.out.println("All set passed");
        saveAccount(account);
        System.out.println("Saved passed");
        return createdStore;
    }

    public String updateStoreName(String sellerId, Boolean hasStoreNameSame, StoreRequestDTO storeDto) {
//        Store activeStore = storeRepository.findActiveStoreByAccountId(sellerId)
//            .orElseThrow(() -> new ResourceNotFoundException("Active store not found for this seller"));
        Account account = getAccountById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with this id " + sellerId));

        if (hasStoreNameSame) {
            throw new ResourceDuplicationException("The name of the store is already been taken");
        }

        Store store = account.getStores()
                        .stream()
                .filter(foundStore -> foundStore.getIsDeleted() == null || !foundStore.getIsDeleted())
                .findAny()
                .orElseThrow(() -> new ResourceNotFoundException("You need to create the store first"));

        store.setName(storeDto.storeName());

        saveAccount(account);

        return store.getName();
    }

    public void deleteAccountAndStore(String sellerId) {
        Account account = getAccountById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller with this id is not found " + sellerId));
        accountRepository.delete(account);
    }

}
