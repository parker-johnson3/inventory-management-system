package com.cs506.project.repos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.cs506.project.schemas.ManagerSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.List;
public class ManagerRepositoryTest {
    @Mock
    private Connection mockConnection;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResult;

    @Mock
    private ManagerRepository mockManagerRepository;

    @BeforeEach
    public void setUp () throws SQLException {

        MockitoAnnotations.openMocks(this);

        ManagerRepository repo = new ManagerRepository(mockConnection);
        mockManagerRepository = spy(repo);

    }

    /**
     * Tests to see if the Query string for getWithBasicDetails is correct with a specified input
     * @throws SQLException
     */
    @Test
    public void testGetWithAllBasicDetailsQueryString () throws SQLException {
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(queryCaptor.capture())).thenReturn(mockResult);

        FacilityRepository repo = new FacilityRepository(mockConnection);

        repo.getAllWithBasicDetails(10);

        assertEquals("SELECT facilityId, city, state FROM Facility LIMIT 10", queryCaptor.getValue());
    }

    /**
     * tests to make sure that the GetWithAllBasicDetails returns the correct amount of managers
     * tests to make sure the values in the manager are correct
     * @throws SQLException
     * */
    @Test
    public void testGetWithAllBasicDetailsResponse () throws SQLException {

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResult);

        when(mockResult.next()).thenReturn(true).thenReturn(false);

        when(mockResult.getInt("ManagerId")).thenReturn(0);
        when(mockResult.getString("Name")).thenReturn("Tim");
        when(mockResult.getString("Position")).thenReturn("Manager");
        when(mockResult.getInt("AccessLevel")).thenReturn(3);

        ManagerRepository repo = new ManagerRepository(mockConnection);

        List<ManagerSchema> response = repo.getAllWithBasicDetails(0);

        assertEquals(1, response.size());

        ManagerSchema manager = response.get(0);

        assertEquals(0,manager.managerId);
        assertEquals("Tim",manager.name);
        assertEquals("Manager",manager.position);
        assertEquals(3,manager.accessLevel);

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

        ManagerRepository repo = new ManagerRepository(mockConnection);

        repo.getById(10);

        assertEquals("SELECT * FROM Manager WHERE managerId = 10", queryCaptor.getValue());

    }
    /**
     * Tests to see if the actual manager is returned from getById and that all fields have the correct data
     * @throws SQLException
     * */
    @Test
    public void testGetByIDResponse () throws SQLException {

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResult);

        when(mockResult.next()).thenReturn(true).thenReturn(false);

        when(mockResult.getInt("ManagerId")).thenReturn(0);
        when(mockResult.getString("Name")).thenReturn("Tim");
        when(mockResult.getString("Position")).thenReturn("Manager");
        when(mockResult.getInt("AccessLevel")).thenReturn(3);
        when(mockResult.getString("Password")).thenReturn("123456");
        when(mockResult.getInt("FacilityId")).thenReturn(0);


        ManagerRepository repo = new ManagerRepository(mockConnection);

        List<ManagerSchema> response = repo.getById(0);

        assertEquals(1, response.size());

        ManagerSchema manager = response.get(0);

        assertEquals(0,manager.facilityId);
        assertEquals("Tim",manager.name);
        assertEquals("Manager",manager.position);
        assertEquals(3,manager.accessLevel);
        assertEquals("123456",manager.password);
        assertEquals(0,manager.managerId);

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

        ManagerRepository repo = new ManagerRepository(mockConnection);

        repo.getAllWithAllDetails(10);

        assertEquals("SELECT * FROM Manager LIMIT 10", queryCaptor.getValue());

    }
    /**
     * Tests to see if the getWithAllDetails returns the correct manager and that it only has the specific fields
     * @throws SQLException
     * */
    @Test
    public void testGetWithAllDetailsResponse () throws SQLException {
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResult);

        when(mockResult.next()).thenReturn(true).thenReturn(false);

        when(mockResult.getInt("ManagerId")).thenReturn(0);
        when(mockResult.getString("Name")).thenReturn("Tim");
        when(mockResult.getString("Position")).thenReturn("Manager");
        when(mockResult.getInt("AccessLevel")).thenReturn(3);
        when(mockResult.getString("Password")).thenReturn("123456");
        when(mockResult.getInt("FacilityId")).thenReturn(0);

        ManagerRepository repo = new ManagerRepository(mockConnection);
        List<ManagerSchema> response = repo.getAllWithAllDetails(0);

        assertEquals(1, response.size());

        ManagerSchema manager = response.get(0);

        assertEquals(0,manager.facilityId);
        assertEquals("Tim",manager.name);
        assertEquals("Manager",manager.position);
        assertEquals(3,manager.accessLevel);
        assertEquals("123456",manager.password);
        assertEquals(0,manager.managerId);

    }
}
