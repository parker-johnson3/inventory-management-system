// tests/urlFetch.test.ts
import axios from "axios";

/**
 * Tests each request method for SupplierFacility endpoint, including those that don't exist. 
 * Includes tests for verifying the method, the response status, and valid JSON return.
 * Uses Axios library for fetching
 */
describe('URL Fetch **SupplierFacility** Tests', () => {
  const host = process.env.WEBSERVER_HOST || 'localhost';
  const port = process.env.WEBSERVER_PORT || 5000;
  const BASE_URL = `http://${host}:${port}`;

  /**
   * Tests GET request response for all supplierfacilities
   */
  test('GET request to /supplierfacility', async () => {
    const response = await axios.get(`${BASE_URL}/supplierfacility`);
    expect(response.config.method).toBe("get");
    expect(response.status).toBe(200);
    const responseBody = await response.data;
    expect(() => JSON.parse(JSON.stringify(responseBody))).not.toThrow();
  });

  /**
   * Tests POST request response for a single supplierfacility
   */
  test('POST request to /supplierfacility', async () => {
    const response = await axios.post(`${BASE_URL}/supplierfacility`);
    expect(response.config.method).toBe("post");
    expect(response.status).toBe(200);
    const responseBody = await response.data;
    expect(responseBody).toBe('Success on "/supplierfacility" with method POST');
  });

  /**
   * Tests GET request response for a specific supplierfacility
   */
  test('GET request to /supplierfacility/{ID}', async () => {
    const id = Math.floor(Math.random() * (1000 - 1) + 1);
    const response = await axios.get(`${BASE_URL}/supplierfacility/${id}`);
    expect(response.config.method).toBe("get");
    expect(response.status).toBe(200);
    const responseBody = await response.data;
    expect(() => JSON.parse(JSON.stringify(responseBody))).not.toThrow();
  });

  /**
   * Tests DELETE request response for a specific supplierfacility
   */
  test('DELETE request to /supplierfacility/{ID}', async () => {
    const id = Math.floor(Math.random() * (1000 - 1) + 1);
    const response = await axios.delete(`${BASE_URL}/supplierfacility/${id}`);
    expect(response.config.method).toBe("delete");
    expect(response.status).toBe(200);
    const responseBody = await response.data;
    expect(responseBody).toBe(`Success on "/supplierfacility/{ID}" with method DELETE\nsupplierfacility_id = ${id}`);
  });

  /**
   * Tests PUT request error response for a specific supplierfacility
   */
  test('PUT error request to /supplierfacility/{ID}', async () => {
    try {
        const id = Math.floor(Math.random() * (1000 - 1) + 1);
        await axios.put(`${BASE_URL}/supplierfacility/${id}`);
    } catch (error) {
        expect(error.config.method).toBe("put");
        expect(error.response.status).toBe(405);
    };
  });

  /**
   * Tests POST request error response for a specific supplierfacility
   */
  test('POST error request to /supplierfacility/{ID}', async () => {
    try {
        const id = Math.floor(Math.random() * (1000 - 1) + 1);
        await axios.post(`${BASE_URL}/supplierfacility/${id}`);
    } catch (error) {
        expect(error.config.method).toBe("post");
        expect(error.response.status).toBe(405);
    };
  });

  /**
   * Tests PUT request error response for a all supplierfacilities
   */
  test('PUT error request to /supplierfacility', async () => {
    try {
        const id = Math.floor(Math.random() * (1000 - 1) + 1);
        await axios.put(`${BASE_URL}/supplierfacility`);
    } catch (error) {
        expect(error.config.method).toBe("put");
        expect(error.response.status).toBe(405);
    };
  });

  /**
   * Tests DELETE request error response for a all supplierfacilities
   */
  test('DELETE error request to /supplierfacility', async () => {
    try {
        const id = Math.floor(Math.random() * (1000 - 1) + 1);
        await axios.delete(`${BASE_URL}/supplierfacility`);
    } catch (error) {
        expect(error.config.method).toBe("delete");
        expect(error.response.status).toBe(405);
    };
  });

});
