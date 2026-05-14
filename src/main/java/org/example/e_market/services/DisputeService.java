package org.example.e_market.services;

import org.example.e_market.dto.requests.RaiseDisputeRequest;
import org.example.e_market.dto.requests.ResolveDisputeRequest;
import org.example.e_market.entities.Dispute;

import java.util.List;

public interface DisputeService {
    Dispute raiseDispute(Long orderId, RaiseDisputeRequest request);
    Dispute resolveDispute(Long disputeId, ResolveDisputeRequest request);
    List<Dispute> getOpenDisputes();
    List<Dispute> getCustomerDisputes();
}
