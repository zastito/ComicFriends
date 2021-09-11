package com.app.ComicFriends.repositories;

import org.springframework.data.repository.CrudRepository;

import com.app.ComicFriends.entities.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
}