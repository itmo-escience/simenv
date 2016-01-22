package itmo.escience.simenv.algorithms.watchmakerTest

import java.util
import java.util.Random

import org.uncommons.maths.random.{MersenneTwisterRNG, Probability}
import org.uncommons.watchmaker.framework.factories.StringFactory
import org.uncommons.watchmaker.framework.operators.{EvolutionPipeline, StringMutation, StringCrossover}
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection
import org.uncommons.watchmaker.framework._
import org.uncommons.watchmaker.framework.termination.TargetFitness

/**
  * Created by mikhail on 21.01.2016.
  */
object wtest {
  val chars: Array[Char] = Array('i', 'd', 'n', 'a', 'h', 'o', 'y', ' ')
  val factory: CandidateFactory[String] = new StringFactory(chars, 11)

  val operators: util.List[EvolutionaryOperator[String]] = new util.LinkedList[EvolutionaryOperator[String]]()
  operators.add(new StringCrossover())
  operators.add(new StringMutation(chars, new Probability(0.02)))

  val pipeline: EvolutionaryOperator[String] = new EvolutionPipeline[String](operators)


  class StringEvaluator extends FitnessEvaluator[String] {
    val targetString : String  = "idi na hooy"
    def getFitness(t: String, list: util.List[_ <: String]): Double = {
      var matches: Int = 0
      for (i <- 0 until t.length()) {
        if (t.charAt(i) == targetString.charAt(i)) {
          matches += 1
        }
      }
      matches
    }

    def isNatural: Boolean = true
  }

  val selector: SelectionStrategy[Object] = new RouletteWheelSelection()

  val rng: Random = new MersenneTwisterRNG()

  val fitnessEvaluator: FitnessEvaluator[String] = new StringEvaluator()

  val  engine: EvolutionEngine[String] = new GenerationalEvolutionEngine[String](factory,
      pipeline,
      fitnessEvaluator,
      selector,
      rng)

  engine.addEvolutionObserver(new EvolutionObserver[String]()
  {
    def populationUpdate(data :PopulationData[_ <: String]) =
    {
      println(s"Generation ${data.getGenerationNumber}: ${data.getBestCandidate}\n")
    }
  })

  def main(args: Array[String]) {
    val result = engine.evolve(10, 1, new TargetFitness(11, true))
    println(result)
  }

}
