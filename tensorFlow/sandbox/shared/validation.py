from sklearn.model_selection import cross_val_predict
from sklearn.metrics import confusion_matrix

def confusion_matrix(stochastic_gradient_descent_classifier, model, labels, cv):
  """
  Confusion matrix counts the number of times instances of class A are classified as class B in its matrix.
  """
  train_pred = cross_val_predict(stochastic_gradient_descent_classifier, model, labels, cv=cv)
  confusion_matrix(labels, train_pred)
