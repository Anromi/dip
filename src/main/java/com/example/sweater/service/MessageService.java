package com.example.sweater.service;

import com.example.sweater.domain.User;
import com.example.sweater.domain.Mess;
import com.example.sweater.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Autowired
    private MessageRepo messageRepo;

    public Page<Mess> messageList(Pageable pageable, String filter, User user) {
        return filter != null && !filter.isEmpty() ?
                messageRepo.findByTag(filter, pageable, user) :
                messageRepo.findAll(pageable, user);
    }

    public Page<Mess> messageListForUser(Pageable pageable, User currentUser, User author) {
        return messageRepo.findByUser(pageable, author, currentUser);
    }

    public Page<Mess> messageList(Pageable pageable, User user) {
        return null;
    }
}
