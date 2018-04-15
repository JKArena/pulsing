from sklearn.preprocessing import Imputer

def populate_additional_features(housing):
  housing["rooms_per_household"] = housing["total_rooms"]/housing["households"]
  housing["bedrooms_per_room"] = housing["total_bedrooms"]/housing["total_rooms"]
  housing["population_per_household"] = housing["population"]/housing["households"]

  # since total_bedrooms attribute has some missing values
  # housing.drop("total_bedrooms", axis=1) drop the feature
  median = housing["total_bedrooms"].median() # use the median to fill the empty values
  housing["total_bedrooms"].fillna(median, inplace=True)

  imputer = Imputer(strategy="median") # takes care of missing values using median of the attribute
  housing_num = housing.drop("ocean_proximity", axis=1) # median can be computed on numerical attribute, so create a copy w/o text attribute

  imputer.fit(housing_num)

  imputer.statistics_
  housing_num.median().values

  X = imputer.transform(housing_num)
  # Now use this "trained" imputer to transform the training set by replacing missing values by the learned medians
  return X


# corr_matrix = housing.corr()
# corr_matrix["median_house_value"].sort_values(ascending=False)
