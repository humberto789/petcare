package com.imd.petcare.service;

import com.imd.petcare.dto.SchedulingDTO;
import com.imd.petcare.mappers.DtoMapper;
import com.imd.petcare.mappers.SchedulingDTOMapper;
import com.imd.petcare.model.Scheduling;
import com.imd.petcare.repository.GenericRepository;
import com.imd.petcare.repository.SchedulingRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class SchedulingService implements GenericService<Scheduling, SchedulingDTO>{
    private final SchedulingRepository repository;
    private final SchedulingDTOMapper mapper;

    public SchedulingService(SchedulingRepository repository, SchedulingDTOMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public GenericRepository<Scheduling> getRepository() {
        return this.repository;
    }

    @Override
    public DtoMapper<Scheduling, SchedulingDTO> getDtoMapper() {
        return this.mapper;
    }
}
