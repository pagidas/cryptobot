import dash
import dash_core_components as dcc
import dash_html_components as html
import plotly.express as px
from dash.dependencies import Input, Output


class Plotter:
    def __init__(self, bot):
        self.bot = bot

    def _update_data(self):
        self.df = self.bot.get_dataframe()

    def _make_graph_from(self, x="time", y="price"):
        # update data
        self._update_data()
        # make figure
        product_id = self.df.productId.values[0]
        self.fig = px.line(self.df, x=x, y=y, title=f"Price of {product_id}")

    def _make_demo_graph(self):
        # get data as Dataframe
        df = px.data.gapminder().query("country=='Greece'")

        # make figure
        self.fig = px.line(df, x="year", y="lifeExp", title='Life expectancy in Greece')

    def _make_app(self):
        if not self.fig:
            raise ValueError()

        self.app = dash.Dash()

        self.app.layout = html.Div(children=[
            html.H1(children="Welcome to cryptobot"),
            dcc.Graph(
                id='main-graph',
                figure=self.fig
            ),
            dcc.Interval("main-interval")
        ])

        @self.app.callback(
            Output(component_id="main-graph", component_property="figure"),
            Input(component_id="main-interval", component_property="n_intervals")
        )
        def _update_fig(_):
            self._make_graph_from()
            return self.fig

    def initiate_figure(self):
        self._make_graph_from()
        self._make_app()

    def run(self):
        self.app.run_server(host="0.0.0.0", port=8050, debug=True)
