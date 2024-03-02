package com.example.vinhomeproject.service;

import com.example.vinhomeproject.models.PaymentType;
import com.example.vinhomeproject.repositories.PaymentTypeRepository;
import com.example.vinhomeproject.response.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentTypeService {
    private final PaymentTypeRepository rs;




    public PaymentTypeService(PaymentTypeRepository rs) {
        this.rs = rs;
    }

    public ResponseEntity<ResponseObject> getAllPaymentType(){
         rs.findAll();
        return ResponseEntity.ok(new ResponseObject(
                "successfully",
                rs.findAll()
        ));
    }
    public ResponseEntity<ResponseObject> getPaymentTypeById(Long id){
        return ResponseEntity.ok(new ResponseObject(
                "successfully",
                rs.findPaymentTypeById(id)
        ));
    }
    public ResponseEntity<String> deletePaymentType(Long id) {
        PaymentType existingUser = rs.findById(id).orElse(null);

        if (existingUser != null) {
            existingUser.setStatus(false);


             rs.save(existingUser);
            return ResponseEntity.ok("delete successfully");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("id not exist");
        }


    }
    public ResponseEntity<String> updatePaymentType(PaymentType id) {
        PaymentType existingUser =rs.findById(id.getId()).orElse(null);

        if (existingUser != null) {
            existingUser.setStatus(id.isStatus());
            existingUser.setPayments(id.getPayments());
            existingUser.setName(id.getName());
            existingUser.setCreateBy(id.getCreateBy());
            existingUser.setCreateDate(id.getCreateDate());
            existingUser.setModifiedBy(id.getModifiedBy());
            existingUser.setModifiedDate(id.getModifiedDate());
            rs.save(existingUser);
            return ResponseEntity.ok("update successfully");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("id not exist");
        }
    }

    public ResponseEntity<String> createPaymentType(PaymentType id) {
        PaymentType existingUser = new PaymentType();


        existingUser.setStatus(id.isStatus());
        existingUser.setPayments(id.getPayments());
        existingUser.setName(id.getName());
        existingUser.setCreateBy(id.getCreateBy());
        existingUser.setCreateDate(id.getCreateDate());
        existingUser.setModifiedBy(id.getModifiedBy());
        existingUser.setModifiedDate(id.getModifiedDate());
        rs.save(existingUser);
        return ResponseEntity.ok("create successfully");
    }
}
