package org.example.e_market.services.admin;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.e_market.dto.response.VendorResponse;
import org.example.e_market.entity.enums.VendorStatus;
import org.example.e_market.entity.vendor.Vendor;
import org.example.e_market.utils.filters.VendorFilter;
import org.example.e_market.exceptions.CustomBadRequestException;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.repositories.VendorRepository;
import org.example.e_market.utils.specifications.VendorSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private final VendorRepository vendorRepository;


    @Transactional
    @Override
    public void updateVendorStatus(UUID vendorId, VendorStatus status) {

        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(()
                -> new CustomNotFoundException("Vendor not found"));

        if (vendor.getStatus() == status) throw new CustomBadRequestException("Status already set");
        vendor.setStatus(status);
    }

    @Override
    public Page<VendorResponse> getAllVendors(VendorFilter filter, int page, int size, String sortBy, String sortDir) {

        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "createdAt";
        }
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Specification<Vendor> specification = VendorSpecification.filter(filter);

        return vendorRepository.findAll(specification, pageable).map((vendor) -> new VendorResponse(
                vendor.getId(), vendor.getBusinessName(), vendor.getStatus(), vendor.getTotalEarnings()
        ));
    }


}
