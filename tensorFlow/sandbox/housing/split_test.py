import hashlib
import numpy as np
from sklearn.model_selection import train_test_split

# Use each instance's identifier to decide whether or not it should go in the test set.
# Compute a hash, keep only the last byte of the hash, and put the instance's identifier,
# keep only the last byte of the hash, and put the instance in the test setif this value is lower
# or equal to 51 (~20% of 256)

def test_set_check(identifier, test_ratio, hash):
  return hash(np.int64(identifier)).digest()[-1] < 256 * test_ratio

def split_train_test_by_id(data, test_ratio, id_column, hash=hashlib.md5):
  ids = data[id_column]
  in_test_set = ids.apply(lambda id_: test_set_check(id_, test_ratio, hash))
  return data.loc[~in_test_set], data.loc[in_test_set]

def housing_default_split(housing):
  housing_with_id["id"] = housing["longitude"] * 1000 + housing["latitude"] # create an id
  train_set, test_set = split_train_test_by_id(housing_with_id, 0.2, "id")
  return train_set, test_set

def sklearn_default_split(housing):
  train_set, test_set = train_test_split(housing, test_size=0.2, random_state=42)
  return train_set, test_set
