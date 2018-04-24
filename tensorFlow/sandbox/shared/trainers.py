import numpy as np

from sklearn.model_selection import cross_val_score

from sklearn.linear_model import LinearRegression
from sklearn.tree import DecisionTreeRegressor
from sklearn.ensemble import RandomForestRegressor

def linear_regression(model, label):
  lin_reg = LinearRegression()
  lin_reg.fit(model, label)
  return lin_reg

def decision_tree_regression(model, label):
  tree_reg = DecisionTreeRegressor()
  tree_reg.fit(model, label)
  return tree_reg

def random_forest_regressor(model, label):
  """Random Forest works by training many Decision Trees on random subset of the features,
  then averaging out their predictions. Building a model on top of many other models is called 
  Ensemble Learning, and it is often a great way to push ML algorithms even further.
  """
  forest_reg = RandomForestRegressor()
  forest_reg.fit(model, labels)

def cross_score(tree_reg, model, label, scoring, cv):
  """Performs K-fold cross validation: randomly splits the training set into cv (i.e. 10)
  distinct subsets called folds, then it trains and evaluates the Decision Tree 
  model cv times, picking a different fold for evaluation every time and training 
  on the other 9 folds. The result is an array containing the cv evaluation scores.
  """
  scores = cross_val_score(tree_reg, model, label, scoring=scoring, cv=cv)
  tree_rmse_scores = np.sqrt(-scores)
