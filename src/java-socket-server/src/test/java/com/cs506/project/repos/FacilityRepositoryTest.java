package com.cs506.project.repos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.cs506.project.schemas.FacilitySchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.List;

public class FacilityRepositoryTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResult;

    @Mock
    private FacilityRepository mockFacilityRepository;

    @BeforeEach
    public void setUp () throws SQLException {

        MockitoAnnotations.openMocks(this);

        FacilityRepository repo = new FacilityRepository(mockConnection);
        mockFacilityRepository = spy(repo);

    }
    /**
     * Tests to make sure correct query string is correct with specified input
     * @throws SQLException
     * */
    @Test
    public void testGetWithAllDetailsQueryString () throws SQLException {

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(queryCaptor.capture())).thenReturn(mockResult);

        FacilityRepository repo = new FacilityRepository(mockConnection);

        repo.getAllWithAllDetails(10);

        assertEquals("SELECT * FROM Facility LIMIT 10", queryCaptor.getValue());

    }
    /**
     * tests to make sure that the GetWithAllDetails returns the correct amount of facilities
     * tests to make sure the values in the facility are correct
     * @throws SQLException
     * */
    @Test
    public void testGetWithAllDetailsResponse () throws SQLException {

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResult);

        when(mockResult.next()).thenReturn(true).thenReturn(false);

        when(mockResult.getInt("FacilityID")).thenReturn(1);
        when(mockResult.getString("Name")).thenReturn("Madison Facility");
        when(mockResult.getString("City")).thenReturn("Madison");
        when(mockResult.getString("State")).thenReturn("Wisconsin");
        when(mockResult.getString("Description")).thenReturn("Madison Facility");
        when(mockResult.getInt("ComponentsInProduction")).thenReturn(12);
        when(mockResult.getInt("ComponentsCompleted")).thenReturn(10);
        when(mockResult.getInt("ModelsInProduction")).thenReturn(4);
        when(mockResult.getInt("ModelsCompleted")).thenReturn(3);
        when(mockResult.getInt("EmployeeCount")).thenReturn(30);
        when(mockResult.getInt("ManagerID")).thenReturn(100000);

        FacilityRepository repo = new FacilityRepository(mockConnection);

        List<FacilitySchema> response = repo.getAllWithAllDetails(1);

        assertEquals(1, response.size());

        FacilitySchema facility = response.get(0);

        assertEquals(1,facility.facilityId);
        assertEquals("Madison Facility",facility.name);
        assertEquals("Madison",facility.city);
        assertEquals("Wisconsin",facility.state);
        assertEquals("Madison Facility",facility.description);
        assertEquals(12,facility.componentsInProduction);
        assertEquals(10,facility.componentsCompleted);
        assertEquals(4,facility.modelsInProduction);
        assertEquals(3,facility.modelsCompleted);
        assertEquals(30,facility.employeeCount);
        assertEquals(100000,facility.managerId);

    }

    /**
     *Tests to see if the Query string for getById is correct with a specified input
     * @throws SQLException
     * */
    @Test
    public void testGetByIdQueryString () throws SQLException {

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(queryCaptor.capture())).thenReturn(mockResult);

        FacilityRepository repo = new FacilityRepository(mockConnection);

        repo.getById(10);

        assertEquals("SELECT * FROM Facility WHERE facilityId = 10", queryCaptor.getValue());

    }
    /**
     * Tests to see if the actual facility is returned from getById and that all fields have the correct data
     * @throws SQLException
     * */
    @Test
    public void testGetByIDResponse () throws SQLException {

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResult);

        when(mockResult.next()).thenReturn(true).thenReturn(false);

        when(mockResult.getInt("FacilityID")).thenReturn(1);
        when(mockResult.getString("Name")).thenReturn("Madison Facility");
        when(mockResult.getString("City")).thenReturn("Madison");
        when(mockResult.getString("State")).thenReturn("Wisconsin");
        when(mockResult.getString("Description")).thenReturn("Madison Facility");
        when(mockResult.getInt("ComponentsInProduction")).thenReturn(12);
        when(mockResult.getInt("ComponentsCompleted")).thenReturn(10);
        when(mockResult.getInt("ModelsInProduction")).thenReturn(4);
        when(mockResult.getInt("ModelsCompleted")).thenReturn(3);
        when(mockResult.getInt("EmployeeCount")).thenReturn(30);
        when(mockResult.getInt("ManagerID")).thenReturn(100000);

        FacilityRepository repo = new FacilityRepository(mockConnection);

        List<FacilitySchema> response = repo.getById(1);

        assertEquals(1, response.size());

        FacilitySchema facility = response.get(0);

        assertEquals(1,facility.facilityId);
        assertEquals("Madison Facility",facility.name);
        assertEquals("Madison",facility.city);
        assertEquals("Wisconsin",facility.state);
        assertEquals("Madison Facility",facility.description);
        assertEquals(12,facility.componentsInProduction);
        assertEquals(10,facility.componentsCompleted);
        assertEquals(4,facility.modelsInProduction);
        assertEquals(3,facility.modelsCompleted);
        assertEquals(30,facility.employeeCount);
        assertEquals(100000,facility.managerId);

    }
    /**
     * Tests to see if the Query string for getWithBasicDetails is correct with a specified input
     * @throws SQLException
     */
    @Test
    public void testGetWithBasicDetailsQueryString () throws SQLException {
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(queryCaptor.capture())).thenReturn(mockResult);

        FacilityRepository repo = new FacilityRepository(mockConnection);

        repo.getAllWithBasicDetails(10);

        assertEquals("SELECT facilityId, city, state FROM Facility LIMIT 10", queryCaptor.getValue());
    }

    /**
     * Tests to see if the GetWithBasicDetails returns the correct facility and that it only has the specific fields
     * @throws SQLException
     * */
    @Test
    public void testGetWithBasicDetailsResponse () throws SQLException {
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResult);

        when(mockResult.next()).thenReturn(true).thenReturn(false);

        when(mockResult.getInt("FacilityID")).thenReturn(1);
        when(mockResult.getString("City")).thenReturn("Madison");
        when(mockResult.getString("State")).thenReturn("Wisconsin");

        FacilityRepository repo = new FacilityRepository(mockConnection);
        List<FacilitySchema> response = repo.getAllWithBasicDetails(1);

        assertEquals(1, response.size());
        FacilitySchema facility = response.get(0);
        assertEquals(1,facility.facilityId);
        assertEquals("Madison",facility.city);
        assertEquals("Wisconsin",facility.state);

    }

}
