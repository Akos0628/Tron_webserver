import numpy as np
import pygame
import random

UP = 0
LEFT = 1
DOWN = 2
RIGHT = 3

class Map():
    def __init__(self, n, m):
        self.map = self.generate_map(n, m)
        self.n = n
        self.m = m
        self.map = self.draw_walls(self.map)
    
    def get_map(self):
        return self.map
    
    def draw_walls(self, map):
        n = len(map)
        m = len(map[0])
        num_of_walls = random.randint(5, 25)
        for nw in range(num_of_walls):
            i = random.randint(1, n-1)
            j = random.randint(1, m-1)
            map[i][j] = 1
            length = random.randint(3, 30)
            direction = random.randint(0, 3)
            for length in range(length):
                if random.random() < 0.05:
                    direction = random.randint(0, 3)

                if direction == 0:
                    if i == 0 or map[i-1][j] == 1:
                        break
                    map[i-1][j] = 1
                    i -= 1
                elif direction == 1:
                    if i == n - 1 or map[i+1][j] == 1:
                        break
                    map[i+1][j] = 1
                    i += 1
                elif direction == 2:
                    if j == 0 or map[i][j-1] == 1:
                        break
                    map[i][j-1] = 1
                    j -= 1
                else:
                    if j == m - 1 or map[i][j+1] == 1:
                        break
                    map[i][j+1] = 1
                    j += 1
        return map 


    def generate_map(self, n, m):
        map = []
        for i in range(n):
            map.append([])
            for j in range(m):
                if i == 0 or i == n-1 or j == 0 or j == m-1:
                    map[i].append(1)
                else:
                    map[i].append(0)
        return map


class GameEnv:
    def __init__(self):  
        self.map = Map(25, 45).get_map()
        self.map = np.pad(self.map, (1,1), 'constant', constant_values=1)
        pygame.init()
        self.screen = pygame.display.set_mode((1200, 600))
        pygame.display.set_caption('Tron')
        self.draw_map(self.map, self.screen)
        self.num_states = 9 

    def get_map(self):
        return self.map

    def draw_map(self, map, screen):
        n = len(map)
        m = len(map[0])
        for i in range(n):
            for j in range(m):
                if map[i][j] == 1: #wall
                    pygame.draw.rect(screen, (0, 0, 0), (j*20, i*20, 20, 20))
                elif map[i][j] == 2: #player
                    pygame.draw.rect(screen, (255, 0, 0), (j*20, i*20, 20, 20))
                elif map[i][j] == 0: #empty
                    pygame.draw.rect(screen, (255, 255, 0), (j*20, i*20, 20, 20))
        pygame.display.update()
    
    def get_map_around(self, x, y):        
        map = np.array(self.map)                  
        m = map[x-1:x+2, y-1:y+2].reshape(9)         
        return m


    def update_map(self, bike, dir):
        map = self.step(self.map, bike, dir)
        #draw map
        self.draw_map(map, self.screen)
        #pygame.time.delay(10)

    def step(self, map, bike, direction):
        if direction == UP:
            map[bike.x][bike.y] = 2
        elif direction == DOWN:
            map[bike.x][bike.y ] = 2
        elif direction == LEFT:
            map[bike.x][bike.y] = 2
        elif direction == RIGHT:
            map[bike.x][bike.y] = 2
        return map

