import plots
import cryptobot_api


bot = cryptobot_api.initialize_cryptonator()
plotter = plots.Plotter(bot)
plotter.initiate_figure()
plotter.run()
