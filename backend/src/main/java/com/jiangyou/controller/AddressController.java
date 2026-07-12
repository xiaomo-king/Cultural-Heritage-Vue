package com.jiangyou.controller;

import com.jiangyou.dto.*;
import com.jiangyou.model.Address;
import com.jiangyou.service.AddressService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {
    private final AddressService addressService;
    public AddressController(AddressService as) { this.addressService = as; }

    @GetMapping("/list")
    public ApiResponse<List<Address>> getList(@RequestHeader("userId") Long userId) {
        return ApiResponse.success(addressService.getList(userId));
    }

    @PostMapping("/save")
    public ApiResponse<Address> save(@RequestHeader("userId") Long userId, @RequestBody AddressRequest req,
                                     @RequestParam(required = false) Long addressId) {
        return ApiResponse.success(addressService.save(userId, addressId, req));
    }

    @PostMapping("/delete/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ApiResponse.success("删除成功", null);
    }
}