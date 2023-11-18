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
    submap = np.array([[map[0], map[1], map[2]],
                [map[3], map[4], map[5]],
                [map[6], map[7], map[8]]])

    submap[submap != '0'] = 1
    #get the next step
    step = bike.step(submap)
    return str(step)


if __name__ == '__main__':
    app.run()