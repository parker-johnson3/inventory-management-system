from setuptools import setup, Extension

module = Extension(
    'proxyserver',
    sources=['extensions/proxyserver.c']
)

setup(
    name='webserver',
    author='Mrigank Kumar',
    description='\n'.join((
        'Webserver modules, which are',
        '- views',
        '- internals'
    )),
    packages=['views', 'webserver.internals', 'views.backend', 'views.mock'],
    ext_modules=[module]
)
