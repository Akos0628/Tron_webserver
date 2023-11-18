import numpy as np
import tensorflow as tf
import numpy as np
import pandas as pd
import random

UP = 0
LEFT = 1
DOWN = 2
RIGHT = 3

def get_map_around(map, x, y):
    map = np.array(map)
    
    m = map[x-1:x+2, y-1:y+2].reshape(9)
    return m

class NNBike():
    def __init__(self, epoch = 150):
        self.x = 10
        self.y = 10
        self.input_shape = (3, 3, 1)
        self.num_actions = 4  # Number of possible actions
        self.epochs = epoch
        
        #basic cnn model the input is 3*3*1 matrix, the output is 4*1 matrix
        self.model = tf.keras.Sequential([
            tf.keras.layers.Conv2D(3, (1, 1), padding='same', activation='relu', input_shape=self.input_shape),
            tf.keras.layers.Flatten(),
            tf.keras.layers.Dense(self.num_actions, activation='softmax'),
        ])
        self.loss_fn = tf.keras.losses.MeanSquaredError()
        self.optimizer = tf.keras.optimizers.SGD()

        self.model.compile(optimizer=self.optimizer,
              loss=self.loss_fn,
              metrics=['accuracy'])
        self.model.summary()
#
    def train(self, X, y, X_val, y_val):
        history = self.model.fit(X, y, epochs=self.epochs, validation_data=(X_val, y_val))
        return history

        
    #train function with validation data
    def train(self, X, y, X_val, y_val):
        history = self.model.fit(X, y, epochs=self.epochs, validation_data=(X_val, y_val))
        return history



    def predict(self, data):
        return self.model.predict(data, verbose=0)
    
    def step(self, map):
        m = np.array(map).reshape(1, 3, 3, 1)
        #all non 0 values to 1
        m[m != 0] = 1

        #predict the next step
        step = self.predict(m)
        print(step)
        step = np.argmax(step)

        return step
    
    def load_model(self, path):
        self.model = tf.keras.models.load_model(path)
