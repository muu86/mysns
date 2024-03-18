package com.mj.mysns.batch.address;

import com.mj.mysns.location.entity.LegalAddress;
import com.mj.mysns.location.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class LegalAddressWriter implements ItemWriter<LegalAddress> {

    private final AddressRepository addressRepository;

    @Override
    public void write(Chunk<? extends LegalAddress> chunk) throws Exception {
        addressRepository.saveAll(chunk);
    }
}
