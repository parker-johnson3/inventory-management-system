// tests/urlFetch.test.ts
import axios from "axios";

/**
 * Tests each request method for Supplier endpoint, including those that don't exist. 
 * Includes tests for verifying the method, the response status, and valid JSON return.
 * Uses Axios library for fetching
 */
describe('URL Fetch **Supplier** Tests', () => {
  const host = process.env.WEBSERVER_HOST || 'localhost';
  const port = process.env.WEBSERVER_PORT || 5000;
  const BASE_URL = `http://${host}:${port}`;

  /**
   * Tests GET request response for all suppliers
   */
  test('GET request to /supplier', async () => {
    const response = await axios.get(`${BASE_URL}/supplier`);
    expect(response.config.method).toBe("get");
    expect(response.status).toBe(200);
    const responseBody = await response.data;
    expect(() => JSON.parse(JSON.stringify(responseBody))).not.toThrow();
  });

  /**
   * Tests POST request response for a single supplier
   */
  test('POST request to /supplier', async () => {
    const response = await axios.post(`${BASE_URL}/supplier`);
    expect(response.config.method).toBe("post");
    expect(response.status).toBe(200);
    const responseBody = await response.data;
    expect(responseBody).toBe('Success on "/supplier" with method POST');
  });

  /**
   * Tests GET request response for a specific supplier
   */
  test('GET request to /supplier/{ID}', async () => {
    const id = Math.floor(Math.random() * (1000 - 1) + 1);
    const response = await axios.get(`${BASE_URL}/supplier/${id}`);
    expect(response.config.method).toBe("get");
    expect(response.status).toBe(200);
    const responseBody = await response.data;
    expect(() => JSON.parse(JSON.stringify(responseBody))).not.toThrow();
  });

  /**
   * Tests DELETE request response for a specific supplier
   */
  test('DELETE request to /supplier/{ID}', async () => {
    const id = Math.floor(Math.random() * (1000 - 1) + 1);
    const response = await axios.delete(`${BASE_URL}/supplier/${id}`);
    expect(response.config.method).toBe("delete");
    expect(response.status).toBe(200);
    const responseBody = await response.data;
    expect(responseBody).toBe(`Success on "/supplier/{ID}" with method DELETE\nsupplier_id = ${id}`);
  });

  /**
   * Tests PUT request error response for a specific supplier
   */
  test('PUT error request to /supplier/{ID}', async () => {
    try {
        const id = Math.floor(Math.random() * (1000 - 1) + 1);
        await axios.put(`${BASE_URL}/supplier/${id}`);
    } catch (error) {
        expect(error.config.method).toBe("put");
        expect(error.response.status).toBe(405);
    };
  });

  /**
   * Tests POST request error response for a specific supplier
   */
  test('POST error request to /supplier/{ID}', async () => {
    try {
        const id = Math.floor(Math.random() * (1000 - 1) + 1);
        await axios.post(`${BASE_URL}/supplier/${id}`);
    } catch (error) {
        expect(error.config.method).toBe("post");
        expect(error.response.status).toBe(405);
    };
  });

  /**
   * Tests PUT request error response for all suppliers
   */
  test('PUT error request to /supplier', async () => {
    try {
        const id = Math.floor(Math.random() * (1000 - 1) + 1);
        await axios.put(`${BASE_URL}/supplier`);
    } catch (error) {
        expect(error.config.method).toBe("put");
        expect(error.response.status).toBe(405);
    };
  });

  /**
   * Tests DELETE request error response for all suppliers
   */
  test('DELETE error request to /supplier', async () => {
    try {
        const id = Math.floor(Math.random() * (1000 - 1) + 1);
        await axios.delete(`${BASE_URL}/supplier`);
    } catch (error) {
        expect(error.config.method).toBe("delete");
        expect(error.response.status).toBe(405);
    };
  });

});
