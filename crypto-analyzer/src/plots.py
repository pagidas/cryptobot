import dash
import dash_core_components as dcc
import dash_html_components as html
import plotly.express as px


class Plotter:
    def __init__(self):
        self.fig = None
        self.app = None

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
            )
        ])

    def run(self):
        self._make_demo_graph()
        self._make_app()
        self.app.run_server(host="0.0.0.0", port=8050, debug=True)
