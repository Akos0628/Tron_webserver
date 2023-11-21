import random
import numpy as np
import pickle
import GameEnv

UP = 0
LEFT = 1
DOWN = 2
RIGHT = 3


class QBike:
	def __init__(self, x=10, y=10):
		self.x = x
		self.y = y
		self.prev_reward = 0
		self.num_states = 9  # Number of possible states 
		self.num_actions = 4  # Number of possible actions
		self.state = tuple(np.zeros(self.num_states, dtype=int))
		self.Q_table = np.zeros((2,2,2,2,2,2,2,2,2,4))
		self.isalive = True
		self.learning_rate = 0.05
		self.discount_factor = 0.98
		self.exploration_prob = 0.75
		self.exploration_prob_min = 0.01
		self.num_episodes = 10000
		self.score = []
		self.env = None
		self.randstep = False
		print('BikeAgent init')

	def choose_action(self, state):
		if random.uniform(0, 1) < self.exploration_prob:
			action = random.choice(range(self.num_actions))
			self.randstep = True
			return action 
		else:
			self.randstep = False
			return np.argmax(self.Q_table[state])
		
	def get_state(self):
		state = self.env.get_map_around(self.x, self.y)
		if(state[4] > 0):
			self.isalive= False
		state = tuple(1 if item != 0 else 0 for item in state)
		return state

	def get_next_state(self, action):
    	#Logic to determine the next state based on the action.
		self.x, self.y
		if  action == 0:  	# UP
			self.y = self.y + 1
		elif action == 1:  	# LEFT
			self.x = self.x - 1
		elif action == 2:  	# DOWN
			self.y = self.y - 1
		else: 				# RIGHT
			self.x = self.x + 1
		return self.get_state()

	
	def get_reward(self):
    	# Logic to calculate the reward for the action.
		if not self.isalive:
			return -20
		else:
			if self.randstep:
				self.prev_reward = self.prev_reward + 2
				return 2
			else:
				self.prev_reward = self.prev_reward + 1
				return 1

	def step(self, state):
		state = tuple(1 if item != 0 else 0 for item in state)
		return np.argmax(self.Q_table[state])

	
	def load_model(self, path):
		with open(path, 'rb') as f:
			self.Q_table = pickle.load(f)
		
	def train(self):
		
		for i in range(1, self.num_episodes + 1):
			self.x = random.randint(1, 24)
			self.y= random.randint(1, 44)
			self.prev_reward  = 0			#also length
			self.env  = GameEnv.GameEnv()
			self.isalive=True

            # print updates
			if i % 25 == 0:
				print(f"Episodes: {i}, score: {np.mean(self.score)}, eps: {self.exploration_prob}, lr: {self.learning_rate}")
				self.score = []
               
            # occasionally save latest model
			if (i < 500 and i % 50 == 0) or (i >= 500 and i < 1000 and i % 100 == 0) or (i >= 1000 and i % 500 == 0):
				with open(f'states/{i}.Qtable', 'wb') as file:
					pickle.dump(self.Q_table, file)
                
			self.exploration_prob = max(self.exploration_prob * self.discount_factor, self.exploration_prob_min)
			current_state = self.get_state()
			while self.isalive:
                # choose action and take it
				action = self.choose_action(current_state)
				next_state = self.get_next_state(action)
				reward = self.get_reward()
                # Bellman Equation Update
				self.Q_table[current_state][action] = (1 - self.learning_rate)\
				* self.Q_table[current_state][action] + self.learning_rate\
					* (reward + self.discount_factor * max(self.Q_table[next_state])) 
				current_state = next_state
				self.state = current_state
				self.env.update_map(self, action)
			if not self.isalive:
				print(i,"\tDead in: ", self.prev_reward, " steps")
            # keep track of important metrics
			self.score.append(self.prev_reward)
	
	def run(self):
		for i in range(1, self.num_episodes + 1):
			self.x = random.randint(1, 24)
			self.y= random.randint(1, 44)
			self.env  = GameEnv.GameEnv()
			self.isalive=True
			current_state = self.get_state()
			while self.isalive:
                # choose action and take it
				action = self.step(current_state)
				print("step_", action)
				next_state = self.get_next_state(action)
				current_state = next_state
				self.state = current_state
				self.env.update_map(self, action)
