# mypy: disable-error-code="attr-defined"

import random
import faker
import functools
from typing import Callable, Generator

from webserver.internals.models import (
    Airplane,
    AirplaneToComponent,
    Component,
    Customer,
    Facility,
    Manager,
    Supplier,
    SupplierToFacility,
)

data_faker = faker.Faker()


def static_vars(**kwargs) -> Callable[[Callable], Callable]:
    '''Decorator to add static variables to a function.

    This decorator adds static variables to a function. It takes keyword
    arguments where the keys are the variable names and the values are the
    variable values. The variables are added as attributes to the function
    object.
    '''
    def decorator(func: Callable) -> Callable:
        for key, value in kwargs.items():
            setattr(func, key, value)

        setattr(func, '_static_vars', kwargs.keys())

        return func

    return decorator


def reset_faker_seed(func: Callable) -> Callable:
    '''
    Decorator to reset the seed of the Faker library before executing a function.
    It ensures that each call to the decorated function starts with the same
    Faker seed, providing consistent randomized data generation across
    multiple function calls.
    '''
    @functools.wraps(func)
    def inner(*args, **kwargs):
        faker.Faker.seed(506)
        return func(*args, **kwargs)

    return inner


@reset_faker_seed
@static_vars(ID=1_000_000, faker=faker.Faker())
def airplane(n: int = 1) -> Generator[Airplane, None, None]:
    '''Generate a specified number of airplane records.

    Args:
        n (int): The number of airplane records to generate. Must be a
            positive integer. Defaults to 1.

    Yields:
        Airplane: A generator that yields airplane records.
    '''
    n = max(1, n)
    while n > 0:
        yield Airplane(airplane.ID,
                       airplane.faker.name().split(' ')[0],
                       airplane.faker.sentence(),
                       airplane.faker.city(),
                       airplane.faker.state(),
                       random.choice(('In-Progress', 'Finished', 'Unstarted')),
                       round(random.random() * 1e7, 2),
                       airplane.faker.date(),
                       airplane.faker.date(),
                       random.randint(int(1e6), int(2e6)),
                       random.randint(100, 400),
                       'Big',
                       random.choice((True, False)))
        airplane.ID += 1
        n -= 1


@reset_faker_seed
@static_vars(ID=2_000_000)
def airplane_to_component(n: int = 1) -> Generator[AirplaneToComponent, None, None]:
    '''Generate a specified number of airplane to component records.

    Args:
        n (int): The number of airplane to component records to generate.
        Must be a positive integer. Defaults to 1.

    Yields:
        AirplaneToComponent: A generator that yields airplane to component
        records.
    '''
    n = max(1, n)
    while n > 0:
        yield AirplaneToComponent(airplane_to_component.ID,
                                  random.randint(int(1e6), int(2e6)),
                                  random.randint(int(1e6), int(2e6)),
                                  )
        airplane_to_component.ID += 1
        n -= 1


@reset_faker_seed
@static_vars(ID=3_000_000, faker=faker.Faker())
def component(n: int = 1) -> Generator[Component, None, None]:
    '''Generate a specified number of component records.

    Args:
        n (int): The number of component records to generate. Must be a
            positive integer. Defaults to 1.

    Yields:
        Component: A generator that yields component records.
    '''
    n = max(1, n)
    while n > 0:
        yield Component(
            component.ID,
            component.faker.name().split(' ')[0],
            component.faker.sentence(),
            component.faker.city(),
            component.faker.state(),
            random.choice(('Wings', 'Engines', 'Wheels', 'Gears', 'Flaps')),
            random.randint(int(1e6), int(2e6)),
            round(random.random() * 1e4, 2),
            random.choice(('In-Progress', 'Finished', 'Unstarted'))
        )
        component.ID += 1
        n -= 1


@reset_faker_seed
@static_vars(ID=4_000_000, faker=faker.Faker())
def customer(n: int = 1) -> Generator[Customer, None, None]:
    '''Generate a specified number of airplane records.

    Args:
        n (int): The number of airplane records to generate. Must be a
            positive integer. Defaults to 1.

    Yields:
        Customer: A generator that yields airplane records.
    '''
    n = max(1, n)
    while n > 0:
        yield Customer(
            customer.ID,
            customer.faker.name(),
            customer.faker.sentence()
        )
        customer.ID += 1
        n -= 1


@reset_faker_seed
@static_vars(ID=6_000_000, faker=faker.Faker())
def facility(n: int = 1) -> Generator[Facility, None, None]:
    '''Generate a specified number of facility records.

    Args:
        n (int): The number of facility records to generate. Must be a
            positive integer. Defaults to 1.

    Yields:
        Facility: A generator that yields facility records.
    '''
    n = max(1, n)
    while n > 0:
        yield Facility(
            facility.ID,
            facility.faker.name(),
            facility.faker.city(),
            facility.faker.state(),
            facility.faker.sentence(),
            random.randint(0, 100),
            random.randint(0, 100),
            random.randint(0, 100),
            random.randint(0, 100),
            random.randint(0, 100),
            random.randint(int(1e6), int(2e6)),
        )
        facility.ID += 1
        n -= 1


@reset_faker_seed
@static_vars(ID=7_000_000, faker=faker.Faker())
def manager(n: int = 1) -> Generator[Manager, None, None]:
    '''Generate a specified number of manager records.

    Args:
        n (int): The number of manager records to generate. Must be a
            positive integer. Defaults to 1.

    Yields:
        Manager: A generator that yields manager records.
    '''
    n = max(1, n)
    while n > 0:
        yield Manager(
            manager.ID,
            manager.faker.name(),
            manager.faker.password(),
            random.choice(('Facility', 'Warehouse', 'Regional')),
            random.randint(1, 3),
            random.randint(int(1e6), int(2e6))
        )
        manager.ID += 1
        n -= 1


@reset_faker_seed
@static_vars(ID=8_000_000, faker=faker.Faker())
def supplier(n: int = 1) -> Generator[Supplier, None, None]:
    '''Generate a specified number of supplier records.

    Args:
        n (int): The number of supplier records to generate. Must be a
            positive integer. Defaults to 1.

    Yields:
        Supplier: A generator that yields supplier records.
    '''
    n = max(1, n)
    while n > 0:
        types = random.choices(('Wings', 'Engines', 'Wheels', 'Gears', 'Flaps'),
                               k=random.randint(1, 4))
        yield Supplier(
            supplier.ID,
            supplier.faker.name(),
            supplier.faker.sentence(),
            ','.join(types),
            ','.join([str(random.randint(int(1e6), int(2e6)))
                      for _ in range(random.randint(1, 5))])
        )
        supplier.ID += 1
        n -= 1


@reset_faker_seed
@static_vars(ID=9_000_000, faker=faker.Faker())
def supplier_to_facility(n: int = 1) -> Generator[SupplierToFacility, None, None]:
    '''Generate a specified number of supplier records.

    Args:
        n (int): The number of supplier records to generate. Must be a
            positive integer. Defaults to 1.

    Yields:
        Supplier: A generator that yields supplier records.
    '''
    n = max(1, n)
    while n > 0:
        yield SupplierToFacility(
            supplier_to_facility.ID,
            random.randint(int(1e6), int(2e6)),
            random.randint(int(1e6), int(2e6))
        )
        supplier_to_facility.ID += 1
        n -= 1


# Controlled instances that will always be the same
control = static_vars(**{
    'airplane': list(airplane(10)),
    'airplanecomponent': list(airplane_to_component(20)),
    'component': list(component(20)),
    'facility': list(facility(5)),
    'deleted': list(),
    'dry_run': True,
})


@control
def deterministic(kind: str = '') -> list:
    if hasattr(deterministic, 'dry_run'):
        delattr(deterministic, 'dry_run')

        assert len(deterministic.airplane) % 2 == 0, 'Wrong number of airplanes'

        # Python hack to traverse two at a time
        component_iter = iter(deterministic.component)
        atc_iter = iter(deterministic.airplanecomponent)
        airplane_iter = iter(deterministic.airplane)

        for component, atc in zip(component_iter, atc_iter):
            component2, atc2 = next(component_iter), next(atc_iter)

            airplane = next(airplane_iter)

            # Record 1 for ATC
            atc.airplane_id = airplane.ID
            atc.component_id = component.ID

            # Record 2 for ATC
            atc2.airplane_id = airplane.ID
            atc2.component_id = component2.ID

        # Same hack again
        airplane_iter = iter(deterministic.airplane)
        facility_iter = iter(deterministic.facility)
        for airplane in airplane_iter:
            airplane2 = next(airplane_iter)
            facility = next(facility_iter)

            facility.components_in_production = 2  # from airplane
            facility.components_completed = 2  # from airplane2

            facility.models_in_production = 1  # from airplane
            facility.models_completed = 1  # from airplane2

            # They're physically together
            airplane.city, airplane2.city = (facility.city,) * 2
            airplane.state, airplane2.state = (facility.state,) * 2

            airplane.facility_id = facility.ID
            airplane2.facility_id = facility.ID

        for atc in deterministic.airplanecomponent:
            airplane = Airplane._lookup(atc.airplane_id)
            component = Component._lookup(atc.component_id)
            component.city = airplane.city
            component.state = airplane.state

        # Correlation baby!
        return []

    if kind not in deterministic._static_vars:
        raise KeyError(f'{kind = } does not exist')

    return getattr(deterministic, kind, [])
