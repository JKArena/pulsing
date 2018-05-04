from sklearn.datasets import fetch_mldata

def fetch_mnist():
  mnist = fetch_mldata('MNIST original')
  return mnist
