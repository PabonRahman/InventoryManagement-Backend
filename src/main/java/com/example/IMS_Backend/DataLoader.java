package com.example.IMS_Backend;

import com.example.IMS_Backend.model.ERole;
import com.example.IMS_Backend.model.Role;
import com.example.IMS_Backend.model.User;
import com.example.IMS_Backend.model.Category;
import com.example.IMS_Backend.repository.RoleRepository;
import com.example.IMS_Backend.repository.UserRepository;
import com.example.IMS_Backend.repository.CategoryRepository;
import com.example.IMS_Backend.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== STARTING DATA LOADER ===");

        // Clear existing data to start fresh (optional - you might want to keep some data)
        userRepository.deleteAll();
        roleRepository.deleteAll();
        // categoryRepository.deleteAll(); // Consider keeping categories if they're stable
        // supplierRepository.deleteAll();
        System.out.println("Cleared existing user and role data");

        // Create roles
        Role roleUser = roleRepository.save(new Role(ERole.ROLE_USER));
        Role roleModerator = roleRepository.save(new Role(ERole.ROLE_MODERATOR));
        Role roleAdmin = roleRepository.save(new Role(ERole.ROLE_ADMIN));

        System.out.println("Created roles: USER, MODERATOR, ADMIN");

        // Create users with specific roles
        createUser("user", "user@example.com", "password123", roleUser);
        createUser("moderator", "moderator@example.com", "password123", roleModerator);
        createUser("admin", "admin@example.com", "password123", roleAdmin);

        // Add the pabon user from your screenshot
        createUser("pabon", "pabon@example.com", "password123", roleUser);

        // Create test categories
        createTestCategories();

        // Verify what was created
        verifyData();

        System.out.println("=== DATA LOADER COMPLETED ===");
    }

    private void createUser(String username, String email, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        System.out.println("✓ Created user: " + username);

        // Immediately verify the roles were saved
        User verifiedUser = userRepository.findById(savedUser.getId()).get();
        String userRoles = verifiedUser.getRoles().stream()
                .map(r -> r.getName().name())
                .reduce((a, b) -> a + ", " + b)
                .orElse("NO ROLES");
        System.out.println("  Roles: " + userRoles);
    }

    private void createTestCategories() {
        try {
            System.out.println("=== CREATING TEST CATEGORIES ===");

            // Create sample categories if they don't exist
            if (categoryRepository.count() == 0) {
                Category electronics = new Category("Electronics", "Electronic devices and gadgets");
                Category clothing = new Category("Clothing", "Fashion and apparel");
                Category books = new Category("Books", "Books and literature");

                categoryRepository.save(electronics);
                categoryRepository.save(clothing);
                categoryRepository.save(books);

                System.out.println("Created test categories: Electronics, Clothing, Books");
            }

            System.out.println("Total categories in database: " + categoryRepository.count());
        } catch (Exception e) {
            System.out.println("Error creating test categories: " + e.getMessage());
        }
    }

    private void verifyData() {
        System.out.println("=== FINAL VERIFICATION ===");

        String[] testUsers = {"user", "moderator", "admin", "pabon"};
        for (String username : testUsers) {
            userRepository.findByUsername(username).ifPresent(user -> {
                String roles = user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("NO ROLES");
                System.out.println(username + " -> " + roles);
            });
        }

        System.out.println("Total users in DB: " + userRepository.count());
        System.out.println("Total roles in DB: " + roleRepository.count());
        System.out.println("Total categories in DB: " + categoryRepository.count());
    }
}