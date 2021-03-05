import dash
import dash_core_components as dcc
import dash_html_components as html
import consume.listeners as listeners
from dash.dependencies import Input, Output
from model.models import CoinbaseProduct
from rethinkdb.errors import ReqlTimeoutError


class PlotlyPlotter:
    def __init__(self):
        self.feed = listeners.get_coinbase_message_feed()
        self.app = dash.Dash()

    def run_dashboard(self):
        # TODO pass a config object
        self.app.layout = html.Div(children=[
            html.H1(children="Welcome to cryptobot's plotter"),
            html.Div(id="output-par"),
            dcc.Interval(
                id="interval",
                interval=500
            )
        ])

        @self.app.callback(
            Output(component_id="output-par", component_property="children"),
            Input(component_id="interval", component_property="n_intervals")
        )
        def update_dash_app(interval_number):
            try:
                change = self.feed.next(wait=False)["new_val"]
                coinbase_product = CoinbaseProduct.from_dict(change)

                return coinbase_product.price
            except ReqlTimeoutError as e:
                print(interval_number, e)

        self.app.run_server(host="0.0.0.0", port=8081, debug=True)

