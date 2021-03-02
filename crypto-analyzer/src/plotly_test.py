import plotly.graph_objects as go

import pandas as pd
from sklearn.linear_model import LinearRegression

import dash
import dash_core_components as dcc
import dash_html_components as html
from dash.dependencies import Input, Output
import numpy as np
from scipy import interpolate

reg = LinearRegression()
# Load data
df = pd.read_csv(
    "https://raw.githubusercontent.com/plotly/datasets/master/finance-charts-apple.csv")
df.columns = [col.replace("AAPL.", "") for col in df.columns]

# Create figure
fig = go.Figure()

forecasting = df.High[80:100]
reg.fit(forecasting.index.values.reshape(-1,1), forecasting.values)
# draw a line based on the slope and intercept of the forecasting
abline = [reg.coef_[0] * i + reg.intercept_ for i in forecasting.index.values]
# f = interpolate.interp1d(np.arange(20, 40), abline, kind='linear')
# xnew = np.arange(20, 39, 0.1)

fig.add_traces([go.Scatter(x=list(df.Date), y=list(df.High), name='current price'),

                go.Scatter(x=list(df.Date[20:40]), y=list(forecasting), line=dict(width=3, color='red'), name='forecasting'),
                go.Scatter(x=list(df.Date[20:40]), y=abline, line=dict(width=5, color='black'), name='linear-fit'),
                # go.Scatter(x=list(df.Date[20:40]), y=f(xnew), line=dict(width=5, color='blue'), name='linear-fit'),
                go.Indicator(mode="number+delta",
                             value=reg.coef_[0],
                             number={"font": {"size": 35, "color":"red"}, "suffix":"%"},
                             title={"text": "Trend Slope", "font":{"size": 35, "color":"black"}},
                             domain={'x': [0.75, 1], 'y': [0.75, 1]}
                            )
                ])

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
