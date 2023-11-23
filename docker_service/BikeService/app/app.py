from flask import Flask
import NNBike
import QBike
import numpy as np


app = Flask(__name__)

nnbike = NNBike.NNBike()
nnbike.load_model('model_jo.h5')

qbike = QBike.QBike()
qbike.load_model("QLearningV5.Qtable")

@app.route('/')
def hello_world():
    print("hello worldben vagyok")
    return 'Hello, World!'

#define a function that takes a map and returns the next step
@app.route('/nnstep/<map>')
def nnstep(map):
    #convert the map to a list of ints
    map = map.split(',')
    submap = np.array([[int(map[0]), int(map[1]), int(map[2])],
                       [int(map[3]), int(map[4]), int(map[5])],
                       [int(map[6]), int(map[7]), int(map[8])]])

    #get the next step
    step = nnbike.step(submap)
    return str(step)

@app.route('/qstep/<map>')
def qstep(map):
    #convert the map to a list of ints
    map = map.split(',')
    submap = np.array([int(map[0]), int(map[1]), int(map[2]),
                       int(map[3]), 0, int(map[5]),
                       int(map[6]), int(map[7]), int(map[8])])

    #get the next step
    step = qbike.step(submap)
    return str(step)

@app.route('/get_bike_models')
def get_bike_models():
    return ["nnstep", "qstep"]

@app.route('/health')
def health():
    return '', 200

@app.route('/ready')
def ready():
    return '', 200

if __name__ == '__main__':
    app.run(host='0.0.0.0')