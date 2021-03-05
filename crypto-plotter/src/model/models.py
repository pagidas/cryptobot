from dataclasses import dataclass
from dataclasses_json import dataclass_json


@dataclass_json
@dataclass
class CoinbaseProduct:
    """
    Class that encapsulates the coinbase product messages.
    """
    type: str
    sequence: str
    product_id: str
    price: str
    open_24h: str
    volume_24h: str
    low_24h: str
    high_24h: str
    volume_30d: str
    best_bid: str
    best_ask: str
    side: str
    time: str
    trade_id: str
    last_size: str
