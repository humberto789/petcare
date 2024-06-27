package com.imd.petcare.controller;

import com.imd.petcare.dto.SchedulingDTO;
import com.imd.petcare.model.Scheduling;
import com.imd.petcare.service.SchedulingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/scheduling")
@Validated
public class SchedulingController extends GenericController<Scheduling, SchedulingDTO, SchedulingService>{

    protected SchedulingController(SchedulingService service) {
        super(service);
    }
}
