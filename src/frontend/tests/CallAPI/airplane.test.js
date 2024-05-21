// tests/urlFetch.test.ts
import axios from "axios";

/**
 * Tests each request method for Airplane endpoint, including those that don't exist. 
 * Includes tests for verifying the method, the response status, and valid JSON return.
 * Uses Axios library for fetching
 */
describe('URL Fetch **Airplane** Tests', () => {
  const host = process.env.WEBSERVER_HOST || 'localhost';
  const port = process.env.WEBSERVER_PORT || 5000;
  const BASE_URL = `http://${host}:${port}`;

  /**
   * Tests GET request response for all airplanes
   */
  test('GET request to /airplane', async () => {
    const response = await axios.get(`${BASE_URL}/airplane`);
    expect(response.config.method).toBe("get");
    expect(response.status).toBe(200);
    const responseBody = await response.data; 
    expect(() => JSON.parse(JSON.stringify(responseBody))).not.toThrow();
  });

  /**
   * Tests POST request response for a single airplane
   */
  test('POST request to /airplane', async () => {
    const response = await axios.post(`${BASE_URL}/airplane`);
    expect(response.config.method).toBe("post");
    expect(response.status).toBe(200);
    const responseBody = await response.data; 
    expect(responseBody).toBe('Success on "/airplane" with method POST');
  });


  /**
   * Tests GET request response for a specific airplane
   */
  test('GET request to /airplane/{ID}', async () => {
    const id = Math.floor(Math.random() * (1000 - 1) + 1);
    const response = await axios.get(`${BASE_URL}/airplane/${id}`);
    expect(response.config.method).toBe("get");
    expect(response.status).toBe(200);
    const responseBody = await response.data; 
    expect(() => JSON.parse(JSON.stringify(responseBody))).not.toThrow();
  });

  /**
   * Tests PUT request response for a specific airplane
   */
  test('PUT request to /airplane/{ID}', async () => {
    const id = Math.floor(Math.random() * (1000 - 1) + 1);
    const response = await axios.put(`${BASE_URL}/airplane/${id}`);
    expect(response.config.method).toBe("put");
    expect(response.status).toBe(200);
    const responseBody = await response.data; 
    expect(responseBody).toBe(`Success on "/airplane/{ID}" with method PUT\nairplane_id = ${id}`);
  });

  /**
   * Tests DELETE request response for a specific airplane
   */
  test('DELETE request to /airplane/{ID}', async () => {
    const id = Math.floor(Math.random() * (1000 - 1) + 1);
    const response = await axios.delete(`${BASE_URL}/airplane/${id}`);
    expect(response.config.method).toBe("delete");
    expect(response.status).toBe(200);
    const responseBody = await response.data; 
    expect(responseBody).toBe(`Success on "/airplane/{ID}" with method DELETE\nairplane_id = ${id}`);
  });

  /**
   * Tests POST request error response for a specific airplane
   */
  test('POST error request to /airplane/{ID}', async () => {
    try {
        const id = Math.floor(Math.random() * (1000 - 1) + 1);
        await axios.post(`${BASE_URL}/airplane/${id}`);
    } catch (error) {
        expect(error.config.method).toBe("post");
        expect(error.response.status).toBe(405);
    };
  });

  /**
   * Tests PUT request error response for all airplanes
   */
  test('PUT error request to /airplane', async () => {
    try {
        const id = Math.floor(Math.random() * (1000 - 1) + 1);
        await axios.put(`${BASE_URL}/airplane`);
    } catch (error) {
        expect(error.config.method).toBe("put");
        expect(error.response.status).toBe(405);
    };
  });

  /**
   * Tests DELETE request error response for all airplanes
   */
  test('DELETE error request to /airplane', async () => {
    try {
        const id = Math.floor(Math.random() * (1000 - 1) + 1);
        await axios.delete(`${BASE_URL}/airplane`);
    } catch (error) {
        expect(error.config.method).toBe("delete");
        expect(error.response.status).toBe(405);
    };
  });

});

