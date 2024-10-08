package com.team.sop_management_service;

import com.team.sop_management_service.controller.SOPController;
import com.team.sop_management_service.error.InvalidSOPException;
import com.team.sop_management_service.error.SOPNotFoundException;
import com.team.sop_management_service.models.SOP;
import com.team.sop_management_service.enums.Visibility;
import com.team.sop_management_service.enums.SOPStatus;
import com.team.sop_management_service.service.SOPService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SOPControllerTest {

    @Mock
    private SOPService sopService;

    @InjectMocks
    private SOPController sopController;

    private SOP sampleSOP;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleSOP = new SOP();
        sampleSOP.setSopId("1");
        sampleSOP.setTitle("Sample SOP");
        sampleSOP.setVisibility(Visibility.DEPARTMENT);
        sampleSOP.setStatus(SOPStatus.DRAFT);
    }

    @Test
    void testInitiateSOP_success() throws InvalidSOPException {
        when(sopService.initiateSOP(any(SOP.class))).thenReturn(sampleSOP);

        ResponseEntity<?> response = sopController.initiateSOP(sampleSOP);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleSOP, response.getBody());
    }

    @Test
    void testInitiateSOP_invalidSOPException() throws InvalidSOPException {
        when(sopService.initiateSOP(any(SOP.class))).thenThrow(new InvalidSOPException("Invalid SOP"));

        ResponseEntity<?> response = sopController.initiateSOP(sampleSOP);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid SOP", response.getBody());
    }

    @Test
    void testSaveSOP_success() {
        when(sopService.saveSOP(any(SOP.class))).thenReturn(sampleSOP);

        ResponseEntity<?> response = sopController.saveSOP(sampleSOP);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleSOP, response.getBody());
    }

    @Test
    void testDeleteSOP_success() {
        doNothing().when(sopService).deleteSOP(anyString());

        ResponseEntity<?> response = sopController.deleteSOP("1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetAllSOPs_success() {
        List<SOP> sopList = new ArrayList<>();
        sopList.add(sampleSOP);
        when(sopService.getAllSOPs()).thenReturn(sopList);

        ResponseEntity<?> response = sopController.getAllSOPs();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sopList, response.getBody());
    }

    @Test
    void testGetSOPById_success() throws SOPNotFoundException {
        when(sopService.getSOPById(anyString())).thenReturn(sampleSOP);

        ResponseEntity<?> response = sopController.getSOPById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleSOP, response.getBody());
    }

    @Test
    void testGetSOPById_notFound() throws SOPNotFoundException {
        when(sopService.getSOPById(anyString())).thenThrow(new SOPNotFoundException("SOP not found"));

        ResponseEntity<?> response = sopController.getSOPById("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetSOPsByVisibility_success() {
        List<SOP> sopList = new ArrayList<>();
        sopList.add(sampleSOP);
        when(sopService.getSOPsByVisibility(any(Visibility.class))).thenReturn(sopList);

        ResponseEntity<?> response = sopController.getSOPsByVisibility(Visibility.ORGANIZATION);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sopList, response.getBody());
    }

    @Test
    void testGetSOPsByStatus_success() {
        List<SOP> sopList = new ArrayList<>();
        sopList.add(sampleSOP);
        when(sopService.getSOPsByStatus(any(SOPStatus.class))).thenReturn(sopList);

        ResponseEntity<?> response = sopController.getSOPsByStatus(SOPStatus.DRAFT);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sopList, response.getBody());
    }

    @Test
    void testGetSOPsByAuthor_success() {
        List<SOP> sopList = new ArrayList<>();
        sopList.add(sampleSOP);
        when(sopService.getSOPsByAuthor(anyString())).thenReturn(sopList);

        ResponseEntity<?> response = sopController.getSOPsByAuthor("author123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sopList, response.getBody());
    }
}
