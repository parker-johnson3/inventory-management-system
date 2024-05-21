package com.cs506.project;

import com.cs506.project.repos.AirplaneRepository;
import com.cs506.project.schemas.AirplaneSchema;
import com.cs506.project.schemas.SocketServerRequest;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RepositoryControllerTest {

    @Test
    public void testCreateSocketServerRequest () {

        String request = "{\n" +
                "  \"type\": \"CREATE\",\n" +
                "  \"entityName\": \"Airplane\",\n" +
                "  \"limit\": 0,\n" +
                "  \"requestingAllDetails\": \"true\",\n" +
                "  \"entities\": []\n" +
                "}";

        RepositoryController controller = new RepositoryController();
        Object ans = controller.createSocketServerRequest(request.getBytes());

        assertTrue(ans instanceof SocketServerRequest);

        assertEquals(((SocketServerRequest) ans).type, "CREATE");
        assertEquals(((SocketServerRequest) ans).entityName, "Airplane");
        assertEquals(((SocketServerRequest) ans).limit, 0);
        assertEquals(((SocketServerRequest) ans).requestingAllDetails, true);
        assertEquals(((SocketServerRequest) ans).entities.size(), 0);

    }


}
