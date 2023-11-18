from flask import Flask
import NNBike
import numpy as np
import pip
import sys



app = Flask(__name__)

bike = NNBike.NNBike()
bike.load_model('model_jo.h5')


@app.route('/')
def hello_world():
    print("hello worldben vagyok")
    return 'Hello, World!'

#define a function that takes a map and returns the next step
@app.route('/step/<map>')
def step(map):
    #convert the map to a list of ints
    map = map.split(',')
    submap = np.array([[int(map[0]), int(map[1]), int(map[2])],
                       [int(map[3]), int(map[4]), int(map[5])],
                       [int(map[6]), int(map[7]), int(map[8])]])

    #get the next step
    step = bike.step(submap)
    return str(step)


if __name__ == '__main__':
    app.run(host='0.0.0.0')