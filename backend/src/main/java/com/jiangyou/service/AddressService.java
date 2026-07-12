package com.jiangyou.service;

import com.jiangyou.dto.AddressRequest;
import com.jiangyou.model.Address;
import com.jiangyou.repository.AddressRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    public AddressService(AddressRepository ar) { this.addressRepository = ar; }

    public List<Address> getList(Long userId) { return addressRepository.findByUserIdOrderByIsDefaultDesc(userId); }

    public Address save(Long userId, Long addressId, AddressRequest req) {
        Address addr = addressId != null ? addressRepository.findById(addressId).orElse(new Address()) : new Address();
        if (addressId == null) addr.setUserId(userId);
        addr.setConsignee(req.getConsignee());
        addr.setPhone(req.getPhone());
        addr.setRegion(req.getRegion() != null ? req.getRegion() : "");
        addr.setDetail(req.getDetail() != null ? req.getDetail() : "");
        if (req.getIsDefault() != null && req.getIsDefault()) {
            addressRepository.findByUserIdOrderByIsDefaultDesc(userId).forEach(a -> { a.setIsDefault(0); addressRepository.save(a); });
            addr.setIsDefault(1);
        } else if (addressId == null) {
            addr.setIsDefault(addressRepository.findByUserIdOrderByIsDefaultDesc(userId).isEmpty() ? 1 : 0);
        }
        return addressRepository.save(addr);
    }

    public void delete(Long id) { addressRepository.deleteById(id); }
}
