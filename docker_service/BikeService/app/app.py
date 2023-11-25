from flask import Flask
import NNBike
import QBike
import numpy as np


app = Flask(__name__)

nnbike = NNBike.NNBike()
nnbike.load_model('model_jo.h5')

qbikeV5 = QBike.QBike()
qbikeV5.load_model("QLearningV5.Qtable")
qbikeV2 = QBike.QBike()
qbikeV2.load_model("QLearningV2-Funky.Qtable")

@app.route('/nnstep/<map>')
def nnstep(map):
    map = map.split(',')
    submap = np.array([[int(map[0]), int(map[1]), int(map[2])],
                       [int(map[3]), int(map[4]), int(map[5])],
                       [int(map[6]), int(map[7]), int(map[8])]])

    step = nnbike.step(submap)
    return str(step)

@app.route('/qstep/<map>')
def qstep(map):
    map = map.split(',')
    submap = np.array([int(map[0]), int(map[1]), int(map[2]),
                       int(map[3]), 0, int(map[5]),
                       int(map[6]), int(map[7]), int(map[8])])

    step = qbikeV5.step(submap)
    return str(step)

@app.route('/qstep_funky/<map>')
def funkystep(map):
    map = map.split(',')
    submap = np.array([int(map[0]), int(map[1]), int(map[2]),
                       int(map[3]), 0, int(map[5]),
                       int(map[6]), int(map[7]), int(map[8])])

    step = qbikeV2.step(submap)
    return str(step)

@app.route('/get_bike_models')
def get_bike_models():
    return ["nnstep", "qstep", "qstep_funky"]

@app.route('/health')
def health():
    return '', 200

@app.route('/ready')
def ready():
    return '', 200

if __name__ == '__main__':
    app.run(host='0.0.0.0')