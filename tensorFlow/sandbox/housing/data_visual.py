import numpy as np
from sklearn.model_selection import StratifiedShuffleSplit

def add_income_category(housing):
  """
  Divide the median income by 1.5 (to limit the number of categories), and round up using ceil
  (to have discrete categories), and then merge all the categories greater than 5 into category 5
  """
  housing["income_cat"] = np.ceil(housing["median_income"] / 1.5)
  housing["income_cat"].where(housing["income_cat"] < 5, 5.0, inplace=True)

def shuffled_split(housing):
  add_income_category(housing)
  split = StratifiedShuffleSplit(n_splits=1, test_size=0.2, random_state=42)
  for train_index, test_index in split.split(housing, housing["income_cat"]):
    strat_train_set = housing.loc[train_index]
    strat_test_set = housing.loc[test_index]
  strat_test_set["income_cat"].value_counts() / len(strat_test_set)

  for set_ in (strat_train_set, strat_test_set):
    set_.drop("income_cat", axis=1, inplace=True)

  return strat_train_set, strat_test_set

# Visualize
# housing = strat_train_set.copy()
# housing.plot(kind="scatter", x="longitude", y="latitude", alpha=0.1)
# 
# s - radius of each circle which represents the district's population
# c - price
# cmap - predefined color map called jet
# housing.plot(kind="scatter", x="longitude", y="latitude", alpha=0.4, s=housing["population"]/100, label="population",
#               figsize=(10,7), c="median_house_value", cmap=plt.get_cmap("jet"), colorbar=True,)
#
# standard correlation coefficient (a.k.a Pearson's r) between every pair of attributes using the corr method
# corr_matrix = housing.corr()
# from pandas.plotting import scatter_matrix
# attributes = ["median_house_value", "median_income", "total_rooms", "housing_median_age"]
# scatter_matrix(housing[attributes], figsize=(12,8))
#
# housing.plot(kind="scatter", x="median_income", y="median_house_value", alpha=0.1)
# 

