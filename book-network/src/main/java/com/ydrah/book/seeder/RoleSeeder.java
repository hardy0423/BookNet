package com.ydrah.book.seeder;

import com.ydrah.book.Enum.RoleEnum;
import com.ydrah.book.repositories.RoleRepository;
import com.ydrah.book.role.Role;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        loadRoles();
    }

    private void loadRoles() {

        RoleEnum[] roleNames = {
                RoleEnum.ADMIN,
                RoleEnum.USER
        };

        Arrays.stream(roleNames).forEach(roleEnum -> {

            Optional<Role> existingRole = roleRepository.findByName(roleEnum.name());

            if (existingRole.isEmpty()) {
                Role roleToCreate = new Role();
                roleToCreate.setName(roleEnum.name());  // important

                roleRepository.save(roleToCreate);
                System.out.println("Role created: " + roleEnum.name());
            } else {
                System.out.println("Role already exists: " + roleEnum.name());
            }

        });
    }
}