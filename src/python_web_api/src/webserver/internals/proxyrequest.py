from dataclasses import dataclass
from enum import Enum
from typing import Any, Iterable, Optional

from webserver.internals import models
import json
import functools
import re


def change_case(key: str) -> str:
    '''Convert camel case to snake case
    Args:
        key (str): string in camel case

    Returns:
        str: string in snake case
    '''
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', key)
    return re.sub('([a-z0-9])([A-Z])', r'\1_\2', s1).lower()


class ProxyRequestType(Enum):
    CREATE = 'CREATE'
    READ = 'READ'
    UPDATE = 'UPDATE'
    DELETE = 'DELETE'

    @classmethod
    def from_str(clz, type_: str) -> 'ProxyRequestType':
        if type_.upper() == 'CREATE':
            return clz.CREATE
        elif type_.upper() == 'READ':
            return clz.READ
        elif type_.upper() == 'UPDATE':
            return clz.UPDATE
        elif type_.upper() == 'DELETE':
            return clz.DELETE
        else:
            raise ValueError(f'No ProxyRequestType for {type_ = }')


@dataclass
class ProxyRequestEncoder:
    type_: ProxyRequestType
    entity_type: models.Model
    limit: int = -1
    all_: bool = True

    def __post_init__(self):
        if not isinstance(self.type_, ProxyRequestType):
            raise TypeError('Invalid request type, must be a member of enum '
                            f'ProxyRequestType. Found {self.type_}')

    def encode(self, limit: Optional[int] = None,
               all_: Optional[bool] = None,
               entities: Optional[Iterable[str]] = None):
        limit = int(limit) if limit is not None else self.limit
        all_ = bool(all_) if all_ is not None else self.all_
        entities = tuple(entities or [])
        req = {
            "type": self.type_.value,
            "entityName": self.entity_type.__name__,
            "limit": limit,
            "requestingAllDetails": all_,
            "entities": entities,
        }

        return json.dumps(req)


class ProxyRequestDecoder:
    parse = functools.partial(json.loads, cls=models.ModelDecoder)

    def __init__(self, model_name: str):
        self.model_name = model_name

    def decode(self, request: str) -> dict[str, Any]:
        initial_parse = json.loads(request)
        if initial_parse['error'].strip():
            return {'error': True}

        for entity in initial_parse['entities']:
            original_keyset = set(entity.keys())

            # Switch from camel case to snake case
            for key in original_keyset:
                entity[change_case(key)] = entity.pop(key)

            # Add type field to parse as model
            entity['type'] = self.model_name
            if (_id := f'{self.model_name.lower()}_id') in entity:
                entity['ID'] = entity.pop(_id)

        entities = json.dumps(initial_parse['entities'])
        parsed = ProxyRequestDecoder.parse(entities)

        return {'data': parsed}
