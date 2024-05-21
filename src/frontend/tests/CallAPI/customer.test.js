// tests/urlFetch.test.ts
import axios from "axios";


/**
 * Tests each request method for Customer endpoint, including those that don't exist. 
 * Includes tests for verifying the method, the response status, and valid JSON return.
 * Uses Axios library for fetching
 */
describe('URL Fetch **Customer** Tests', () => {
  const host = process.env.WEBSERVER_HOST || 'localhost';
  const port = process.env.WEBSERVER_PORT || 5000;
  const BASE_URL = `http://${host}:${port}`;

  /**
   * Tests GET request response for all customers
   */
  test('GET request to /customer', async () => {
    const response = await axios.get(`${BASE_URL}/customer`);
    expect(response.status).toBe(200);
    const responseBody = await response.data;
    expect(response.config.method).toBe("get");
    expect(() => JSON.parse(JSON.stringify(responseBody))).not.toThrow();
  });

  /**
   * Tests POST request response for a single customer
   */
  test('POST request to /customer', async () => {
    const response = await axios.post(`${BASE_URL}/customer`);
    expect(response.config.method).toBe("post");
    expect(response.status).toBe(200);
    const responseBody = await response.data;
    expect(responseBody).toBe('Success on "/customer" with method POST');
  });

  /**
   * Tests GET request response for a specific customer
   */
  test('GET request to /customer/{ID}', async () => {
    const id = Math.floor(Math.random() * (1000 - 1) + 1);
    const response = await axios.get(`${BASE_URL}/customer/${id}`);
    expect(response.config.method).toBe("get");
    expect(response.status).toBe(200);
    const responseBody = await response.data;
    expect(() => JSON.parse(JSON.stringify(responseBody))).not.toThrow();
  });

   /**
   * Tests DELETE request response for a specific customer
   */
  test('DELETE request to /customer/{ID}', async () => {
    const id = Math.floor(Math.random() * (1000 - 1) + 1);
    const response = await axios.delete(`${BASE_URL}/customer/${id}`);
    expect(response.config.method).toBe("delete");
    expect(response.status).toBe(200);
    const responseBody = await response.data;
    expect(responseBody).toBe(`Success on "/customer/{ID}" with method DELETE\ncustomer_id = ${id}`);
  });

   /**
   * Tests PUT request error response for a specific customer
   */
  test('PUT error request to /customer/{ID}', async () => {
    try {
        const id = Math.floor(Math.random() * (1000 - 1) + 1);
        await axios.put(`${BASE_URL}/customer/${id}`);
    } catch (error) {
        expect(error.config.method).toBe("put");
        expect(error.response.status).toBe(405);
    };
  });

   /**
   * Tests POST request response for a specific customer
   */
  test('POST error request to /customer/{ID}', async () => {
    try {
        const id = Math.floor(Math.random() * (1000 - 1) + 1);
        await axios.post(`${BASE_URL}/customer/${id}`);
    } catch (error) {
        expect(error.config.method).toBe("post");
        expect(error.response.status).toBe(405);
    };
  });

   /**
   * Tests PUT request response for all customers
   */
  test('PUT error request to /customer', async () => {
    try {
        const id = Math.floor(Math.random() * (1000 - 1) + 1);
        await axios.put(`${BASE_URL}/customer`);
    } catch (error) {
        expect(error.config.method).toBe("put");
        expect(error.response.status).toBe(405);
    };
  });

  /**
   * Tests DELETE request response for all customers
   */
  test('DELETE error request to /customer', async () => {
    try {
        const id = Math.floor(Math.random() * (1000 - 1) + 1);
        await axios.delete(`${BASE_URL}/customer`);
    } catch (error) {
        expect(error.config.method).toBe("delete");
        expect(error.response.status).toBe(405);
    };
  });

});

