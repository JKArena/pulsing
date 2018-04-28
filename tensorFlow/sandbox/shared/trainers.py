import numpy as np

from sklearn.model_selection import cross_val_score

from sklearn.linear_model import LinearRegression
from sklearn.tree import DecisionTreeRegressor
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import GridSearchCV

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

def grid_search_cv(forest_reg, param_grid, cv, scoring):
  """One way to fine tune a model would be to fiddle with the hyperparameters manually, until you find 
  a great combination of hyperparameter values. This would be very tedious work, so instead use GridSearchCV for 
  the search. Need to tell it which hyperparameters you want it to experiement with, what values to try out, and 
  it will evaluate all the possible combinations of hyperparameter values, using cross-validation.
  param_grid = [
    {'n_estimators': [3, 10, 30], 'max_features': [2, 4, 6, 8]},
    {'bootstrap': [False], 'n_estimators': [3, 10], 'max_features': [2, 3, 4]}
  ]
  Note grid search approach is fine when exploring relatively few combinations, but when hyperparameter search space 
  is large, it is often preferable to use RandomizedSearchCV instead. Instead of trying out all possible combinations,
  it evaluates a given number of random combinations by selecting a random value for each hyperparameter at every 
  iteration
  """
  grid_search = GridSearchCV(forest_reg, param_grid, cv=cv, scoring=scoring)

def cross_score(tree_reg, model, label, scoring, cv):
  """Performs K-fold cross validation: randomly splits the training set into cv (i.e. 10)
  distinct subsets called folds, then it trains and evaluates the Decision Tree 
  model cv times, picking a different fold for evaluation every time and training 
  on the other 9 folds. The result is an array containing the cv evaluation scores.
  """
  scores = cross_val_score(tree_reg, model, label, scoring=scoring, cv=cv)
  tree_rmse_scores = np.sqrt(-scores)
