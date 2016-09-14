import matplotlib.pyplot as plt
import numpy as np
import math
import random

class SparseVector:
	#uses nested dictionaries
	def __init__(self):
		self.my_data = {}
	def setMe(self, movie, user, rating):
		if self.my_data.has_key(movie):
			my_score = {}
			my_score[user] = rating	#add new pair to inner dictionary
			self.my_data[movie] = my_score
			return
		my_row = {}
		my_row[user] = rating		#add new dictionary and append
		self.my_data[movie]= my_row
		return
	# def setMe(self, point):	#point is a dictionary. one movie with multiple ratings
	# 	if self.my_data.has_key(point):
	# 		my_score = {}
	# 		for user in point:
	# 			my_score[user] = rating	#add new pair to inner dictionary
	# 		self.my_data[movie] = my_score
	# 		return
	# 	my_row = {}
	# 	my_row[user] = rating		#add new dictionary and append
	# 	self.my_data[movie]= my_row
	# 	return
	def getAverage(self):
		total = 0
		count = 0
		for movie in self.my_data:
			for user in movie:
				total += movie[user]
				count += 1
		return total/count

	# def getDistance(self, movie1, movie2):
	# 	dist = 0
	# 	count = 0
	# 	m1 = self.my_data[movie1]
	# 	m2 = self.my_data[movie2]
	# 	for person in m1:
	# 		if person in m2:
	# 			#dist += (math.pow(m1[person],2)-math.pow(m2[person],2))
	# 			dist += abs(m1[person]-m2[person])
	# 	distance = dist/count
	# 	return distance;

	def getMean(self, movie1, movie2):
		m1 = self.my_data[movie1]
		m2 = self.my_data[movie2]
		mean_movie = {}
		for person in m1:
			if person in m2:
				mean_rating = (m1[person] + m2[person])/2
			else:
				mean_rating = m1[person]
			mean_movie[person] = mean_rating
		for person in m2:
			if not person in mean_movie:
				mean_movie[person] = m2[person]
		return mean_movie

def getClosest(point, centroids):
	min_dist = float('inf')
	#print centroids.my_data
	for centroid in centroids.my_data:
		#print point, centroids.my_data[centroid]
		dist = getDistance(point, centroids.my_data[centroid])
		if dist < min_dist:
			min_dist = dist
			closest = centroid
	return closest

def getDistance(m1, m2):
	dist = 0
	count = 0
	connect = 0
	print m1;
	for person in m1:
		if person in m2:
			#dist += (math.pow(m1[person],2)-math.pow(m2[person],2))
			dist += abs(m1[person]-m2[person])
			connect = 1
	if connect:
		distance = dist/count
	else:
		distance = 1000
	return distance;

def cluster(points, k):
	centroids = SparseVector()
	for i in range(k):
		movie = random.choice(points.my_data.keys())
		human = random.choice(points.my_data[movie].keys())
		rating = points.my_data[movie][human]
		centroids.setMe(movie, human, rating)
	domains = {}
	while 1 == 1:
		for point in points.my_data:
		 	my_centroid = getClosest(points.my_data[point], centroids)
			if domains.has_key(my_centroid):
				for human in points.my_data[point]:
					rating = points.my_data[point][human]
					domains[my_centroid].setMe(point,human,rating)
			else:
				domain = SparseVector()
				for human in points.my_data[point]:
					rating = points.my_data[point][human]
					domain.setMe(point, human, rating)
				domains[my_centroid] = domain


		#for centroid in centroids.mydata:
			#update each centroid


def loadDataset(filename='u.data'):
	in_data = np.genfromtxt(filename)
	#movie person rating
	#initialize sparse vector
	movies = SparseVector()
	for row in in_data:
		movies.setMe(row[0],row[1],row[2])
	return movies

if __name__=="__main__":
	movies = loadDataset()
	x = cluster(movies, 10)
