package com.cs506.project.repos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.cs506.project.schemas.AirplaneSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AirplaneRepositoryTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResult;

    @Mock
    private AirplaneRepository mockAirplaneRepository;

    @BeforeEach
    public void setUp () throws SQLException {

        MockitoAnnotations.openMocks(this);

        AirplaneRepository repo = new AirplaneRepository(mockConnection);
        mockAirplaneRepository = spy(repo);

    }

    /**
     * Tests to see that the query string is correcy with a specified  limit input for getWithAllDetails
     * @throws SQLException
     */
    @Test
    public void testGetWithAllDetailsQueryString () throws SQLException {

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(queryCaptor.capture())).thenReturn(mockResult);

        AirplaneRepository repo = new AirplaneRepository(mockConnection);

        repo.getAllWithAllDetails(10);

        assertEquals("SELECT * FROM Airplane LIMIT 10;", queryCaptor.getValue());

    }

    /**
     * tests to see that the correct airplane is returned and that all the details of the airplane are correct
     * @throws SQLException
     */
    @Test
    public void testGetWithAllDetailsResponse () throws SQLException {

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResult);

        when(mockResult.next()).thenReturn(true).thenReturn(false);

        when(mockResult.getInt("AirplaneId")).thenReturn(1);
        when(mockResult.getString("Name")).thenReturn("Boeing 747");
        when(mockResult.getString("ProductionStage")).thenReturn("Development");
        when(mockResult.getDouble("Cost")).thenReturn(14.1);
        when(mockResult.getDate("DateStarted")).thenReturn(new Date(2024, 11, 11));
        when(mockResult.getDate("DateFinished")).thenReturn(new Date(2024, 12, 12));
        when(mockResult.getInt("FacilityId")).thenReturn(1);
        when(mockResult.getString("Size")).thenReturn("Large");
        when(mockResult.getBoolean("HasFirstClass")).thenReturn(true);

        AirplaneRepository repo = new AirplaneRepository(mockConnection);

        List<AirplaneSchema> response = repo.getAllWithAllDetails(1);

        assertEquals(1, response.size());

        AirplaneSchema airplane = response.get(0);

        assertEquals(1,airplane.airplaneId);
        assertEquals("Boeing 747",airplane.name);
        assertEquals("Development",airplane.productionStage);
        assertEquals(14.1, airplane.cost);
        assertEquals(new Date(2024, 11, 11), airplane.dateStarted);
        assertEquals(new Date(2024, 12, 12), airplane.dateFinished);
        assertEquals( 1, airplane.facilityID);
        assertEquals("Large",airplane.size);
        assertEquals(true, airplane.hasFirstClass);

    }

    /**
     * Tests to see if the Query string for getById is correct with a specified input
     * @throws SQLException
     */
    @Test
    public void testGetByIdQueryString () throws SQLException {

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(queryCaptor.capture())).thenReturn(mockResult);

        AirplaneRepository repo = new AirplaneRepository(mockConnection);

        repo.getById(10);

        assertEquals("SELECT * FROM Airplane WHERE AirplaneId = 10;", queryCaptor.getValue());

    }

    /**
     * tests to see that the correct airplane and all of it's details are retained when using getById
     * @throws SQLException
     */
    @Test
    public void testGetByIdResponse () throws SQLException {

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResult);

        when(mockResult.next()).thenReturn(true).thenReturn(false);

        when(mockResult.getInt("AirplaneId")).thenReturn(1);
        when(mockResult.getString("Name")).thenReturn("Boeing 747");
        when(mockResult.getString("ProductionStage")).thenReturn("Development");
        when(mockResult.getDouble("Cost")).thenReturn(14.1);
        when(mockResult.getDate("DateStarted")).thenReturn(new Date(2024, 11, 11));
        when(mockResult.getDate("DateFinished")).thenReturn(new Date(2024, 12, 12));
        when(mockResult.getInt("FacilityId")).thenReturn(1);
        when(mockResult.getString("Size")).thenReturn("Large");
        when(mockResult.getBoolean("HasFirstClass")).thenReturn(true);

        AirplaneRepository repo = new AirplaneRepository(mockConnection);

        List<AirplaneSchema> response = repo.getAllWithAllDetails(1);

        assertEquals(1, response.size());

        AirplaneSchema airplane = response.get(0);

        assertEquals(1,airplane.airplaneId);
        assertEquals("Boeing 747",airplane.name);
        assertEquals("Development",airplane.productionStage);
        assertEquals(14.1, airplane.cost);
        assertEquals(new Date(2024, 11, 11), airplane.dateStarted);
        assertEquals(new Date(2024, 12, 12), airplane.dateFinished);
        assertEquals( 1, airplane.facilityID);
        assertEquals("Large",airplane.size);
        assertEquals(true, airplane.hasFirstClass);

    }

    /**
     * Tests to see that the query string for getWithBasicDetails is correct with a specified limit
     * @throws SQLException
     */
    @Test
    public void testGetWithBasicDetailsQueryString () throws SQLException {
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(queryCaptor.capture())).thenReturn(mockResult);

        AirplaneRepository repo = new AirplaneRepository(mockConnection);

        repo.getAllWithBasicDetails(10);

        assertEquals("SELECT AirplaneId, Name, ProductionStage, Cost FROM Airplane LIMIT 10;", queryCaptor.getValue());
    }

    /**
     * Tests to see if only the specified fields are returned with the correct airplane when called
     * @throws SQLException
     */
    @Test
    public void testGetWithBasicDetailsResponse () throws SQLException {

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResult);

        when(mockResult.next()).thenReturn(true).thenReturn(false);

        when(mockResult.getInt("AirplaneId")).thenReturn(1);
        when(mockResult.getString("Name")).thenReturn("Boeing 747");
        when(mockResult.getString("ProductionStage")).thenReturn("Development");
        when(mockResult.getDouble("Cost")).thenReturn(14.1);

        AirplaneRepository repo = new AirplaneRepository(mockConnection);

        List<AirplaneSchema> response = repo.getAllWithBasicDetails(1);

        assertEquals(1, response.size());

        AirplaneSchema airplane = response.get(0);

        assertEquals(1,airplane.airplaneId);
        assertEquals("Boeing 747",airplane.name);
        assertEquals("Development",airplane.productionStage);
        assertEquals(14.1, airplane.cost);
        assertEquals(null, airplane.size);

    }

}
