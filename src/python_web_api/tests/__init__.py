import unittest


if __name__ == '__main__':
    loader = unittest.TestLoader()

    start_dir = '.'
    pattern = 'test_*.py'
    suite = loader.discover(start_dir=start_dir, pattern=pattern)

    runner = unittest.TextTestRunner()
    runner.run(suite)
