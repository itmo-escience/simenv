package itmo.escience.Environment

import itmo.escience.Environment.{ResourceManager, Estimator}

/**
  * Created by Mishanya on 24.11.2015.
  */
class Environment() {

  var resourceManager: ResourceManager = _
  var estimator: Estimator = _

  def setResourceManager(resourceManager: ResourceManager) = {
    this.resourceManager = resourceManager
  }

  def setEstimator(estimator: Estimator) = {
    this.estimator = estimator
  }

  def checkCompleteness(): Boolean = {
    return (resourceManager != null) && (estimator != null)
  }

}
