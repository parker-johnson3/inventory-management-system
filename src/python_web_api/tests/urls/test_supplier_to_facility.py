import functools
import json
import random
import requests
import unittest
from tests.urls.url_test_utils import Server
from webserver.internals import models

parse = functools.partial(json.loads, cls=models.ModelDecoder)


class TestSupplierToFacility(unittest.TestCase):
    server = None

    @classmethod
    def setUpClass(cls):
        try:
            with requests.get('http://127.0.0.1:15000/', timeout=5) as resp:
                if resp.ok:
                    return
        except Exception:
            pass

        cls.server = Server(host='0.0.0.0', port=15000)
        cls.server.start()

        for _ in range(5):
            with requests.get('http://127.0.0.1:15000/', timeout=5) as resp:
                if resp.ok:
                    break
        else:
            assert False, 'Failed to start server'

    @classmethod
    def tearDownClass(cls):
        if cls.server:
            cls.server.stop()

    def test_supplierfacility_get(self):
        with requests.get('http://127.0.0.1:15000/supplierfacility') as resp:
            self.assertTrue(resp.ok)
            self.assertEqual(resp.status_code, 200)

            src = resp.text

        try:
            decoded_models = parse(src)
        except Exception:
            self.fail(msg='Could not parse JSON Data as a SupplierToFacility')

        self.assertIsNotNone(decoded_models)
        self.assertIsInstance(decoded_models, list)
        self.assertGreaterEqual(len(decoded_models), 1)

        for model in decoded_models:
            self.assertIsInstance(model, models.SupplierToFacility)
            for key, value in model.__annotations__.items():
                self.assertIsNotNone(getattr(model, key, None))
                self.assertIsInstance(getattr(model, key), value)

            self.assertGreaterEqual(model.ID, int(1e6))

    def test_supplierfacility_get_with_id(self):
        x = random.randint(1, 10000)
        with requests.get(f'http://127.0.0.1:15000/supplierfacility/{x}') as resp:
            self.assertTrue(resp.ok)
            self.assertEqual(resp.status_code, 200)

            src = resp.text

        try:
            model = parse(src)
        except Exception:
            self.fail(msg='Could not parse JSON Data as a SupplierToFacility')

        self.assertIsNotNone(model)
        self.assertIsInstance(model, models.SupplierToFacility)

        for key, value in model.__annotations__.items():
            self.assertIsNotNone(getattr(model, key, None))
            self.assertIsInstance(getattr(model, key), value)

        self.assertEqual(model.ID, x)

    def test_supplierfacility_post(self):
        with requests.post('http://127.0.0.1:15000/supplierfacility') as resp:
            self.assertTrue(resp.ok)
            self.assertEqual(resp.status_code, 200)

            text = resp.text.lower()

            expected_msg = r'success\s+on\s+"/supplierfacility"\s+with\s+method\s+post'
            self.assertRegex(text, expected_msg)

    def test_supplierfacility_post_with_id(self):
        x = random.randint(1, 10000)
        with requests.post(f'http://127.0.0.1:15000/supplierfacility/{x}') as resp:
            self.assertFalse(resp.ok)
            self.assertEqual(resp.status_code, 405)

    def test_supplierfacility_put(self):
        with requests.put('http://127.0.0.1:15000/supplierfacility') as resp:
            self.assertFalse(resp.ok)
            self.assertEqual(resp.status_code, 405)

    def test_supplierfacility_put_with_id(self):
        x = random.randint(1, 10000)
        with requests.put(f'http://127.0.0.1:15000/supplierfacility/{x}') as resp:
            self.assertFalse(resp.ok)
            self.assertEqual(resp.status_code, 405)

    def test_supplierfacility_delete(self):
        with requests.delete(f'http://127.0.0.1:15000/supplierfacility') as resp:
            self.assertFalse(resp.ok)
            self.assertEqual(resp.status_code, 405)

    def test_supplierfacility_delete_with_id(self):
        x = random.randint(1, 10000)
        with requests.delete(f'http://127.0.0.1:15000/supplierfacility/{x}') as resp:
            self.assertTrue(resp.ok)
            self.assertEqual(resp.status_code, 200)

            text = resp.text.lower()

            expected_msg = r'success\s+on\s+"/supplierfacility/[{]id[}]"\s+with\s+method\s+delete'
            self.assertRegex(text, expected_msg)

            expected_id = f'supplierfacility_id\\s+=\\s+{x}'
            self.assertRegex(text, expected_id)


if __name__ == '__main__':
    unittest.main()
