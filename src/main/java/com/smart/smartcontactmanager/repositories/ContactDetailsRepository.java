package com.smart.smartcontactmanager.repositories;

import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.entities.contactDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactDetailsRepository extends JpaRepository<contactDetails,Integer> {
    //pagination
    @Query("from contactDetails as c where c.user.id =:userId")
    //currentPage-page
    //Contact Per page - 5
    public Page<contactDetails> findContactsByUser(@Param("userId")int userId, Pageable pePageable);

    //search
    public List<contactDetails> findByNameContainingAndUser(String name, User user);

}
