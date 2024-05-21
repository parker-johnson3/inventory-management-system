import random
import unittest
import types

from webserver.internals import models
from views.mock import mock_data


class TestFacilityMocker(unittest.TestCase):
    def test_single(self):
        generator = mock_data.facility(1)

        self.assertIsInstance(generator, types.GeneratorType)

        for count, element in enumerate(generator):
            self.assertIsInstance(element, models.Facility)
            self.assertLess(count, 1)

    def test_no_params(self):
        generator = mock_data.facility()

        self.assertIsInstance(generator, types.GeneratorType)

        for count, element in enumerate(generator):
            self.assertIsInstance(element, models.Facility)
            self.assertLess(count, 1)

    def test_negative_param(self):
        n = random.randint(-1000, -1)
        generator = mock_data.facility(n)

        self.assertIsInstance(generator, types.GeneratorType)

        for count, element in enumerate(generator):
            self.assertIsInstance(element, models.Facility)
            self.assertLess(count, 1)

    def test_multiple(self):
        n = random.randint(1, 1000)
        generator = mock_data.facility(n)

        self.assertIsInstance(generator, types.GeneratorType)

        for count, element in enumerate(generator):
            self.assertIsInstance(element, models.Facility)
            self.assertLess(count, n)


if __name__ == '__main__':
    unittest.main()
