from sklearn.pipeline import Pipeline
from sklearn.preprocessing import Imputer, StandardScaler
from sklearn.base import BaseEstimator, TransformerMixin
from sklearn.linear_model import LinearRegression
from sklearn.tree import DecisionTreeRegressor

rooms_ix, bedroomx_ix, population_ix, household_ix = 3, 4, 5, 6

class CombinedAttributesAdder(BaseEstimator, TransformerMixin):
  def __init__(self, add_bedrooms_per_room = True):
    self.add_bedrooms_per_room = add_bedrooms_per_room
  def fit(self, X, y=None):
    return self
  def transform(self, X, y=None):
    rooms_per_household = X[:, rooms_ix] / X[:, household_ix]
    population_per_household = X[:, population_ix] / X[:, household_ix]
    if self.add_bedrooms_per_room:
      bedrooms_per_room = X[:, bedroomx_ix] / X[:, rooms_ix]
      return np.c_[X, rooms_per_household, population_per_household, bedrooms_per_room]
    else:
      return np.c_[X, rooms_per_household, population_per_household]

#attr_adder = CombinedAttributesAdder(add_bedrooms_per_room=False)
#housing_extra_attribs = attr_adder.transform(housing.values)

def pipeline(housing):
  housing_num = housing.drop("ocean_proximity", axis=1)
  num_pipeline = Pipeline([
      ('imputer', Imputer(strategy="median")),
      ('attribs_adder', CombinedAttributesAdder()),
      ('std_scaler', StandardScaler)
    ])
  housing_num_tr = num_pipeline.fit_transform(housing_num)
  housing_num_tr

def linearRegression(model, label):
  lin_reg = LinearRegression()
  lin_reg.fit(model, label)
  return lin_reg

def decisionTreeRegression(model, label):
  tree_reg = DecisionTreeRegressor()
  tree_reg.fit(model, label)
  return tree_reg