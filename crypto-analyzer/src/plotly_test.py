import plotly.graph_objects as go

import pandas as pd

import dash
import dash_core_components as dcc
import dash_html_components as html
from dash.dependencies import Input, Output

# Load data
df = pd.read_csv(
    "https://raw.githubusercontent.com/plotly/datasets/master/finance-charts-apple.csv")
df.columns = [col.replace("AAPL.", "") for col in df.columns]

# Create figure
fig = go.Figure()

fig.add_traces([go.Scatter(x=list(df.Date), y=list(df.High)),

                go.Scatter(x=list(df.Date[20:40]), y=list(df.High[40:60]), line=dict(width=5, color='red'))])
# Set title
fig.update_layout(
    title_text="Time series with range slider and selectors"
)

# Add range slider
fig.update_layout(
    xaxis=dict(
        rangeselector=dict(
            buttons=list([
                dict(count=1,
                     label="1m",
                     step="month",
                     stepmode="backward"),
                dict(count=6,
                     label="6m",
                     step="month",
                     stepmode="backward"),
                dict(count=1,
                     label="YTD",
                     step="year",
                     stepmode="todate"),
                dict(count=1,
                     label="1y",
                     step="year",
                     stepmode="backward"),
                dict(step="all")
            ])
        ),
        # rangeslider=dict(
        #     visible=True
        # ),
        type="date"
    )
)

app = dash.Dash()
app.layout = html.Div([
    dcc.Graph(figure=fig, id='live-update-graph'),
    dcc.Interval(
            id='interval-component',
            interval=1000,  # in milliseconds
            n_intervals=21 # number of interval the plot will start
    )
])


@app.callback(Output('live-update-graph', 'figure'),
              Input('interval-component', 'n_intervals'))
def update_graph_live(n):
    # print("T")
    with fig.batch_update():
        fig.data[0].y = df.High.values[:n]
        # fig.data[0].x = df.Date.values[:n]
        # fig.data[1].y = df.High.values[n:n+20]
        # fig.data[2].x = df.Date.values[n:n+20]
    # fig.update_layout(
    #     title_text=fig['layout'].title.text+"a"
    # )
    # fig.update(data=[{'y': [4, 5, 6]}])
    return fig
# fig.show()


# WARNING: for development purposes only use a different server for production
app.run_server(host="0.0.0.0", port=8050, debug=True)
