package com.imd.petcare.mappers;

import com.imd.petcare.dto.SchedulingDTO;
import com.imd.petcare.model.Scheduling;
import com.imd.petcare.model.User;
import org.springframework.stereotype.Component;

@Component
public class SchedulingDTOMapper implements DtoMapper<Scheduling, SchedulingDTO>{

    @Override
    public SchedulingDTO toDto(Scheduling entity) {
        return new SchedulingDTO(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getTitle(),
                entity.getDescription(),
                entity.getMonth(),
                entity.getDay(),
                entity.getYear(),
                entity.getType());
    }

    @Override
    public Scheduling toEntity(SchedulingDTO schedulingDTO) {
        Scheduling scheduling = new Scheduling();
        scheduling.setId(schedulingDTO.id());
        scheduling.setTitle(schedulingDTO.title());
        scheduling.setDescription(schedulingDTO.description());
        scheduling.setMonth(schedulingDTO.month());
        scheduling.setDay(schedulingDTO.day());
        scheduling.setYear(schedulingDTO.year());
        scheduling.setType(schedulingDTO.type());
        User user = new User();
        user.setId(schedulingDTO.userId());
        scheduling.setUser(user);

        return scheduling;
    }
}
