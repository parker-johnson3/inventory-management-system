from dataclasses import dataclass
from typing import Any
from json import JSONEncoder, JSONDecoder
import abc

__all__ = (
    'Airplane',
    'AirplaneToComponent',
    'Component',
    'Customer',
    'Facility',
    'Manager',
    'Model',
    'RecordTracker',
    'Supplier',
    'SupplierToFacility',
)

decoder_mapping: dict[str, type] = dict()


class RecordTracker(abc.ABCMeta):
    '''Metaclass that is used to track instantiated records for Models

    This metaclass should typically not be used directly.
    Use a subclass of the Model abstract base class instead.
    '''
    def __new__(cls: type, name: str, bases: tuple[type], dct: dict[str, Any]):
        new_cls: type = type.__new__(cls, name, bases, dct)
        decoder_mapping[name] = new_cls

        def meta_init(self: Any) -> None:
            if not hasattr(self, 'ID'):
                raise AttributeError(
                    f'Field `ID` not found in {self},'
                    f' which is required for tracking'
                )

            if hasattr(new_cls, 'records'):
                if not isinstance(getattr(new_cls, 'records'), dict):
                    raise TypeError(
                        f'{new_cls} ClassVar records expected to be a dict'
                        f' mapping ID to records. Found {type(new_cls.records)}'
                    )
            else:
                setattr(new_cls, 'records', dict())

            records: dict = getattr(new_cls, 'records')
            records[getattr(self, 'ID')] = self

        def lookup(ID: int) -> Any:
            if not hasattr(new_cls, 'records'):
                raise AttributeError(
                    f'Field `records` not found, which is required for lookups.'
                )

            if not isinstance(getattr(new_cls, 'records'), dict):
                raise TypeError(
                    f'{new_cls} ClassVar records expected to be a dict'
                    f' of records. Found {type(new_cls.records)}'
                )

            records: dict = getattr(new_cls, 'records')

            return records.get(ID, None)

        setattr(new_cls, '__post_init__', meta_init)
        setattr(new_cls, '_lookup', staticmethod(lookup))

        return new_cls


class Model(abc.ABC, metaclass=RecordTracker):
    pass


@dataclass
class Airplane(Model):
    '''Represents an Airplane Record

    This is a dataclass that stores information regarding an airplane,
    equivalent to a database record

    Attributes:
        ID: int
        name: str
        production_stage_name: str
        cost: float
        date_started: str in format 'YYYY-MM-DD'
        date_finished: str in format 'YYYY-MM-DD'
        customer_id: int, foreign key to a Customer instance
        seating_capacity: int
        size: str
        has_first_class: bool
    '''
    ID: int
    name: str
    description: str
    city: str
    state: str
    production_stage: str
    cost: float
    date_started: str
    date_finished: str
    facility_id: int
    seating_capacity: int
    size: str
    has_first_class: bool


@dataclass
class AirplaneToComponent(Model):
    '''Represents a relationship between airplanes and components

    This is a dataclass that stores mappings between airplane instances and
    component instances, equivalent to a database record

    Attributes:
        ID: int
        airplane_id: int, ID of the airplane instance
        component_id: int, ID of the component instance
    '''
    ID: int
    airplane_id: int
    component_id: int


@dataclass
class Component(Model):
    '''Represents a Component Record

    This is a dataclass that stores information regarding an airplane component,
    equivalent to a database record

    Attributes:
        ID: int
        name: str
        description: str
        component_type: str
        supplier_id: int, foreign key to the supplier of this component
        cost: float
        production_stage: str
    '''
    ID: int
    name: str
    description: str
    city: str
    state: str
    component_type: str
    facility_id: int
    cost: float
    production_stage: str


@dataclass
class Customer(Model):
    '''Represents a Customer Record

    This is a dataclass that stores information regarding a customer,
    equivalent to a database record

    Attributes:
        ID: int
        name: str
        description: str
    '''
    ID: int
    name: str
    description: str


@dataclass
class Facility(Model):
    '''Represents a Facility Record

    This is a dataclass that stores information regarding a facility,
    equivalent to a database record

    Attributes:
        ID: int
        name: str
        city: str
        state: str
        description: str
        components_in_production: int
        components_completed: int
        models_in_production: int
        models_completed: int
        employee_count: int
        manager_id: int, foreign key to a manager instance
    '''
    ID: int
    name: str
    city: str
    state: str
    description: str
    components_in_production: int
    components_completed: int
    models_in_production: int
    models_completed: int
    employee_count: int
    manager_id: int


@dataclass
class Manager(Model):
    '''Represents a Manager Record

    This is a dataclass that stores information regarding a manager,
    equivalent to a database record

    Attributes:
        ID: int
        name: str
        password: str
        position: str
        access_level: int
        facility_id: int, foreign key to a facility instance
    '''
    ID: int
    name: str
    password: str
    position: str
    access_level: int
    facility_id: int


@dataclass
class Supplier(Model):
    '''Represents a Supplier Record

    This is a dataclass that stores information regarding a Supplier,
    equivalent to a database record

    Attributes:
        ID: int
        name: str
        description: str
        component_types: str, comma separated string of component types
        facilities_supplying: str, comma separated string of facility ID's
    '''
    ID: int
    name: str
    description: str
    component_types: str
    facilities_supplying: str


@dataclass
class SupplierToFacility(Model):
    '''Represents a relationship between suppliers and facilities

    This is a dataclass that stores mappings between supplier instances and
    facility instances, equivalent to a database record

    Attributes:
        ID: int
        supplier_id: int, ID of the supplier instance
        facility_id: int, ID of the facility instance
    '''
    ID: int
    supplier_id: int
    facility_id: int


class ModelEncoder(JSONEncoder):
    '''Custom JSON encoder for encoding instances of a Model class.

    This encoder extends the JSONEncoder class to provide custom encoding
    behavior for instances of a Model class. It converts Model instances into
    JSON-compatible dictionaries.
    '''

    def default(self, obj: Any) -> Any:
        '''Encode the given object as JSON.

        This method is called by the JSONEncoder superclass to encode the
        given object as JSON. If the object is not an instance of a Model
        class, the default behavior of the superclass is used.

        Args:
            obj (Any): The object to encode.

        Returns:
            Any: The JSON-compatible representation of the object.
        '''
        if not isinstance(obj, Model):
            return super().default(obj)

        d = {x: getattr(obj, x) for x in obj.__annotations__}
        d.update({'type': obj.__class__.__name__})
        return d


class ModelDecoder(JSONDecoder):
    '''Custom JSON decoder for decoding JSON strings into Model objects.

    This decoder extends the JSONDecoder class to provide custom decoding
    behavior for JSON strings. It decodes JSON strings into Model objects,
    where each object is an instance of a subclass of Model.
    '''
    @staticmethod
    def _dispatch(obj: Any) -> list | Model:
        '''Dispatch method to determine the appropriate decoding strategy.

        This method determines the appropriate decoding strategy based on the
        type of the JSON object. If the object is a list, it calls solve_list;
        if it is a dictionary, it calls parse_dict; otherwise, it raises a
        TypeError.
        '''
        if isinstance(obj, list):
            return ModelDecoder._solve_list(obj)
        elif isinstance(obj, dict):
            return ModelDecoder._parse_dict(obj)
        else:
            raise TypeError(f'Unexpected Type {type(obj)}')

    @staticmethod
    def _parse_dict(dc: dict) -> Model:
        '''Parse a dictionary representing a JSON object into a Model object.

        This method parses a dictionary representing a JSON object into a Model
        object.
        '''
        if 'type' not in dc:
            raise KeyError('Expected key "type" not found.')

        cls = decoder_mapping.get(dc['type'], None)
        if cls is None:
            raise KeyError(
                '"type" field found, but is not a valid Model subtype.'
                f' Found {{"type": {dc["type"]}}}')

        dc.pop('type')
        return cls(**dc)

    @staticmethod
    def _solve_list(ls: list) -> list:
        return [ModelDecoder._dispatch(el) for el in ls]

    def decode(self, s: str, *args, **kwargs) -> Any:
        default = super().decode(s)

        return ModelDecoder._dispatch(default)
