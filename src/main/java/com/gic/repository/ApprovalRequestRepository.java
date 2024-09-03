package com.gic.repository;

import com.gic.model.ApprovalRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApprovalRequestRepository extends MongoRepository<ApprovalRequest, String> {
}